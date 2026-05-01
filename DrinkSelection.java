import java.util.*;

public class DrinkSelection {

    // Constants for magic strings
    private static final class Constants {
        private static final String SEPARATOR = "********************************************";
    }

    // Enum for Drink Categories
    enum DrinkCategory {
        SOFT_DRINKS("Soft Drinks", 1),
        COFFEE("Coffee", 2),
        JUICE_PLANT("Juice or Plant Drinks", 3),
        NOTHING("Nothing / Go Back", 0);

        private final String displayName;
        private final int code;

        DrinkCategory(String displayName, int code) {
            this.displayName = displayName;
            this.code = code;
        }

        String getDisplayName() {
            return displayName;
        }

        int getCode() {
            return code;
        }

        static Optional<DrinkCategory> fromCode(int code) {
            return Arrays.stream(values())
                    .filter(category -> category.code == code)
                    .findFirst();
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

    // EXTRACT METHOD - Menu display
    private static void displayMenu() {
        System.out.println("\nPlease Select a Drink you want:");
        System.out.println("1. Soft Drinks");
        System.out.println("2. Coffee");
        System.out.println("3. Juice or Plant Drinks");
        System.out.println("0. Nothing");
    }

    // EXTRACT METHOD - Process single drink category
    private static int processCategory(int choice, String regNumber) {
        switch (choice) {
            case 1:
                System.out.println("\n→ Entering Soft Drinks section...");
                int softDrinkTotal = MySoftDrinks.SoftDrinkbill(regNumber);
                if (softDrinkTotal > 0) {
                    System.out.println("Soft Drinks Total: " + softDrinkTotal + " TK");
                }
                return softDrinkTotal;

            case 2:
                System.out.println("\n→ Entering Coffee section...");
                int coffeeTotal = MyCoffee.CoffeeBill(regNumber);
                if (coffeeTotal > 0) {
                    System.out.println("Coffee Total: " + coffeeTotal + " TK");
                }
                return coffeeTotal;

            case 3:
                System.out.println("\n→ Entering Juice or Plant Drinks section...");
                int juiceTotal = MyJuiceOrPlantDrink.JuiceORPlantbill(regNumber);
                if (juiceTotal > 0) {
                    System.out.println("Juice/Plant Drinks Total: " + juiceTotal + " TK");
                }
                return juiceTotal;

            default:
                System.out.println("Please choose a correct option!");
                System.out.println(Constants.SEPARATOR);
                return 0;
        }
    }

    // REFACTORED MAIN METHOD
    public static void myDrink(String regNumber) {
        if (regNumber == null || regNumber.isEmpty()) {
            System.out.print("Enter registration number: ");
            regNumber = scanner.nextLine().trim();
        }

        int grandTotal = 0;

        while (true) {
            displayMenu();

            int choice = readInt("Enter Your Choice: ");
            System.out.println();

            // Handle exit directly (remove nothing handler)
            if (choice == 0) {
                System.out.println("Exiting Drinks Menu...");
                System.out.println(Constants.SEPARATOR);
                break;
            }

            // Check if valid choice
            Optional<DrinkCategory> categoryOpt = DrinkCategory.fromCode(choice);
            if (!categoryOpt.isPresent() || categoryOpt.get() == DrinkCategory.NOTHING) {
                System.out.println("Please choose a correct option!");
                System.out.println(Constants.SEPARATOR);
                continue;
            }

            int categoryTotal = processCategory(choice, regNumber);
            grandTotal += categoryTotal;
            System.out.println(Constants.SEPARATOR);
        }

        if (grandTotal > 0) {
            System.out.println("Grand Total for Drinks: " + grandTotal + " TK");
        }
    }

    // Overloaded method for backward compatibility
    public static void myDrink(String[] regNumber) {
        String reg = (regNumber != null && regNumber.length > 0) ? regNumber[0] : "";
        myDrink(reg);
    }

    // Utility methods
    public static List<DrinkCategory> getAvailableCategories() {
        return Arrays.asList(DrinkCategory.SOFT_DRINKS, DrinkCategory.COFFEE, DrinkCategory.JUICE_PLANT);
    }

    public static boolean isValidOption(int choice) {
        return DrinkCategory.fromCode(choice).isPresent() && choice != 0;
    }
}