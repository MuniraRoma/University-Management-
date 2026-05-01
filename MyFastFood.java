import java.util.*;
import java.io.*;

public class MyFastFood {

    // Constants for magic strings
    private static final class Constants {
        private static final String BILL_DIR = "cafe/CafeBills";
        private static final String SHARED_BILL_FILE = BILL_DIR + "/BillFastfood.txt";
        private static final String PERSONAL_BILL_DIR = BILL_DIR + "/FastFood/";
        private static final String MENU_HEADER = "\n===== FAST FOOD MENU =====";
        private static final String MENU_FOOTER = "0. Nothing / Go Back\n==========================";
    }

    enum FastFoodItem {
        BURGER("Burger", 80),
        ZINGER_BURGER("Zinger Burger", 250),
        SHAWARMA("Shawarma", 120),
        PIZZA("Pizza", 350),
        SANDWICH("Sandwich", 70),
        FRIES("Fries", 50),
        DEAL_A("Deal A", 550),
        DEAL_B("Deal B", 850),
        DEAL_C("Deal C", 1050);

        final String displayName;
        final int price;

        FastFoodItem(String name, int price) {
            this.displayName = name;
            this.price = price;
        }

        static FastFoodItem fromChoice(int choice) {
            if (choice < 1 || choice > values().length) return null;
            return values()[choice - 1];
        }
    }

    static class OrderedItem {
        final FastFoodItem item;
        final int orderedQuantity;
        int returnedQuantity = 0;

        OrderedItem(FastFoodItem item, int qty) {
            this.item = item;
            this.orderedQuantity = qty;
        }

        int getFinalQuantity() {
            return Math.max(0, orderedQuantity - returnedQuantity);
        }

        int getFinalCost() {
            return getFinalQuantity() * item.price;
        }

        void returnSome(int qty) {
            if (qty > 0 && qty <= orderedQuantity - returnedQuantity) {
                returnedQuantity += qty;
            }
        }

        String getBillLine() {
            int qty = getFinalQuantity();
            if (qty <= 0) return null;
            return String.format("%-20s %4d    %6d", item.displayName, qty, getFinalCost());
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

    private static int readNonNegativeInt(String prompt) {
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
            System.out.println("Quantity must be at least 1.");
        }
    }

    private static int readReturnQuantity(int maxAllowed) {
        while (true) {
            int qty = readInt("Enter returned quantity (max " + maxAllowed + "): ");
            if (qty >= 0 && qty <= maxAllowed) return qty;
            System.out.println("Cannot return more than ordered. Try again.");
        }
    }

    private static boolean askForReturn() {
        System.out.println("\n1. Return some quantity");
        System.out.println("   Any other number → keep all");
        int choice = readInt("Choice: ");
        return choice == 1;
    }

    // EXTRACT METHOD - Menu display
    private static void displayMenu() {
        System.out.println(Constants.MENU_HEADER);
        for (int i = 0; i < FastFoodItem.values().length; i++) {
            FastFoodItem item = FastFoodItem.values()[i];
            System.out.printf("%2d. %-20s %6d TK\n", i + 1, item.displayName, item.price);
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

        private static void savePersonalBill(String regNumber, List<OrderedItem> orders, int total) {
            ensureDirectoryExists(Constants.PERSONAL_BILL_DIR);
            File personalFile = new File(Constants.PERSONAL_BILL_DIR + regNumber + ".txt");

            try (FileWriter writer = new FileWriter(personalFile, true)) {
                writer.write("\nDate: " + new Date() + "\n");
                for (OrderedItem order : orders) {
                    writer.write("Item: " + order.item.displayName + "\n");
                    writer.write("Quantity: " + order.orderedQuantity + "\n");
                    if (order.returnedQuantity > 0) {
                        writer.write("Returned Quantity: " + order.returnedQuantity + "\n");
                    }
                }
                writer.write("----------------------------------------\n");
                writer.write("Total Amount: " + total + "\n\n");
            } catch (IOException e) {
                System.out.println("Error writing personal bill: " + e.getMessage());
            }
        }

        private static void saveSharedBill(List<OrderedItem> orders) {
            ensureDirectoryExists(Constants.BILL_DIR);
            File sharedFile = new File(Constants.SHARED_BILL_FILE);

            try (FileWriter writer = new FileWriter(sharedFile, true)) {
                for (OrderedItem order : orders) {
                    String billLine = order.getBillLine();
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
    private static Optional<OrderedItem> processSingleOrder() {
        int choice = readInt("\nEnter Your Choice (0 to finish): ");

        if (choice == 0) {
            return Optional.empty();
        }

        FastFoodItem selected = FastFoodItem.fromChoice(choice);
        if (selected == null) {
            System.out.println("Invalid choice. Please try again.");
            return Optional.empty();
        }

        int qty = readPositiveQuantity();

        OrderedItem order = new OrderedItem(selected, qty);

        if (askForReturn()) {
            int returnQty = readReturnQuantity(qty);
            if (returnQty > 0) {
                order.returnSome(returnQty);
            }
        }

        int finalQty = order.getFinalQuantity();
        if (finalQty > 0) {
            System.out.printf("Added: %s x %d = %d TK\n", selected.displayName, finalQty, order.getFinalCost());
        } else {
            System.out.println("All items returned. Nothing added.");
        }

        return Optional.of(order);
    }

    // REFACTORED MAIN METHOD
    public static int FastfoodBill() {
        String regNumber = getRegistrationNumber();
        return FastfoodBill(regNumber);
    }

    public static int FastfoodBill(String regNumber) {
        if (regNumber == null || regNumber.isEmpty()) {
            System.out.print("Enter registration number: ");
            regNumber = scanner.nextLine().trim();
        }

        List<OrderedItem> orders = new ArrayList<>();
        int grandTotal = 0;

        while (true) {
            displayMenu();

            Optional<OrderedItem> orderOpt = processSingleOrder();

            if (!orderOpt.isPresent()) {
                break;
            }

            OrderedItem order = orderOpt.get();
            if (order.getFinalQuantity() > 0) {
                orders.add(order);
                grandTotal += order.getFinalCost();
            }
        }

        if (!orders.isEmpty()) {
            BillFileService.savePersonalBill(regNumber, orders, grandTotal);
            BillFileService.saveSharedBill(orders);
            System.out.println("✅ Bill Generated Successfully");
            System.out.println("Total fast food bill: " + grandTotal + " TK");
        } else {
            System.out.println("No items were ordered.");
        }

        return grandTotal;
    }

    private static String getRegistrationNumber() {
        try {
            if (Main.regNumber != null && !Main.regNumber.isEmpty()) {
                return Main.regNumber;
            }
        } catch (Exception e) {
            // Main class might not exist
        }
        return System.getProperty("user.regNumber", "UNKNOWN");
    }

    public static void main(String[] args) {
        String regNumber = (args.length > 0) ? args[0] : "";
        FastfoodBill(regNumber);
    }
}