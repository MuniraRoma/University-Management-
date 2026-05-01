import java.util.*;
import java.io.*;

public class MyFastFood {

    private static final Scanner scanner = new Scanner(System.in);
    private static final java.util.Date DATE = new java.util.Date();

    // ─────────────────────────────────────────────
    //  Enum – single place to add/modify items (OCP)
    // ─────────────────────────────────────────────
    enum FastFoodItem {
        BURGER          ("Burger",           80),
        ZINGER_BURGER   ("Zinger Burger",   250),
        SHAWARMA        ("Shawarma",        120),
        PIZZA           ("Pizza",           350),
        SANDWICH        ("Sandwich",         70),
        FRIES           ("Fries",            50),
        DEAL_A          ("Deal A",          550),
        DEAL_B          ("Deal B",          850),
        DEAL_C          ("Deal C",         1050);

        final String displayName;
        final int price;

        FastFoodItem(String name, int price) {
            this.displayName = name;
            this.price = price;
        }

        static FastFoodItem fromChoice(int choice) {
            if (choice < 1 || choice > values().length) {
                return null;
            }
            return values()[choice - 1];
        }
    }

    // ─────────────────────────────────────────────
    //  Ordered item – keeps track of original vs returned
    // ─────────────────────────────────────────────
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
            return String.format("%-20s %4d    %6d",
                    item.displayName, qty, getFinalCost());
        }
    }

    // ─────────────────────────────────────────────
    //  Main billing method – returns total for integration
    // ─────────────────────────────────────────────
    public static int FastfoodBill() {
        List<OrderedItem> orders = new ArrayList<>();
        int grandTotal = 0;

        File personalFile = new File("cafe/CafeBills/FastFood/" + Main.regNumber + ".txt");
        File sharedBillFile = new File("cafe/CafeBills/BillFastfood.txt");

        try (
                FileWriter personalWriter = new FileWriter(personalFile, true);
                FileWriter sharedWriter = new FileWriter(sharedBillFile, true)
        ) {
            personalWriter.write("\nDate: " + DATE + "\n");

            while (true) {
                displayMenu();

                System.out.print("\nEnter Your Choice (0 to finish): ");
                int choice = readNonNegativeInt();

                if (choice == 0) {
                    break;
                }

                FastFoodItem selected = FastFoodItem.fromChoice(choice);
                if (selected == null) {
                    System.out.println("Invalid choice. Please try again.");
                    continue;
                }

                int qty = readPositiveQuantity();
                if (qty <= 0) continue;

                OrderedItem order = new OrderedItem(selected, qty);
                orders.add(order);

                personalWriter.write("Item: " + selected.displayName + "\n");
                personalWriter.write("Quantity: " + qty + "\n");

                // Return option
                if (askForReturn()) {
                    int returnQty = readReturnQuantity(qty);
                    if (returnQty > 0) {
                        order.returnSome(returnQty);
                        personalWriter.write("Returned Quantity: " + returnQty + "\n");
                    }
                }

                // Write final line to shared bill file
                String billLine = order.getBillLine();
                if (billLine != null) {
                    sharedWriter.write(billLine + "\n");
                }

                grandTotal += order.getFinalCost();
            }

            personalWriter.write("----------------------------------------\n");
            personalWriter.write("Total Amount: " + grandTotal + "\n");
            personalWriter.write("----------------------------------------\n\n");

            System.out.println("✅ Bill Generated Successfully");

            return grandTotal;

        } catch (IOException e) {
            System.out.println("Error handling files: " + e.getMessage());
            return 0;
        }
    }

    // ─────────────────────────────────────────────
    //  Helper methods – each has one clear responsibility
    // ─────────────────────────────────────────────

    private static void displayMenu() {
        File menuFile = new File("cafe/CafeBills/FastFoodMenu.txt");

        System.out.println("\n===== FAST FOOD MENU =====");

        if (!menuFile.exists() || !menuFile.canRead()) {
            // fallback menu
            for (int i = 0; i < FastFoodItem.values().length; i++) {
                FastFoodItem item = FastFoodItem.values()[i];
                System.out.printf("%2d. %-20s %6d TK\n", i + 1, item.displayName, item.price);
            }
            return;
        }

        try (Scanner reader = new Scanner(menuFile)) {
            while (reader.hasNextLine()) {
                System.out.println(reader.nextLine());
            }
        } catch (Exception e) {
            System.out.println("Cannot read menu file. Using fallback.");
        }
    }

    private static int readNonNegativeInt() {
        while (true) {
            try {
                int val = scanner.nextInt();
                scanner.nextLine();
                return Math.max(0, val);
            } catch (InputMismatchException e) {
                System.out.print("Please enter a valid number: ");
                scanner.nextLine();
            }
        }
    }

    private static int readPositiveQuantity() {
        System.out.print("Enter quantity: ");
        while (true) {
            int qty = readNonNegativeInt();
            if (qty > 0) return qty;
            System.out.println("Quantity must be at least 1. Try again.");
        }
    }

    private static boolean askForReturn() {
        System.out.println("\n1. Return some quantity");
        System.out.println("   Any other number → keep all");
        System.out.print("Choice: ");
        return readNonNegativeInt() == 1;
    }

    private static int readReturnQuantity(int maxAllowed) {
        System.out.print("Enter returned quantity (max " + maxAllowed + "): ");
        while (true) {
            int qty = readNonNegativeInt();
            if (qty > maxAllowed) {
                System.out.println("Cannot return more than ordered. Try again.");
                continue;
            }
            return qty;
        }
    }

    // ─────────────────────────────────────────────
    //  Backward compatibility / testing entry point
    // ─────────────────────────────────────────────
    public static void main(String[] args) {
        String regNumber = args.length > 0 ? args[0] : "";
        if (regNumber.isEmpty()) {
            System.out.print("Enter registration number: ");
            regNumber = scanner.nextLine().trim();
        }

        MyFastFood fastFood = new MyFastFood();
        int total = fastFood.FastfoodBill();

        if (total > 0) {
            System.out.println("Total fast food bill: " + total + " TK");
        } else {
            System.out.println("No items were ordered.");
        }
    }
}
