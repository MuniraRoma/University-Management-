import java.util.*;
import java.io.*;

public class MyDesiFood {

    private static final Scanner scanner = new Scanner(System.in);
    private static final java.util.Date DATE = new java.util.Date();

    public void desiFoodBill() {
    }

    private static class Constants {
        public static final String BILL_DIR = "cafe/CafeBills";
        public static final String BILL_FILE = BILL_DIR + "/BillDesiFood.txt";
        public static final String MENU_HEADER = "\n===== DESI FOOD MENU =====";
        public static final String MENU_FOOTER = "0. Nothing / Go Back\n==========================";
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
        final int quantity;

        OrderedDish(DesiDish dish, int quantity) {
            this.dish = dish;
            this.quantity = quantity;
        }

        int getTotalPrice() {
            return quantity * dish.price;
        }

        String getBillLine() {
            return String.format("%-25s %3d    %6d", dish.displayName, quantity, getTotalPrice());
        }
    }

    public int DesifoodBill() {
        List<OrderedDish> orders = new ArrayList<>();
        int sessionTotal = 0;

        displayMenu();

        while (true) {
            int choice = readInt("Enter Your Choice (0 to finish): ");
            if (choice == 0) break;

            DesiDish selected = DesiDish.fromChoice(choice);
            if (selected == null) {
                System.out.println("Invalid choice!");
                continue;
            }

            int quantity = readInt("Enter quantity: ");
            if (quantity <= 0) {
                System.out.println("Quantity must be positive!");
                continue;
            }

            OrderedDish order = new OrderedDish(selected, quantity);
            orders.add(order);
            sessionTotal += order.getTotalPrice();

            System.out.printf("✓ Added: %s x %d = %d TK\n", selected.displayName, quantity, order.getTotalPrice());
            System.out.println("Current total: " + sessionTotal + " TK");
        }

        if (!orders.isEmpty()) {
            BillFileService.saveOrders(orders);
            System.out.println("\n✅ Desi Food Bill Generated. Total: " + sessionTotal + " TK");
        } else {
            System.out.println("No Desi Food items ordered.");
        }

        return sessionTotal;
    }

    private void displayMenu() {
        System.out.println(Constants.MENU_HEADER);
        DesiDish[] dishes = DesiDish.values();
        for (int i = 0; i < dishes.length; i++) {
            System.out.printf("%d. %s (%d Rs)\n", i + 1, dishes[i].displayName, dishes[i].price);
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
    // FILE SERVICE
    // ==========================
    private static class BillFileService {
        static void saveOrders(List<OrderedDish> orders) {
            try {
                new File(Constants.BILL_DIR).mkdirs();
                try (FileWriter writer = new FileWriter(Constants.BILL_FILE, true)) {
                    for (OrderedDish order : orders) {
                        writer.write(order.getBillLine() + "\n");
                    }
                }
            } catch (IOException e) {
                System.out.println("Error saving: " + e.getMessage());
            }
        }
    }
}