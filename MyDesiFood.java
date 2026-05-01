import java.util.*;
import java.io.*;

public class MyDesiFood {

    // Constants for magic strings
    private static final class Constants {
        private static final String BILL_DIR = "cafe/CafeBills";
        private static final String SHARED_BILL_FILE = BILL_DIR + "/BillDesiFood.txt";
        private static final String PERSONAL_BILL_DIR = BILL_DIR + "/DesiFood/";
        private static final String MENU_HEADER = "\n===== DESI FOOD MENU =====";
        private static final String MENU_FOOTER = "0. Nothing / Go Back\n=========================";
    }

    enum DesiDish {
        HALEEM("Haleem", 120),
        ROGAN_GOSHT("Rogan Gosht", 250),
        MATAR_PANEER("Matar Paneer", 150),
        ALOO_KA_PARATHA("Aloo Ka Paratha", 100),
        SPICY_SWEET_POTATOES("Spicy Sweet Potatoes", 80),
        CHOLE_PALAK("Chole Palak", 110),
        MASH_KI_DAL("Mash ki Dal", 130),
        BIRYANI("Biryani", 200),
        CHICKEN_QORMA("Chicken Quorma", 180),
        SAMOSA("Samosa", 25);

        final String displayName;
        final int price;

        DesiDish(String name, int price) {
            this.displayName = name;
            this.price = price;
        }

        static DesiDish fromChoice(int choice) {
            if (choice < 1 || choice > values().length) return null;
            return values()[choice - 1];
        }
    }

    static class OrderedDish {
        final DesiDish dish;
        final int orderedQuantity;
        int returnedQuantity = 0;

        OrderedDish(DesiDish dish, int qty) {
            this.dish = dish;
            this.orderedQuantity = qty;
        }

        int getFinalQuantity() {
            return Math.max(0, orderedQuantity - returnedQuantity);
        }

        int getFinalCost() {
            return getFinalQuantity() * dish.price;
        }

        void returnSome(int qty) {
            if (qty > 0 && qty <= orderedQuantity - returnedQuantity) {
                this.returnedQuantity += qty;
            }
        }

        String getBillLine() {
            int qty = getFinalQuantity();
            if (qty <= 0) return null;
            return String.format("%-22s %3d    %6d", dish.displayName, qty, getFinalCost());
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
            System.out.println("Cannot return more than ordered.");
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
        for (int i = 0; i < DesiDish.values().length; i++) {
            DesiDish dish = DesiDish.values()[i];
            System.out.printf("%2d. %-22s %5d TK\n", i + 1, dish.displayName, dish.price);
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

        private static void savePersonalBill(String regNumber, List<OrderedDish> orders, int total) {
            ensureDirectoryExists(Constants.PERSONAL_BILL_DIR);
            File personalFile = new File(Constants.PERSONAL_BILL_DIR + regNumber + ".txt");

            try (FileWriter writer = new FileWriter(personalFile, true)) {
                writer.write("\nDate: " + new Date() + "\n");
                for (OrderedDish order : orders) {
                    writer.write("You've ordered a " + order.dish.displayName + "\n");
                    writer.write("Quantity: " + order.orderedQuantity + "\n");
                    if (order.returnedQuantity > 0) {
                        writer.write("You've returned a " + order.dish.displayName + "\n");
                        writer.write("Quantity returned: " + order.returnedQuantity + "\n");
                    }
                }
                writer.write("----------------------------------------\n");
                writer.write("Total Amount: " + total + "\n\n");
            } catch (IOException e) {
                System.out.println("Error writing personal bill: " + e.getMessage());
            }
        }

        private static void saveSharedBill(List<OrderedDish> orders) {
            ensureDirectoryExists(Constants.BILL_DIR);
            File sharedFile = new File(Constants.SHARED_BILL_FILE);

            try (FileWriter writer = new FileWriter(sharedFile, true)) {
                for (OrderedDish order : orders) {
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
    private static Optional<OrderedDish> processSingleOrder() {
        int choice = readInt("\nSelect your Desi food (0 = Finish): ");

        if (choice == 0) {
            return Optional.empty();
        }

        DesiDish selected = DesiDish.fromChoice(choice);
        if (selected == null) {
            System.out.println("Invalid option.");
            return Optional.empty();
        }

        int qty = readPositiveQuantity();

        OrderedDish order = new OrderedDish(selected, qty);

        if (askForReturn()) {
            int returnQty = readReturnQuantity(qty);
            if (returnQty > 0) {
                order.returnSome(returnQty);
            }
        }

        int finalQty = order.getFinalQuantity();
        if (finalQty > 0) {
            System.out.printf("Added: %s x %d = %d TK\n", selected.displayName, finalQty, order.getFinalCost());
        }

        return Optional.of(order);
    }

    // REFACTORED MAIN METHOD
    public static int DesifoodBill() {
        String regNumber = getRegistrationNumber();
        return DesifoodBill(regNumber);
    }

    public static int DesifoodBill(String regNumber) {
        if (regNumber == null || regNumber.isEmpty()) {
            System.out.print("Enter registration number: ");
            regNumber = scanner.nextLine().trim();
        }

        List<OrderedDish> orders = new ArrayList<>();
        int grandTotal = 0;

        while (true) {
            displayMenu();

            Optional<OrderedDish> orderOpt = processSingleOrder();

            if (!orderOpt.isPresent()) {
                break;
            }

            OrderedDish order = orderOpt.get();
            if (order.getFinalQuantity() > 0) {
                orders.add(order);
                grandTotal += order.getFinalCost();
            }
        }

        if (!orders.isEmpty()) {
            BillFileService.savePersonalBill(regNumber, orders, grandTotal);
            BillFileService.saveSharedBill(orders);
            System.out.println("\nTotal desi food bill: " + grandTotal + " TK");
        } else {
            System.out.println("No items ordered.");
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
        DesifoodBill(regNumber);
    }
}