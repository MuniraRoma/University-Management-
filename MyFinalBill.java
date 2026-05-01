import java.util.*;
import java.io.*;

public class MyFinalBill {

    // Constants for magic strings
    private static final class Constants {
        private static final String BILL_DIR = "cafe/CafeBills";
        private static final String FINAL_BILL_DIR = BILL_DIR + "/FinalBills/";
        private static final String STUDENT_DATA_DIR = "students_data/";

        private static final String[] BILL_FILE_PATHS = {
            BILL_DIR + "/BillFastfood.txt",
            BILL_DIR + "/BillDesiFood.txt",
            BILL_DIR + "/BillSoftDrinks.txt",
            BILL_DIR + "/BillCoffee.txt",
            BILL_DIR + "/BillJuiceOrPlant.txt"
        };
    }

    // Simple data classes
    static class StudentInfo {
        final String regNumber;
        final String name;
        final boolean isGuest;

        StudentInfo(String regNumber, String name) {
            this.regNumber = regNumber;
            this.name = name;
            this.isGuest = false;
        }

        StudentInfo(String regNumber, String name, boolean isGuest) {
            this.regNumber = regNumber;
            this.name = name;
            this.isGuest = isGuest;
        }

        static StudentInfo createGuest(String regNumber) {
            return new StudentInfo(regNumber, "Guest", true);
        }
    }

    static class ParsedBillLine {
        final String itemName;
        final int quantity;
        final int totalPrice;
        final String rawLine;

        ParsedBillLine(String itemName, int quantity, int totalPrice, String rawLine) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.rawLine = rawLine;
        }
    }

    static class BillReadingResult {
        final List<ParsedBillLine> parsedItems;
        final List<String> rawLines;
        final int totalAmount;
        final List<String> errors;
        final Date readingTime;

        BillReadingResult(List<ParsedBillLine> parsedItems, List<String> rawLines,
                          int totalAmount, List<String> errors) {
            this.parsedItems = new ArrayList<>(parsedItems);
            this.rawLines = new ArrayList<>(rawLines);
            this.totalAmount = totalAmount;
            this.errors = new ArrayList<>(errors);
            this.readingTime = new Date();
        }

        boolean hasItems() {
            return !rawLines.isEmpty();
        }
    }

    // EXTRACT METHOD - Student data reading
    private static StudentInfo readStudentInfo(String regNumber) {
        File studentFile = new File(Constants.STUDENT_DATA_DIR + regNumber + ".txt");

        if (!studentFile.exists()) {
            return StudentInfo.createGuest(regNumber);
        }

        try (Scanner sc = new Scanner(studentFile)) {
            if (sc.hasNextLine()) {
                String line = sc.nextLine();
                String name = extractNameFromLine(line);
                return new StudentInfo(regNumber, name);
            }
        } catch (IOException e) {
            System.out.println("Error reading student file: " + e.getMessage());
        }

        return StudentInfo.createGuest(regNumber);
    }

    private static String extractNameFromLine(String line) {
        if (line.contains(":")) {
            return line.split(":", 2)[1].trim();
        }
        return line.trim();
    }

    // EXTRACT METHOD - Parse bill line
    private static Optional<ParsedBillLine> parseBillLine(String line) {
        if (line == null || line.trim().isEmpty()) {
            return Optional.empty();
        }

        String trimmedLine = line.trim();
        String[] parts = trimmedLine.split("\\s+");

        int price = 0;
        int priceIndex = -1;

        for (int i = parts.length - 1; i >= 0; i--) {
            try {
                price = Integer.parseInt(parts[i]);
                priceIndex = i;
                break;
            } catch (NumberFormatException ignored) {
            }
        }

        int quantity = 1;
        if (priceIndex > 0) {
            try {
                quantity = Integer.parseInt(parts[priceIndex - 1]);
            } catch (NumberFormatException ignored) {
            }
        }

        String itemName = trimmedLine;
        if (priceIndex > 0) {
            int lastIndex = trimmedLine.lastIndexOf(parts[priceIndex - 1]);
            if (lastIndex > 0) {
                itemName = trimmedLine.substring(0, lastIndex).trim();
            }
        }

        return Optional.of(new ParsedBillLine(itemName, quantity, price, trimmedLine));
    }

    // EXTRACT METHOD - Read all bills
    private static BillReadingResult readAllBills() {
        List<ParsedBillLine> allItems = new ArrayList<>();
        List<String> rawLines = new ArrayList<>();
        int totalAmount = 0;
        List<String> errors = new ArrayList<>();

        for (String path : Constants.BILL_FILE_PATHS) {
            File file = new File(path);
            if (!file.exists()) {
                continue;
            }

            try (Scanner reader = new Scanner(file)) {
                while (reader.hasNextLine()) {
                    String line = reader.nextLine().trim();
                    if (line.isEmpty()) continue;

                    rawLines.add(line);
                    Optional<ParsedBillLine> parsedLineOpt = parseBillLine(line);

                    if (parsedLineOpt.isPresent()) {
                        ParsedBillLine parsedLine = parsedLineOpt.get();
                        allItems.add(parsedLine);
                        totalAmount += parsedLine.totalPrice;
                    }
                }
            } catch (FileNotFoundException e) {
                errors.add("File not found: " + file.getName());
            } catch (Exception e) {
                errors.add("Error reading " + file.getName() + ": " + e.getMessage());
            }
        }

        return new BillReadingResult(allItems, rawLines, totalAmount, errors);
    }

    // EXTRACT METHOD - Clear temp files
    private static void clearTempFiles() {
        for (String path : Constants.BILL_FILE_PATHS) {
            try (PrintWriter writer = new PrintWriter(path)) {
                writer.print("");
            } catch (FileNotFoundException e) {
                System.out.println("Could not clear temp file: " + path);
            } catch (Exception ignored) {
            }
        }
    }

    // EXTRACT METHOD - Save final bill to file
    private static void saveFinalBill(String regNumber, StudentInfo student, List<String> rawLines, int total) {
        File folder = new File(Constants.FINAL_BILL_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File billFile = new File(Constants.FINAL_BILL_DIR + regNumber + ".txt");

        try (FileWriter fw = new FileWriter(billFile, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("Date: " + new Date());
            pw.println("ID: " + student.regNumber);
            pw.println("Name: " + student.name);
            pw.println("-----------------------------------");

            for (String rawLine : rawLines) {
                pw.println(rawLine);
            }

            pw.println("-----------------------------------");
            pw.println("Total: " + total + " TK");
            pw.println();

        } catch (IOException e) {
            System.out.println("Error saving final bill: " + e.getMessage());
        }
    }

    // EXTRACT METHOD - Print bill to console
    private static void printBillToConsole(StudentInfo student, BillReadingResult billData, int remainingBalance) {
        System.out.println("\n\t\t\t-----------------------------------");
        System.out.println("\t\t\tDate: " + billData.readingTime);
        System.out.println("\t\t\tName: " + student.name);
        System.out.println("\t\t\tStudent ID: " + student.regNumber);

        if (student.isGuest) {
            System.out.println("\t\t\t** GUEST USER **");
        }

        System.out.println("\t\t\t-----------Your Bill Is------------\n");
        System.out.println("\t\t\tItems                Qty      Price");

        for (String rawLine : billData.rawLines) {
            System.out.println("\t\t\t" + rawLine);
        }

        System.out.println("\t\t\t-----------------------------------");
        System.out.println("\t\t\tTotal Bill:                 " + billData.totalAmount + " TK");
        System.out.println("\t\t\tRemaining Balance:          " + remainingBalance + " TK");
        System.out.println("\t\t\t-----------------------------------");

        if (billData.errors.size() > 0) {
            System.out.println("\n\t\t\tWarnings:");
            for (String error : billData.errors) {
                System.out.println("\t\t\t- " + error);
            }
        }
    }

    // REFACTORED MAIN METHOD
    public static int finalBill(String regNumber, int amount) {
        if (regNumber == null || regNumber.isEmpty()) {
            System.out.print("Enter registration number: ");
            Scanner scanner = new Scanner(System.in);
            regNumber = scanner.nextLine().trim();
        }

        // Get student information
        StudentInfo student = readStudentInfo(regNumber);

        // Read all bills
        BillReadingResult billData = readAllBills();

        // Check if there are items
        if (!billData.hasItems()) {
            System.out.println("No items found in orders.");
            return amount;
        }

        // Validate balance
        if (amount < billData.totalAmount) {
            System.out.printf("Insufficient Balance! You need %d TK but only have %d TK\n",
                    billData.totalAmount, amount);
            return amount;
        }

        int remainingBalance = amount - billData.totalAmount;

        // Print bill to console
        printBillToConsole(student, billData, remainingBalance);

        // Save bill to file
        saveFinalBill(regNumber, student, billData.rawLines, billData.totalAmount);

        // Clear temporary files
        clearTempFiles();

        return remainingBalance;
    }

    // Utility method to get pending bill total
    public static int getPendingBillTotal() {
        BillReadingResult result = readAllBills();
        return result.totalAmount;
    }

    // Utility method to check if there are pending bills
    public static boolean hasPendingBills() {
        return getPendingBillTotal() > 0;
    }
}