import java.util.*;
import java.io.*;

public class MyJuiceOrPlantDrink {

    private static final Scanner scanner = new Scanner(System.in);
    private static final java.util.Date DATE = new java.util.Date();

    public MyJuiceOrPlantDrink() {
    }

    public void juiceOrPlantBill(String regNumber) {

    }

    private static class Constants {
        public static final String BILL_DIR = "cafe/CafeBills";
        public static final String BILL_FILE = BILL_DIR + "/BillJuiceOrPlant.txt";
        public static final String MENU_HEADER = "\n===== JUICE / PLANT DRINK MENU =====";
        public static final String MENU_FOOTER = "0. Nothing / Go Back\n===================================";
    }

    enum JuiceItem {
        MANGO("Mango Flavour", 50),
        ORANGE("Orange Flavour", 40),
        PINEAPPLE("Pineapple Flavour", 70),
        GRAPE("Grape Flavour", 50),
        MINERAL_WATER("Mineral Water", 30);

        final String displayName;
        final int price;

        JuiceItem(String name, int price) {
            this.displayName = name;
            this.price = price;
        }

        static JuiceItem fromChoice(int choice) {
            if (choice < 1 || choice > values().length) return null;
            return values()[choice - 1];
        }
    }

    static class JuiceOrder {
        final JuiceItem juice;
        final int quantity;

        JuiceOrder(JuiceItem juice, int quantity) {
            this.juice = juice;
            this.quantity = quantity;
        }

        int getTotal() {
            return quantity * juice.price;
        }

        String getBillLine() {
            return String.format("%-25s %3d    %6d", juice.displayName, quantity, getTotal());
        }
    }

    public int JuiceORPlantbill() {
        List<JuiceOrder> orders = new ArrayList<>();
        int total = 0;

        displayMenu();

        while (true) {
            int choice = readInt("Enter Your Choice (0 to finish): ");
            if (choice == 0) break;

            JuiceItem selected = JuiceItem.fromChoice(choice);
            if (selected == null) {
                System.out.println("Invalid choice!");
                continue;
            }

            int quantity = readInt("Enter quantity: ");
            if (quantity <= 0) {
                System.out.println("Quantity must be positive!");
                continue;
            }

            JuiceOrder order = new JuiceOrder(selected, quantity);
            orders.add(order);
            total += order.getTotal();

            System.out.printf("✓ Added: %s x %d = %d TK\n", selected.displayName, quantity, order.getTotal());
            System.out.println("Current total: " + total + " TK");
        }

        if (!orders.isEmpty()) {
            BillFacade.saveBill(orders);
            System.out.println("\n✅ Juice/Plant Drinks Bill Generated. Total: " + total + " TK");
        }

        return total;
    }

    private void displayMenu() {
        System.out.println(Constants.MENU_HEADER);
        JuiceItem[] items = JuiceItem.values();
        for (int i = 0; i < items.length; i++) {
            System.out.printf("%d. %s (%d Rs)\n", i + 1, items[i].displayName, items[i].price);
        }
        System.out.println(Constants.MENU_FOOTER);
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input!");
            }
        }
    }

    // ==========================
    // FACADE DESIGN PATTERN
    // ==========================

    // Facade class that simplifies file operations
    private static class BillFacade {

        // Simple one-method interface for saving bills
        public static void saveBill(List<JuiceOrder> orders) {
            if (orders == null || orders.isEmpty()) {
                System.out.println("No orders to save");
                return;
            }

            // The facade handles all the complexity internally
            FileOperator fileOp = new FileOperator();
            DirectoryManager dirManager = new DirectoryManager();
            BillFormatter formatter = new BillFormatter();

            // Ensure directory exists
            dirManager.ensureDirectoryExists(Constants.BILL_DIR);

            // Format the bill content
            String billContent = formatter.formatOrders(orders);

            // Save to file
            fileOp.appendToFile(Constants.BILL_FILE, billContent);
        }

        // Optional: Method to read bills
        public static String readBill() {
            FileOperator fileOp = new FileOperator();
            return fileOp.readFile(Constants.BILL_FILE);
        }

        // Optional: Method to clear bills
        public static void clearBill() {
            FileOperator fileOp = new FileOperator();
            fileOp.clearFile(Constants.BILL_FILE);
        }
    }

    // Subsystem class 1: Handles directory operations
    private static class DirectoryManager {
        public void ensureDirectoryExists(String dirPath) {
            File directory = new File(dirPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        public boolean directoryExists(String dirPath) {
            return new File(dirPath).exists();
        }
    }

    // Subsystem class 2: Handles file operations
    private static class FileOperator {

        public void appendToFile(String filePath, String content) {
            try (FileWriter writer = new FileWriter(filePath, true)) {
                writer.write(content);
            } catch (IOException e) {
                System.out.println("Error saving to file: " + e.getMessage());
            }
        }

        public String readFile(String filePath) {
            StringBuilder content = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            } catch (IOException e) {
                System.out.println("Error reading file: " + e.getMessage());
                return "";
            }
            return content.toString();
        }

        public void clearFile(String filePath) {
            try (FileWriter writer = new FileWriter(filePath, false)) {
                writer.write("");
            } catch (IOException e) {
                System.out.println("Error clearing file: " + e.getMessage());
            }
        }

        public boolean fileExists(String filePath) {
            return new File(filePath).exists();
        }
    }

    // Subsystem class 3: Handles bill formatting
    private static class BillFormatter {

        public String formatOrders(List<JuiceOrder> orders) {
            StringBuilder sb = new StringBuilder();
            sb.append("=== JUICE/PLANT DRINK BILL ===\n");
            sb.append(String.format("%-25s %3s %8s\n", "Item", "Qty", "Total(TK)"));
            sb.append("----------------------------------------\n");

            for (JuiceOrder order : orders) {
                sb.append(order.getBillLine()).append("\n");
            }

            int grandTotal = orders.stream().mapToInt(JuiceOrder::getTotal).sum();
            sb.append("----------------------------------------\n");
            sb.append(String.format("%-25s %15d TK\n", "GRAND TOTAL:", grandTotal));
            sb.append("========================================\n\n");

            return sb.toString();
        }

        public String formatSingleOrder(JuiceOrder order) {
            return order.getBillLine() + "\n";
        }
    }
}