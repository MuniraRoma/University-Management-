import java.util.*;
import java.io.*;

public class MySoftDrinks {

    // Constants for magic strings
    private static final class Constants {
        private static final String BILL_DIR = "cafe/CafeBills";
        private static final String SHARED_BILL_FILE = BILL_DIR + "/BillSoftDrinks.txt";
        private static final String PERSONAL_BILL_DIR = BILL_DIR + "/MySoftDrinks/";
        private static final String MENU_HEADER = "\n===== SOFT DRINKS MENU =====";
        private static final String MENU_FOOTER = "0. Finish order\n=============================";
    }

    enum SoftDrink {
        COCA_COLA("Coca-Cola", 60),
        PEPSI("Pepsi", 60),
        FANTA("Fanta", 60),
        MOUNTAIN_DEW("Dew", 60),
        STING("Sting", 70),
        SPRITE("Sprite", 60);

        final String displayName;
        final int pricePerUnit;

        SoftDrink(String displayName, int price) {
            this.displayName = displayName;
            this.pricePerUnit = price;
        }

        static SoftDrink fromChoice(int choice) {
            if (choice < 1 || choice > values().length) return null;
            return values()[choice - 1];
        }
    }

    static class OrderedDrink {
        final SoftDrink drink;
        final int orderedQuantity;
        int returnedQuantity = 0;

        OrderedDrink(SoftDrink drink, int qty) {
            this.drink = drink;
            this.orderedQuantity = qty;
        }

        int getFinalQuantity() {
            return Math.max(0, orderedQuantity - returnedQuantity);
        }

        int getFinalCost() {
            return getFinalQuantity() * drink.pricePerUnit;
        }

        void returnQuantity(int qty) {
            if (qty > 0 && qty <= orderedQuantity - returnedQuantity) {
                this.returnedQuantity += qty;
            }
        }

        String getBillLine() {
            int finalQty = getFinalQuantity();
            if (finalQty <= 0) return null;
            return String.format("%-22s %3d    %6d", drink.displayName, finalQty, getFinalCost());
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

    private static int readIntOrZero(String prompt) {
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

    private static int readReturnQty(int max) {
        while (true) {
            int qty = readInt("Enter returned quantity (max " + max + "): ");
            if (qty >= 0 && qty <= max) return qty;
            System.out.println("You cannot return more than ordered.");
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
        for (int i = 0; i < SoftDrink.values().length; i++) {
            SoftDrink d = SoftDrink.values()[i];
            System.out.printf("%2d. %-20s %4d TK\n", i + 1, d.displayName, d.pricePerUnit);
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

        private static void savePersonalBill(String regNumber, List<OrderedDrink> orders, int total) {
            ensureDirectoryExists(Constants.PERSONAL_BILL_DIR);
            File personalLog = new File(Constants.PERSONAL_BILL_DIR + regNumber + ".txt");

            try (FileWriter writer = new FileWriter(personalLog, true)) {
                writer.write("\nDate: " + new Date() + "\n");
                for (OrderedDrink order : orders) {
                    writer.write("You've ordered a " + order.drink.displayName + "\n");
                    writer.write("Quantity: " + order.orderedQuantity + "\n");
                    if (order.returnedQuantity > 0) {
                        writer.write("You've returned a " + order.drink.displayName + "\n");
                        writer.write("Quantity returned: " + order.returnedQuantity + "\n");
                    }
                }
                writer.write("----------------------------------------\n");
                writer.write("Total Amount: " + total + "\n\n");
            } catch (IOException e) {
                System.out.println("Error writing personal bill: " + e.getMessage());
            }
        }

        private static void saveSharedBill(List<OrderedDrink> orders) {
            ensureDirectoryExists(Constants.BILL_DIR);
            File sharedBill = new File(Constants.SHARED_BILL_FILE);

            try (FileWriter writer = new FileWriter(sharedBill, true)) {
                for (OrderedDrink order : orders) {
                    String line = order.getBillLine();
                    if (line != null) {
                        writer.write(line + "\n");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error writing shared bill: " + e.getMessage());
            }
        }
    }

    // EXTRACT METHOD - Process single order
    private static Optional<OrderedDrink> processSingleOrder() {
        int choice = readInt("\nSelect your Soft Drink (0 = Finish): ");

        if (choice == 0) {
            return Optional.empty();
        }

        SoftDrink selected = SoftDrink.fromChoice(choice);
        if (selected == null) {
            System.out.println("Invalid option.");
            return Optional.empty();
        }

        int qty = readPositiveQuantity();

        OrderedDrink order = new OrderedDrink(selected, qty);

        if (askForReturn()) {
            int returnQty = readReturnQty(qty);
            if (returnQty > 0) {
                order.returnQuantity(returnQty);
            }
        }

        return Optional.of(order);
    }

    // REFACTORED MAIN METHOD
    public static int SoftDrinkbill(String regNumber) {
        if (regNumber == null || regNumber.isEmpty()) {
            System.out.print("Enter registration number: ");
            regNumber = scanner.nextLine().trim();
        }

        List<OrderedDrink> orders = new ArrayList<>();
        int grandTotal = 0;

        while (true) {
            displayMenu();

            Optional<OrderedDrink> orderOpt = processSingleOrder();

            if (!orderOpt.isPresent()) {
                break;
            }

            OrderedDrink order = orderOpt.get();
            if (order.getFinalQuantity() > 0) {
                orders.add(order);
                grandTotal += order.getFinalCost();
            }
        }

        if (!orders.isEmpty()) {
            BillFileService.savePersonalBill(regNumber, orders, grandTotal);
            BillFileService.saveSharedBill(orders);
            System.out.println("Total soft drinks bill: " + grandTotal + " TK");
        } else {
            System.out.println("No items ordered.");
        }

        return grandTotal;
    }

    public static void main(String[] args) {
        String regNumber = (args.length > 0) ? args[0] : "";
        SoftDrinkbill(regNumber);
    }
}