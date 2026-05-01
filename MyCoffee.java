import java.util.*;
import java.io.*;

public class MyCoffee {

    // Constants for magic strings
    private static final class Constants {
        private static final String BILL_DIR = "cafe/CafeBills";
        private static final String SHARED_BILL_FILE = BILL_DIR + "/BillCoffee.txt";
        private static final String PERSONAL_BILL_DIR = BILL_DIR + "/Coffee/";
        private static final String MENU_HEADER = "\n===== COFFEE MENU =====";
        private static final String MENU_FOOTER = "0. Nothing / Go Back\n=======================";
    }

    // Enum for coffee items
    enum CoffeeItem {
        ESPRESSO("Espresso", 120),
        CAPPUCCINO("Cappuccino", 150),
        LATTE("Latte", 170),
        MOCHA("Mocha", 200),
        BLACK_COFFEE("Black Coffee", 100);

        final String displayName;
        final int price;

        CoffeeItem(String name, int price) {
            this.displayName = name;
            this.price = price;
        }

        static CoffeeItem fromChoice(int choice) {
            if (choice < 1 || choice > values().length) return null;
            return values()[choice - 1];
        }
    }

    static class CoffeeOrder {
        final CoffeeItem item;
        final int quantity;
        final int totalPrice;

        CoffeeOrder(CoffeeItem item, int quantity) {
            this.item = item;
            this.quantity = quantity;
            this.totalPrice = quantity * item.price;
        }

        String getBillLine() {
            return String.format("%-22s %3d    %6d", item.displayName, quantity, totalPrice);
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

    private static int readPositiveQuantity() {
        while (true) {
            int qty = readInt("Enter quantity: ");
            if (qty > 0) {
                return qty;
            }
            System.out.println("Quantity must be positive!");
        }
    }

    // EXTRACT METHOD - Menu display
    private static void displayMenu() {
        System.out.println(Constants.MENU_HEADER);
        for (int i = 0; i < CoffeeItem.values().length; i++) {
            CoffeeItem item = CoffeeItem.values()[i];
            System.out.printf("%d. %-15s %d TK\n", i + 1, item.displayName, item.price);
        }
        System.out.println(Constants.MENU_FOOTER);
    }

    // EXTRACT CLASS - File operations abstraction
    private static class BillFileService {
        private static void ensureDirectoryExists(String dirPath) {
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        private static void savePersonalBill(String regNumber, List<CoffeeOrder> orders, int total) {
            ensureDirectoryExists(Constants.PERSONAL_BILL_DIR);
            File personalFile = new File(Constants.PERSONAL_BILL_DIR + regNumber + ".txt");

            try (FileWriter writer = new FileWriter(personalFile, true)) {
                writer.write("\nDate: " + new Date() + "\n");
                for (CoffeeOrder order : orders) {
                    writer.write("You've ordered a " + order.item.displayName + "\n");
                    writer.write("Quantity: " + order.quantity + "\n");
                }
                writer.write("----------------------------------------\n");
                writer.write("Total Amount: " + total + "\n\n");
            } catch (IOException e) {
                System.out.println("Error writing personal bill: " + e.getMessage());
            }
        }

        private static void saveSharedBill(List<CoffeeOrder> orders) {
            ensureDirectoryExists(Constants.BILL_DIR);
            File sharedFile = new File(Constants.SHARED_BILL_FILE);

            try (FileWriter writer = new FileWriter(sharedFile, true)) {
                for (CoffeeOrder order : orders) {
                    writer.write(order.getBillLine() + "\n");
                }
            } catch (IOException e) {
                System.out.println("Error writing shared bill: " + e.getMessage());
            }
        }
    }

    // EXTRACT METHOD - Process single order
    private static Optional<CoffeeOrder> processSingleOrder() {
        int choice = readInt("Enter your choice: ");

        if (choice == 0) {
            return Optional.empty();
        }

        CoffeeItem selected = CoffeeItem.fromChoice(choice);
        if (selected == null) {
            System.out.println("Invalid option.");
            return Optional.empty();
        }

        int quantity = readPositiveQuantity();
        CoffeeOrder order = new CoffeeOrder(selected, quantity);

        System.out.printf("Added: %s x %d = %d TK\n", selected.displayName, quantity, order.totalPrice);

        return Optional.of(order);
    }

    // REFACTORED MAIN METHOD - No longer a long method
    public static int CoffeeBill(String regNumber) {
        if (regNumber == null || regNumber.isEmpty()) {
            System.out.print("Enter registration number: ");
            regNumber = scanner.nextLine().trim();
        }

        List<CoffeeOrder> orders = new ArrayList<>();
        int grandTotal = 0;

        while (true) {
            displayMenu();

            Optional<CoffeeOrder> orderOpt = processSingleOrder();

            if (!orderOpt.isPresent()) {
                break;
            }

            CoffeeOrder order = orderOpt.get();
            orders.add(order);
            grandTotal += order.totalPrice;
        }

        if (!orders.isEmpty()) {
            BillFileService.savePersonalBill(regNumber, orders, grandTotal);
            BillFileService.saveSharedBill(orders);
            System.out.println("TOTAL COFFEE BILL: " + grandTotal);
            System.out.println("Thank you ☕");
        } else {
            System.out.println("No items ordered.");
        }

        return grandTotal;
    }

    public static int CoffeeBill() {
        return CoffeeBill("");
    }

    public static void main(String[] args) {
        String regNumber = (args.length > 0) ? args[0] : "";
        CoffeeBill(regNumber);
    }
}