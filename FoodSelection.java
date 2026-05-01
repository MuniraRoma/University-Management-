import java.util.*;

public class FoodSelection {

    // Constants for magic strings
    private static final class Constants {
        private static final String SEPARATOR = "********************************************";
        private static final String MENU_HEADER = "\n===== FOOD CATEGORIES =====";
        private static final String MENU_FOOTER = "==========================";
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
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    }

    // EXTRACT METHOD - Menu display
    private static void displayMenu() {
        System.out.println(Constants.MENU_HEADER);
        System.out.println("1. Fast Food");
        System.out.println("2. Desi Food");
        System.out.println("0. Nothing / Go Back");
        System.out.println(Constants.MENU_FOOTER);
    }

    // EXTRACT METHOD - Process single food category
    private static int processCategory(int choice, String regNumber) {
        switch (choice) {
            case 1:
                System.out.println("\nEntering Fast Food section...");
                System.out.println("----------------------------------------");
                int fastFoodTotal = MyFastFood.FastfoodBill(regNumber);
                System.out.println(Constants.SEPARATOR);
                return fastFoodTotal;

            case 2:
                System.out.println("\nEntering Desi Food section...");
                System.out.println("----------------------------------------");
                int desiFoodTotal = MyDesiFood.DesifoodBill(regNumber);
                System.out.println(Constants.SEPARATOR);
                return desiFoodTotal;

            default:
                return 0;
        }
    }

    // REFACTORED MAIN METHOD
    public static void food(String regNumber) {
        if (regNumber == null || regNumber.isEmpty()) {
            System.out.print("Enter registration number: ");
            regNumber = scanner.nextLine().trim();
        }

        int grandTotal = 0;

        while (true) {
            displayMenu();

            int choice = readInt("Enter Your Choice: ");
            System.out.println();

            // Handle exit directly
            if (choice == 0) {
                System.out.println(Constants.SEPARATOR);
                break;
            }

            if (choice < 1 || choice > 2) {
                System.out.println("Please choose a correct option.");
                System.out.println(Constants.SEPARATOR);
                continue;
            }

            int categoryTotal = processCategory(choice, regNumber);
            grandTotal += categoryTotal;
        }

        if (grandTotal > 0) {
            System.out.println("Grand Total for Food: " + grandTotal + " TK");
        }
    }
}