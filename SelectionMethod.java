import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class SelectionMethod {

    private static final Scanner input = new Scanner(System.in);
    private static final String[] TEMP_BILL_FILES = {
            "cafe/CafeBills/BillFastfood.txt",
            "cafe/CafeBills/BillDesiFood.txt",
            "cafe/CafeBills/BillSoftDrinks.txt",
            "cafe/CafeBills/BillCoffee.txt",
            "cafe/CafeBills/BillJuiceOrPlant.txt"
    };

    private static final int INITIAL_BALANCE = 2000;

    public static void chooseMethod(String regNumber) {
        while (true) {
            printMainMenu();
            int option = readIntInput("Enter Your Choice: ");

            switch (option) {
                case 1 -> foodMenu(regNumber);
                case 2 -> drinkMenu(regNumber);
                case 3 -> generateBill(regNumber);
                case 4 -> {
                    clearTemporaryFiles();
                    System.out.println("Returning to Previous Menu...");
                    return;
                }
                case 5 -> {
                    clearTemporaryFiles();
                    System.out.println("********************************************");
                    System.out.println("Thank you for using Cafe Management System!");
                    System.exit(0);
                }
                default -> System.out.println("Please select a correct option");
            }
        }
    }

    // ----------------- Food Menu -----------------
    private static void foodMenu(String regNumber) {
        String[] foodOptions = {"Fast Food", "Desi Food", "Back to Main Menu"};

        while (true) {
            printMenu("FOOD CATEGORIES", foodOptions);
            int choice = readIntInput("Enter Your Choice: ");

            switch (choice) {
                case 1 -> new MyFastFood().FastfoodBill();
                case 2 -> new MyDesiFood().DesifoodBill();
                case 3 -> {
                    System.out.println("Returning to Main Menu...");
                    System.out.println("********************************************");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    // ----------------- Drink Menu -----------------
    private static void drinkMenu(String regNumber) {
        String[] drinkOptions = {"Soft Drinks", "Coffee", "Juice or Plant Drinks", "Back to Main Menu"};

        while (true) {
            printMenu("DRINK CATEGORIES", drinkOptions);
            int choice = readIntInput("Enter Your Choice: ");

            switch (choice) {
                case 1 -> new MySoftDrinks().SoftDrinkbill(regNumber);
                case 2 -> new MyCoffee().CoffeeBill(regNumber);
                case 3 -> new MyJuiceOrPlantDrink().JuiceORPlantbill();
                case 4 -> {
                    System.out.println("Returning to Main Menu...");
                    System.out.println("********************************************");
                    return;
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    // ----------------- Bill Generation -----------------
    private static void generateBill(String regNumber) {
        try {
            int remainingBalance = MyFinalBill.finalBill(regNumber, INITIAL_BALANCE);
            System.out.println("Remaining Balance: " + remainingBalance + " TK");
        } catch (Exception e) {
            System.out.println("Error generating bill: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ----------------- Utility Methods -----------------
    private static int readIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            String inputStr = input.nextLine().trim();
            try {
                return Integer.parseInt(inputStr);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    private static void printMainMenu() {
        String[] mainOptions = {"Food", "Drinks", "Bill Generate", "Previous Menu", "Exit"};
        printMenu("MAIN MENU", mainOptions);
    }

    private static void printMenu(String title, String[] options) {
        System.out.println("\n===== " + title + " =====");
        for (int i = 0; i < options.length; i++) {
            System.out.printf("%d. %s%n", i + 1, options[i]);
        }
        System.out.println("============================");
    }

    private static void clearTemporaryFiles() {
        for (String filePath : TEMP_BILL_FILES) {
            File file = new File(filePath);
            if (file.exists()) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.print("");
                } catch (IOException e) {
                    System.out.println("Error clearing file: " + filePath);
                }
            }
        }
        System.out.println("\n✅ Temporary files cleared");
    }
}