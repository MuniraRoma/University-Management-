import java.util.*;
import java.io.*;

public class SelectionMethod {

    // Constants for magic strings and numbers
    private static final class Constants {
        private static final int INITIAL_BALANCE = 2000;
        private static final String BILL_DIR = "cafe/CafeBills";
        private static final String TEMP_BILL_FILE = BILL_DIR + "/BillFastfood.txt";
        private static final String STUDENT_DATA_DIR = "students_data/";
        private static final String FINAL_BILL_DIR = BILL_DIR + "/FinalBills/";
        private static final String SEPARATOR = "********************************************";
    }

    // Only keep scanner - removed unused static variables
    private static final Scanner scanner = new Scanner(System.in);

    // ============================================
    // EXTRACT METHOD - Centralized input reading
    // ============================================
    private static int readIntInput(String prompt) {
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

    // ============================================
    // EXTRACT METHOD - Print main menu
    // ============================================
    private static void printMainMenu() {
        System.out.println("\n========== MAIN MENU ==========");
        System.out.println("1. Food");
        System.out.println("2. Drinks");
        System.out.println("3. Bill Generate");
        System.out.println("4. Previous Menu");
        System.out.println("5. Exit");
        System.out.println("================================");
        System.out.println();
    }

    // ============================================
    // EXTRACT METHOD - Clear temporary bill file
    // (Fixed empty catch block)
    // ============================================
    private static void clearTemporaryBillFile() {
        File tempFile = new File(Constants.TEMP_BILL_FILE);
        try (PrintWriter writer = new PrintWriter(tempFile)) {
            writer.print("");
        } catch (IOException e) {
            System.out.println("Error clearing temporary bill file: " + e.getMessage());
        }
    }

    // ============================================
    // EXTRACT METHOD - Read student name from file
    // ============================================
    private static String readStudentName(String regNumber) {
        File studentFile = new File(Constants.STUDENT_DATA_DIR + regNumber + ".txt");
        
        if (!studentFile.exists()) {
            return "Unknown";
        }
        
        try (Scanner sc = new Scanner(studentFile)) {
            if (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(":", 2);
                if (parts.length >= 2) {
                    return parts[1].trim();
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Student file not found: " + e.getMessage());
        }
        return "Unknown";
    }

    // ============================================
    // EXTRACT METHOD - Read bill items from temp file
    // ============================================
    private static List<String> readBillItems() {
        File billFile = new File(Constants.TEMP_BILL_FILE);
        List<String> lines = new ArrayList<>();

        if (!billFile.exists()) {
            return lines;
        }

        try (Scanner reader = new Scanner(billFile)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (!line.isEmpty()) {
                    lines.add(line);
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Bill file not found: " + e.getMessage());
        }

        return lines;
    }

    // ============================================
    // EXTRACT METHOD - Calculate total bill from providers
    // ============================================
    private static int calculateTotalBill(List<BillProvider> providers) {
        int total = 0;
        for (BillProvider provider : providers) {
            total += provider.getBillAmount();
        }
        return total;
    }

    // ============================================
    // EXTRACT METHOD - Print bill to console
    // ============================================
    private static void printBillToConsole(String regNumber, String name, 
                                            List<String> billLines, int total, Date date) {
        System.out.println("\n\n\n");
        System.out.println("\t\t\t-----------------------------------");
        System.out.println("\t\t\tDate: " + date);
        System.out.println("\t\t\tName: " + name);
        System.out.println("\t\t\tStudent ID: " + regNumber);
        System.out.println("\t\t\t---------Thanks For Coming---------");
        System.out.println("\t\t\t-----------Your Bill Is------------\n");
        System.out.println("\t\t\tItems Quantity Prices");

        for (String line : billLines) {
            System.out.println("\t\t\t" + line);
        }

        System.out.printf("\t\t\tTotal Bill %d \n", total);
        System.out.println("\t\t\t---------------------------------------");
        System.out.println("\t\t\t---------------------------------------");
        System.out.println("\t\t\t---------------------------------------");
        System.out.println();
    }

    // ============================================
    // EXTRACT METHOD - Save final bill to file
    // ============================================
    private static void saveFinalBill(String regNumber, String name, 
                                       List<String> items, int total, Date date) {
        File folder = new File(Constants.FINAL_BILL_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File finalBillFile = new File(Constants.FINAL_BILL_DIR + regNumber + ".txt");

        try (FileWriter writer = new FileWriter(finalBillFile, true);
             PrintWriter pw = new PrintWriter(writer)) {

            pw.println("Date: " + date);
            pw.println("-----------------------------------");
            pw.println("Name : " + name);
            pw.println("Student ID : " + regNumber);
            pw.println("-----------------------------------");
            pw.println("---------Thanks For Coming---------");
            pw.println("-----------Your Bill Is------------");
            pw.println("\t\t\tItems Quantity Prices");

            for (String item : items) {
                pw.println("\t\t\t" + item);
            }

            pw.println("Total Bill " + total);
            pw.println("-----------------------------------");
            pw.println("-----------------------------------");
            pw.println();
            pw.println();

        } catch (IOException e) {
            System.out.println("Error saving final bill: " + e.getMessage());
        }
    }

    // ============================================
    // EXTRACT METHOD - Generate and show bill
    // (Removed throws Exception - handled internally)
    // ============================================
    private static void generateAndShowBill(String regNumber, int accountBalance, 
                                             List<BillProvider> providers, Date date) {
        
        String name = readStudentName(regNumber);

        // Calculate total
        int total = calculateTotalBill(providers);

        if (total == 0) {
            System.out.println("Please Buy Something First");
            return;
        }

        if (total > accountBalance) {
            System.out.println("Sir, You don't have enough Account Balance");
            return;
        }

        // We can pay
        List<String> billLines = readBillItems();

        printBillToConsole(regNumber, name, billLines, total, date);
        saveFinalBill(regNumber, name, billLines, total, date);
    }

    // ============================================
    // EXTRACT METHOD - Food menu handler
    // ============================================
    private static void handleFoodMenu(String regNumber) {
        FoodSelection.food(regNumber);
        System.out.println(Constants.SEPARATOR);
    }

    // ============================================
    // EXTRACT METHOD - Drinks menu handler
    // ============================================
    private static void handleDrinksMenu(String regNumber) {
        DrinkSelection.myDrink(regNumber);
        System.out.println(Constants.SEPARATOR);
    }

    // ============================================
    // EXTRACT METHOD - Bill generation handler
    // ============================================
    private static void handleBillGeneration(String regNumber, List<BillProvider> providers, Date date) {
        generateAndShowBill(regNumber, Constants.INITIAL_BALANCE, providers, date);
    }

    // ============================================
    // EXTRACT METHOD - Previous menu handler
    // ============================================
    private static void handlePreviousMenu(String regNumber) throws Exception {
        clearTemporaryBillFile();
        CafeManagement.manage(regNumber);
    }

    // ============================================
    // EXTRACT METHOD - Exit handler
    // ============================================
    private static void handleExit() {
        clearTemporaryBillFile();
        System.out.println(Constants.SEPARATOR);
        System.out.println("Thank you for using Cafe Management System!");
        System.out.println(Constants.SEPARATOR);
        System.exit(0);
    }

    // ============================================
    // REFACTORED MAIN METHOD - No longer a long method
    // Removed throws Exception - handled internally
    // ============================================
    public static void chooseMethod(String regNumber) {
        // Get current date (local variable instead of static)
        Date date = new Date();

        FoodSelection foodSelectionObj = new FoodSelection();
        DrinkSelection drinkSelectionObj = new DrinkSelection();

        List<BillProvider> billProviders = Arrays.asList(
                new FastFoodBillProvider(),
                new DesiFoodBillProvider(),
                new SoftDrinkBillProvider(),
                new CoffeeBillProvider(),
                new JuicePlantDrinkBillProvider()
        );

        while (true) {
            printMainMenu();

            int option = readIntInput("Enter Your Choice: ");
            System.out.println();

            try {
                switch (option) {
                    case 1:
                        handleFoodMenu(regNumber);
                        break;

                    case 2:
                        handleDrinksMenu(regNumber);
                        break;

                    case 3:
                        handleBillGeneration(regNumber, billProviders, date);
                        break;

                    case 4:
                        handlePreviousMenu(regNumber);
                        return;

                    case 5:
                        handleExit();
                        break;

                    default:
                        System.out.println("Please select correct option");
                        System.out.println(Constants.SEPARATOR);
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                System.out.println(Constants.SEPARATOR);
            }
        }
    }

    // ============================================
    // BILL PROVIDER CLASSES (kept as is - they work fine)
    // ============================================

    interface BillProvider {
        int getBillAmount();
        String getCategoryName();
    }

    static class FastFoodBillProvider implements BillProvider {
        @Override 
        public int getBillAmount() {
            return MyFastFood.FastfoodBill();
        }
        @Override 
        public String getCategoryName() { 
            return "Fast Food"; 
        }
    }

    static class DesiFoodBillProvider implements BillProvider {
        @Override 
        public int getBillAmount() {
            return MyDesiFood.DesifoodBill();
        }
        @Override 
        public String getCategoryName() { 
            return "Desi Food"; 
        }
    }

    static class SoftDrinkBillProvider implements BillProvider {
        @Override 
        public int getBillAmount() {
            try {
                return MySoftDrinks.SoftDrinkbill(Main.regNumber);
            } catch (Exception e) {
                System.out.println("Error in Soft Drinks: " + e.getMessage());
                return 0;
            }
        }
        @Override 
        public String getCategoryName() { 
            return "Soft Drinks"; 
        }
    }

    static class CoffeeBillProvider implements BillProvider {
        @Override 
        public int getBillAmount() {
            try {
                return MyCoffee.CoffeeBill(Main.regNumber);
            } catch (Exception e) {
                System.out.println("Error in Coffee: " + e.getMessage());
                return 0;
            }
        }
        @Override 
        public String getCategoryName() { 
            return "Coffee"; 
        }
    }

    static class JuicePlantDrinkBillProvider implements BillProvider {
        @Override 
        public int getBillAmount() {
            try {
                return MyJuiceOrPlantDrink.JuiceORPlantbill();
            } catch (Exception e) {
                System.out.println("Error in Juice/Plant: " + e.getMessage());
                return 0;
            }
        }
        @Override 
        public String getCategoryName() { 
            return "Juice / Plant Drink"; 
        }
    }
}