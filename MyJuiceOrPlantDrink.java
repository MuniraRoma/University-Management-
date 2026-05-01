import java.util.*;
import java.io.*;

public class MyJuiceOrPlantDrink {

    // Constants for magic strings
    private static final class Constants {
        private static final String BILL_DIR = "cafe/CafeBills";
        private static final String SHARED_BILL_FILE = BILL_DIR + "/BillJuiceOrPlant.txt";
        private static final String PERSONAL_BILL_DIR = BILL_DIR + "/MyJuiceOrPlantDrink/";
        private static final String MENU_HEADER = "\n===== JUICE / PLANT DRINK MENU =====";
        private static final String MENU_FOOTER = "0. Nothing / Go Back\n===================================";
    }

    enum JuicePlantItem {
        MANGO("Mango Flavour", 50),
        ORANGE("Orange Flavour", 40),
        PINEAPPLE("Pinnapple Flaour", 70),
        GRAPE("Grape Flavour", 50),
        MINERAL_WATER("Mineral Water", 30);

        final String displayName;
        final int price;

        JuicePlantItem(String displayName, int price) {
            this.displayName = displayName;
            this.price = price;
        }

        static JuicePlantItem fromCode(int code) {
            if (code < 1 || code > values().length) return null;
            return values()[code - 1];
        }
    }

    static class JuiceOrder {
        final JuicePlantItem item;
        final int quantity;
        final int returnedQuantity;
        final int finalQuantity;
        final int totalPrice;

        JuiceOrder(JuicePlantItem item, int quantity, int returnedQuantity) {
            this.item = item;
            this.quantity = quantity;
            this.returnedQuantity = returnedQuantity;
            this.finalQuantity = Math.max(0, quantity - returnedQuantity);
            this.totalPrice = this.finalQuantity * item.price;
        }

        static JuiceOrder create(JuicePlantItem item, int quantity) {
            return new JuiceOrder(item, quantity, 0);
        }

        JuiceOrder withReturn(int returnQty) {
            return new JuiceOrder(this.item, this.quantity, returnQty);
        }

        boolean isEmpty() {
            return finalQuantity <= 0;
        }

        String getPersonalBillLine() {
            StringBuilder sb = new StringBuilder();
            sb.append("You've ordered a ").append(item.displayName).append("\n");
            sb.append("Quantity: ").append(quantity);
            if (returnedQuantity > 0) {
                sb.append("\nYou've returned: ").append(returnedQuantity);
            }
            return sb.toString();
        }

        String getSharedBillLine() {
            if (isEmpty()) return null;
            return String.format("%-22s %3d    %6d", item.displayName, finalQuantity, totalPrice);
        }
    }

    private static final Scanner scanner = new Scanner(System.in);

    // EXTRACT METHOD - Centralized input reading
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String input = scanner.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            int val = readInt(prompt);
            if (val >= 0) return val;
            System.out.println("Please enter a non-negative number.");
        }
    }

    private static int readPositiveQuantity() {
        while (true) {
            int qty = readInt("Enter quantity: ");
            if (qty > 0) return qty;
            System.out.println("Quantity must be positive.");
        }
    }

    private static int readReturnQuantity(int maxQuantity) {
        while (true) {
            int qty = readInt("Enter returned quantity: ");
            if (qty >= 0 && qty <= maxQuantity) return qty;
            System.out.println("Invalid quantity. Must be between 0 and " + maxQuantity);
        }
    }

    private static boolean askForReturn() {
        System.out.println("\n1. Return");
        System.out.println("Press any Integer except '1'");
        return readInt("Choice: ") == 1;
    }

    // EXTRACT METHOD - Menu display
    private static void displayMenu() {
        System.out.println(Constants.MENU_HEADER);
        for (int i = 0; i < JuicePlantItem.values().length; i++) {
            JuicePlantItem item = JuicePlantItem.values()[i];
            System.out.printf("%d. %-20s %d TK\n", i + 1, item.displayName, item.price);
        }
        System.out.println(Constants.MENU_FOOTER);
    }

    // EXTRACT CLASS - File operations
    private static class BillFileService {
        private static void ensureDirectoryExists(String dirPath) {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        private static void savePersonalBill(String regNumber, List<JuiceOrder> orders, int total) {
            ensureDirectoryExists(Constants.PERSONAL_BILL_DIR);
            File personalFile = new File(Constants.PERSONAL_BILL_DIR + regNumber + ".txt");

            try (FileWriter writer = new FileWriter(personalFile, true)) {
                writer.write("\nDate: " + new Date() + "\n");
                for (JuiceOrder order : orders) {
                    writer.write(order.getPersonalBillLine() + "\n");
                }
                writer.write("Total Amount: " + total + "\n");
            } catch (IOException e) {
                System.out.println("Error writing personal bill: " + e.getMessage());
            }
        }

        private static void saveSharedBill(List<JuiceOrder> orders) {
            ensureDirectoryExists(Constants.BILL_DIR);
            File sharedFile = new File(Constants.SHARED_BILL_FILE);

            try (FileWriter writer = new FileWriter(sharedFile, true)) {
                for (JuiceOrder order : orders) {
                    String billLine = order.getSharedBillLine();
                    if (billLine != null) {
                        writer.write(billLine + "\n");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error writing shared bill: " + e.getMessage());
            }
        }
    }

    // EXTRACT METHOD - Process single order
    private static Optional<JuiceOrder> processSingleOrder() {
        int choice = readPositiveInt("\nSelect your Juice Or Plant Drink (0 = Nothing): ");

        if (choice == 0) {
            return Optional.empty();
        }

        JuicePlantItem selected = JuicePlantItem.fromCode(choice);
        if (selected == null) {
            System.out.println("Invalid option.");
            return Optional.empty();
        }

        System.out.println("You've ordered a " + selected.displayName);

        int quantity = readPositiveQuantity();
        JuiceOrder order = JuiceOrder.create(selected, quantity);

        if (askForReturn()) {
            int returnQty = readReturnQuantity(quantity);
            if (returnQty > 0) {
                order = order.withReturn(returnQty);
            }
        }

        return Optional.of(order);
    }

    // REFACTORED MAIN METHOD
    public static int JuiceORPlantbill(String regNumber) {
        if (regNumber == null || regNumber.isEmpty()) {
            System.out.print("Enter registration number: ");
            regNumber = scanner.nextLine().trim();
        }

        List<JuiceOrder> orders = new ArrayList<>();
        int grandTotal = 0;

        while (true) {
            System.out.println();
            displayMenu();
            System.out.println();

            Optional<JuiceOrder> orderOpt = processSingleOrder();

            if (!orderOpt.isPresent()) {
                break;
            }

            JuiceOrder order = orderOpt.get();
            if (!order.isEmpty()) {
                orders.add(order);
                grandTotal += order.totalPrice;
            }
        }

        if (!orders.isEmpty()) {
            BillFileService.savePersonalBill(regNumber, orders, grandTotal);
            BillFileService.saveSharedBill(orders);
            System.out.println("Total Juice/Plant Drinks Bill: " + grandTotal + " TK");
        } else {
            System.out.println("No items ordered.");
        }

        return grandTotal;
    }

    public static int JuiceORPlantbill() {
        return JuiceORPlantbill("");
    }

    public static void main(String[] args) {
        String regNumber = (args.length > 0) ? args[0] : "";
        JuiceORPlantbill(regNumber);
    }
}