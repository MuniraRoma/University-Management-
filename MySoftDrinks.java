import java.util.*;
import java.io.*;

public class MySoftDrinks {

    private static final Scanner scanner = new Scanner(System.in);
    private static final java.util.Date DATE = new java.util.Date();

    public MySoftDrinks() {
    }

    // ✅ MODIFIED: Now returns total without displaying bill immediately
    public int SoftDrinkbill(String regNumber) {
        SoftDrinkBillingFacade billingFacade = new SoftDrinkBillingFacade();
        int total = billingFacade.processBill(regNumber);
        return total;
    }

    // FACADE PATTERN: Provides a simplified interface to the complex billing subsystem
    private static class SoftDrinkBillingFacade {

        public int processBill(String regNumber) {
            SoftDrinkMenu menu = new SoftDrinkMenu();
            OrderProcessor orderProcessor = new OrderProcessor();
            BillSaver billSaver = new BillSaver();  // Changed from BillGenerator

            // Display menu
            menu.display();

            // Take orders
            List<SoftDrinkOrder> orders = orderProcessor.takeOrders();

            // Save orders for final bill (NO DISPLAY)
            if (!orders.isEmpty()) {
                int total = billSaver.saveOrders(orders, regNumber);
                System.out.println("✅ Soft drinks added to cart! Total: " + total + " TK");
                return total;
            }

            return 0;
        }
    }

    // MENU DISPLAY CLASS
    private static class SoftDrinkMenu {
        public void display() {
            System.out.println(Constants.MENU_HEADER);
            SoftDrinkItem[] items = SoftDrinkItem.values();
            for (int i = 0; i < items.length; i++) {
                System.out.printf("%d. %s (%d TK)\n", i + 1, items[i].displayName, items[i].price);
            }
            System.out.println(Constants.MENU_FOOTER);
        }
    }

    // ORDER PROCESSOR CLASS
    private static class OrderProcessor {
        private List<SoftDrinkOrder> orders = new ArrayList<>();
        private int total = 0;

        public List<SoftDrinkOrder> takeOrders() {
            orders.clear();
            total = 0;

            while (true) {
                int choice = readInt("Enter Your Choice (0 to finish): ");
                if (choice == 0) break;

                SoftDrinkItem selected = SoftDrinkItem.fromChoice(choice);
                if (selected == null) {
                    System.out.println("❌ Invalid choice! Please try again.");
                    continue;
                }

                int quantity = readInt("Enter quantity: ");
                if (quantity <= 0) {
                    System.out.println("❌ Quantity must be positive!");
                    continue;
                }

                SoftDrinkOrder order = new SoftDrinkOrder(selected, quantity);
                orders.add(order);
                total += order.getTotal();

                System.out.printf("✅ Added: %s x %d = %d TK\n", selected.displayName, quantity, order.getTotal());
                System.out.println("💰 Current total: " + total + " TK");
            }

            return orders;
        }

        private int readInt(String prompt) {
            while (true) {
                System.out.print(prompt);
                try {
                    return Integer.parseInt(scanner.nextLine().trim());
                } catch (NumberFormatException e) {
                    System.out.println("❌ Invalid input! Please enter a number.");
                }
            }
        }
    }

    // NEW BILL SAVER CLASS - Only saves, doesn't display
    private static class BillSaver {
        public int saveOrders(List<SoftDrinkOrder> orders, String regNumber) {
            if (orders.isEmpty()) return 0;

            int total = 0;
            StringBuilder billContent = new StringBuilder();

            // Simple format for final bill (no fancy headers)
            for (SoftDrinkOrder order : orders) {
                billContent.append(order.getBillLine()).append("\n");
                total += order.getTotal();
            }

            // Save to file for final bill
            BillFileService.saveBill(billContent.toString());

            return total;
        }
    }

    private static class Constants {
        public static final String BILL_DIR = "cafe/CafeBills";
        public static final String BILL_FILE = BILL_DIR + "/BillSoftDrinks.txt";
        public static final String MENU_HEADER = "\n===== SOFT DRINKS MENU =====";
        public static final String MENU_FOOTER = "0. Nothing / Go Back\n============================";
    }

    enum SoftDrinkItem {
        COCA_COLA("Coca-Cola", 60),
        PEPSI("Pepsi", 60),
        FANTA("Fanta", 60),
        DEW("Dew", 60),
        STING("Sting", 70),
        SPRITE("Sprite", 60);

        final String displayName;
        final int price;

        SoftDrinkItem(String name, int price) {
            this.displayName = name;
            this.price = price;
        }

        static SoftDrinkItem fromChoice(int choice) {
            if (choice < 1 || choice > values().length) return null;
            return values()[choice - 1];
        }
    }

    static class SoftDrinkOrder {
        final SoftDrinkItem softDrink;
        final int quantity;

        SoftDrinkOrder(SoftDrinkItem softDrink, int quantity) {
            this.softDrink = softDrink;
            this.quantity = quantity;
        }

        int getTotal() {
            return quantity * softDrink.price;
        }

        String getBillLine() {
            return String.format("%-25s %3d    %6d TK", softDrink.displayName, quantity, getTotal());
        }
    }

    // FILE SERVICE
    private static class BillFileService {
        static void saveBill(String billContent) {
            try {
                new File(Constants.BILL_DIR).mkdirs();
                try (FileWriter writer = new FileWriter(Constants.BILL_FILE, true)) {
                    writer.write(billContent);
                }
                // Removed the print statement that showed "Bill saved to..."
            } catch (IOException e) {
                System.out.println("❌ Error saving order: " + e.getMessage());
            }
        }
    }
}