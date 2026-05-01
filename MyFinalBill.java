import java.util.*;
import java.io.*;

// ─────────────────────────────────────────────
// SINGLE RESPONSIBILITY PRINCIPLE
// Each class has one reason to change
// ─────────────────────────────────────────────

// 1. STUDENT SERVICE - Single Responsibility: Handle student data
interface StudentDataService {
    StudentInfo getStudentInfo(String regNumber) throws IOException;
}

class FileStudentDataService implements StudentDataService {
    private static final String STUDENT_DATA_DIR = "students_data/";

    @Override
    public StudentInfo getStudentInfo(String regNumber) throws IOException {
        File studentFile = new File(STUDENT_DATA_DIR + regNumber + ".txt");

        if (!studentFile.exists()) {
            return StudentInfo.createGuest(regNumber);
        }

        try (Scanner sc = new Scanner(studentFile)) {
            if (sc.hasNextLine()) {
                String line = sc.nextLine();
                String name = extractNameFromLine(line);
                return new StudentInfo(regNumber, name);
            }
        }

        return StudentInfo.createGuest(regNumber);
    }

    private String extractNameFromLine(String line) {
        if (line.contains(":")) {
            return line.split(":", 2)[1].trim();
        }
        return line.trim();
    }
}

class StudentInfo {
    private final String regNumber;
    private final String name;
    private final boolean isGuest;

    public StudentInfo(String regNumber, String name) {
        this.regNumber = regNumber;
        this.name = name;
        this.isGuest = false;
    }

    private StudentInfo(String regNumber, String name, boolean isGuest) {
        this.regNumber = regNumber;
        this.name = name;
        this.isGuest = isGuest;
    }

    public static StudentInfo createGuest(String regNumber) {
        return new StudentInfo(regNumber, "Guest", true);
    }

    public String getRegNumber() { return regNumber; }
    public String getName() { return name; }
    public boolean isGuest() { return isGuest; }
}

// 2. BILL FILE CONFIGURATION - Single Responsibility: Define bill files
class BillFileConfiguration {
    public static final String[] BILL_FILE_PATHS = {
            "cafe/CafeBills/BillFastfood.txt",
            "cafe/CafeBills/BillDesiFood.txt",
            "cafe/CafeBills/BillSoftDrinks.txt",
            "cafe/CafeBills/BillCoffee.txt",
            "cafe/CafeBills/BillJuiceOrPlant.txt"
    };

    public static final String FINAL_BILL_DIR = "cafe/CafeBills/FinalBills/";

    public static List<File> getBillFiles() {
        List<File> files = new ArrayList<>();
        for (String path : BILL_FILE_PATHS) {
            File file = new File(path);
            if (file.exists()) {
                files.add(file);
            }
        }
        return files;
    }
}

// 3. BILL LINE PARSER - Single Responsibility: Parse bill lines and calculate price correctly
interface BillLineParser {
    Optional<ParsedBillLine> parse(String line);
}

class DefaultBillLineParser implements BillLineParser {

    @Override
    public Optional<ParsedBillLine> parse(String line) {
        if (line == null || line.trim().isEmpty()) {
            return Optional.empty();
        }

        String trimmedLine = line.trim();
        String[] parts = trimmedLine.split("\\s+");

        // Extract price (last numeric value)
        int price = 0;
        int priceIndex = -1;

        for (int i = parts.length - 1; i >= 0; i--) {
            try {
                price = Integer.parseInt(parts[i]);
                priceIndex = i;
                break;
            } catch (NumberFormatException ignored) {}
        }

        // Extract quantity (second last numeric value if exists)
        int quantity = 1;
        if (priceIndex > 0) {
            try {
                quantity = Integer.parseInt(parts[priceIndex - 1]);
            } catch (NumberFormatException ignored) {}
        }

        // Extract item name (everything before quantity and price)
        String itemName = trimmedLine;
        if (priceIndex > 0) {
            int lastIndex = trimmedLine.lastIndexOf(parts[priceIndex - 1]);
            if (lastIndex > 0) {
                itemName = trimmedLine.substring(0, lastIndex).trim();
            }
        }

        // Calculate total price for this line (quantity * unit price)
        // Note: In the bill format, the last number is already the TOTAL price
        // So we don't need to multiply, we just use that number
        int totalPrice = price;

        return Optional.of(new ParsedBillLine(itemName, quantity, totalPrice, trimmedLine));
    }
}

class ParsedBillLine {
    private final String itemName;
    private final int quantity;
    private final int totalPrice;
    private final String rawLine;

    public ParsedBillLine(String itemName, int quantity, int totalPrice, String rawLine) {
        this.itemName = itemName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.rawLine = rawLine;
    }

    public String getItemName() { return itemName; }
    public int getQuantity() { return quantity; }
    public int getTotalPrice() { return totalPrice; }
    public String getRawLine() { return rawLine; }
}

// 4. BILL READER - Single Responsibility: Read bill files
interface BillReader {
    BillReadingResult readAllBills();
}

class FileBillReader implements BillReader {
    private final BillLineParser lineParser;
    private final List<File> billFiles;

    public FileBillReader(BillLineParser lineParser, List<File> billFiles) {
        this.lineParser = Objects.requireNonNull(lineParser);
        this.billFiles = new ArrayList<>(billFiles);
    }

    public FileBillReader() {
        this(new DefaultBillLineParser(), BillFileConfiguration.getBillFiles());
    }

    @Override
    public BillReadingResult readAllBills() {
        List<ParsedBillLine> allItems = new ArrayList<>();
        List<String> rawLines = new ArrayList<>();
        int totalAmount = 0;
        List<String> errors = new ArrayList<>();

        for (File file : billFiles) {
            try (Scanner reader = new Scanner(file)) {
                while (reader.hasNextLine()) {
                    String line = reader.nextLine().trim();
                    if (line.isEmpty()) continue;

                    rawLines.add(line);
                    Optional<ParsedBillLine> parsedLineOpt = lineParser.parse(line);

                    if (parsedLineOpt.isPresent()) {
                        ParsedBillLine parsedLine = parsedLineOpt.get();
                        allItems.add(parsedLine);
                        totalAmount += parsedLine.getTotalPrice(); // Sum all line totals
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
}

class BillReadingResult {
    private final List<ParsedBillLine> parsedItems;
    private final List<String> rawLines;
    private final int totalAmount;
    private final List<String> errors;
    private final Date readingTime;

    public BillReadingResult(List<ParsedBillLine> parsedItems, List<String> rawLines,
                             int totalAmount, List<String> errors) {
        this.parsedItems = new ArrayList<>(parsedItems);
        this.rawLines = new ArrayList<>(rawLines);
        this.totalAmount = totalAmount;
        this.errors = new ArrayList<>(errors);
        this.readingTime = new Date();
    }

    public List<ParsedBillLine> getParsedItems() { return Collections.unmodifiableList(parsedItems); }
    public List<String> getRawLines() { return Collections.unmodifiableList(rawLines); }
    public int getTotalAmount() { return totalAmount; }
    public List<String> getErrors() { return Collections.unmodifiableList(errors); }
    public Date getReadingTime() { return readingTime; }
    public boolean hasItems() { return !rawLines.isEmpty(); }
    public boolean hasErrors() { return !errors.isEmpty(); }
}

// 5. BALANCE VALIDATOR - Single Responsibility: Validate balance
class BalanceValidator {

    public ValidationResult validateBalance(int availableBalance, int requiredAmount) {
        if (requiredAmount <= 0) {
            return ValidationResult.invalid("No items to purchase");
        }

        if (availableBalance < requiredAmount) {
            return ValidationResult.invalid(
                    String.format("Insufficient Balance! You need %d TK but only have %d TK",
                            requiredAmount, availableBalance)
            );
        }

        int remainingBalance = availableBalance - requiredAmount;
        return ValidationResult.valid(remainingBalance);
    }
}

// 6. BILL PRINTER - Single Responsibility: Print bill to console
interface BillPrinter {
    void printBill(StudentInfo student, BillReadingResult billData, int remainingBalance);
}

class ConsoleBillPrinter implements BillPrinter {

    @Override
    public void printBill(StudentInfo student, BillReadingResult billData, int remainingBalance) {
        System.out.println("\n\t\t\t-----------------------------------");
        System.out.println("\t\t\tDate: " + billData.getReadingTime());
        System.out.println("\t\t\tName: " + student.getName());
        System.out.println("\t\t\tStudent ID: " + student.getRegNumber());

        if (student.isGuest()) {
            System.out.println("\t\t\t** GUEST USER **");
        }

        System.out.println("\t\t\t-----------Your Bill Is------------\n");
        System.out.println("\t\t\tItems                Qty      Price");

        // Print raw lines exactly as they appear in the bill files
        for (String rawLine : billData.getRawLines()) {
            System.out.println("\t\t\t" + rawLine);
        }

        System.out.println("\t\t\t-----------------------------------");
        System.out.println("\t\t\tTotal Bill:                 " + billData.getTotalAmount() + " TK");
        System.out.println("\t\t\tRemaining Balance:          " + remainingBalance + " TK");
        System.out.println("\t\t\t-----------------------------------");

        if (billData.hasErrors()) {
            System.out.println("\n\t\t\tWarnings:");
            for (String error : billData.getErrors()) {
                System.out.println("\t\t\t- " + error);
            }
        }
    }
}

// 7. BILL SAVER - Single Responsibility: Save bill to file
interface BillSaver {
    void saveBill(StudentInfo student, BillReadingResult billData) throws IOException;
}

class FileBillSaver implements BillSaver {
    private static final String FINAL_BILL_DIR = BillFileConfiguration.FINAL_BILL_DIR;

    @Override
    public void saveBill(StudentInfo student, BillReadingResult billData) throws IOException {
        File folder = new File(FINAL_BILL_DIR);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File billFile = new File(folder, student.getRegNumber() + ".txt");

        try (FileWriter fw = new FileWriter(billFile, true);
             PrintWriter pw = new PrintWriter(fw)) {

            pw.println("Date: " + billData.getReadingTime());
            pw.println("ID: " + student.getRegNumber());
            pw.println("Name: " + student.getName());
            pw.println("-----------------------------------");

            // Save raw lines exactly as they appear
            for (String rawLine : billData.getRawLines()) {
                pw.println(rawLine);
            }

            pw.println("-----------------------------------");
            pw.println("Total: " + billData.getTotalAmount() + " TK");
            pw.println();
        }
    }
}

// 8. TEMP FILE CLEANER - Single Responsibility: Clear temporary files
interface TempFileCleaner {
    void clearTempFiles();
    void clearTempFiles(List<String> errors);
}

class BillTempFileCleaner implements TempFileCleaner {

    @Override
    public void clearTempFiles() {
        clearTempFiles(new ArrayList<>());
    }

    @Override
    public void clearTempFiles(List<String> errors) {
        for (String path : BillFileConfiguration.BILL_FILE_PATHS) {
            try (PrintWriter writer = new PrintWriter(path)) {
                writer.print("");
            } catch (FileNotFoundException e) {
                errors.add("Could not clear temp file: " + path);
            } catch (Exception ignored) {
                // Ignore other errors
            }
        }
    }
}

// 9. VALUE OBJECTS for results
class ValidationResult {
    private final boolean valid;
    private final String message;
    private final int remainingBalance;

    private ValidationResult(boolean valid, String message, int remainingBalance) {
        this.valid = valid;
        this.message = message;
        this.remainingBalance = remainingBalance;
    }

    public static ValidationResult valid(int remainingBalance) {
        return new ValidationResult(true, null, remainingBalance);
    }

    public static ValidationResult invalid(String message) {
        return new ValidationResult(false, message, 0);
    }

    public boolean isValid() { return valid; }
    public String getMessage() { return message; }
    public int getRemainingBalance() { return remainingBalance; }
}

class FinalBillResult {
    private final int remainingBalance;
    private final int totalBill;
    private final int itemCount;
    private final boolean success;
    private final String message;
    private final List<String> warnings;

    private FinalBillResult(int remainingBalance, int totalBill, int itemCount,
                            boolean success, String message, List<String> warnings) {
        this.remainingBalance = remainingBalance;
        this.totalBill = totalBill;
        this.itemCount = itemCount;
        this.success = success;
        this.message = message;
        this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
    }

    public static FinalBillResult success(int remainingBalance, int totalBill,
                                          int itemCount, List<String> warnings) {
        return new FinalBillResult(remainingBalance, totalBill, itemCount,
                true, "Bill generated successfully", warnings);
    }

    public static FinalBillResult noItems(int initialBalance) {
        return new FinalBillResult(initialBalance, 0, 0, false,
                "No items to bill", null);
    }

    public static FinalBillResult insufficientBalance(int initialBalance, int requiredAmount) {
        return new FinalBillResult(initialBalance, requiredAmount, 0, false,
                "Insufficient balance", null);
    }

    public static FinalBillResult error(int initialBalance, String errorMessage) {
        return new FinalBillResult(initialBalance, 0, 0, false,
                "Error: " + errorMessage, null);
    }

    public int getRemainingBalance() { return remainingBalance; }
    public int getTotalBill() { return totalBill; }
    public int getItemCount() { return itemCount; }
    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<String> getWarnings() { return Collections.unmodifiableList(warnings); }
}

// 10. FINAL BILL SERVICE - Single Responsibility: Orchestrate final bill generation
class FinalBillService {
    private final StudentDataService studentService;
    private final BillReader billReader;
    private final BalanceValidator balanceValidator;
    private final BillPrinter billPrinter;
    private final BillSaver billSaver;
    private final TempFileCleaner tempFileCleaner;

    public FinalBillService(StudentDataService studentService,
                            BillReader billReader,
                            BalanceValidator balanceValidator,
                            BillPrinter billPrinter,
                            BillSaver billSaver,
                            TempFileCleaner tempFileCleaner) {
        this.studentService = Objects.requireNonNull(studentService);
        this.billReader = Objects.requireNonNull(billReader);
        this.balanceValidator = Objects.requireNonNull(balanceValidator);
        this.billPrinter = Objects.requireNonNull(billPrinter);
        this.billSaver = Objects.requireNonNull(billSaver);
        this.tempFileCleaner = Objects.requireNonNull(tempFileCleaner);
    }

    public FinalBillResult generateFinalBill(String regNumber, int initialAmount) {
        try {
            // Get student information
            StudentInfo student = studentService.getStudentInfo(regNumber);

            // Read all bills
            BillReadingResult billData = billReader.readAllBills();

            // Check if there are items
            if (!billData.hasItems()) {
                System.out.println("No items found in orders.");
                return FinalBillResult.noItems(initialAmount);
            }

            // Validate balance
            ValidationResult validationResult = balanceValidator.validateBalance(
                    initialAmount, billData.getTotalAmount()
            );

            if (!validationResult.isValid()) {
                System.out.println(validationResult.getMessage());
                return FinalBillResult.insufficientBalance(initialAmount, billData.getTotalAmount());
            }

            // Print bill to console
            billPrinter.printBill(student, billData, validationResult.getRemainingBalance());

            // Save bill to file
            billSaver.saveBill(student, billData);

            // Clear temporary files
            List<String> cleanupErrors = new ArrayList<>();
            tempFileCleaner.clearTempFiles(cleanupErrors);

            return FinalBillResult.success(
                    validationResult.getRemainingBalance(),
                    billData.getTotalAmount(),
                    billData.getRawLines().size(),
                    cleanupErrors
            );

        } catch (IOException e) {
            System.err.println("Error generating final bill: " + e.getMessage());
            return FinalBillResult.error(initialAmount, e.getMessage());
        }
    }
}

// 11. BUILDER PATTERN - For flexible service construction
class FinalBillServiceBuilder {
    private StudentDataService studentService;
    private BillReader billReader;
    private BalanceValidator balanceValidator;
    private BillPrinter billPrinter;
    private BillSaver billSaver;
    private TempFileCleaner tempFileCleaner;

    public FinalBillServiceBuilder withDefaultServices() {
        this.studentService = new FileStudentDataService();
        this.billReader = new FileBillReader();
        this.balanceValidator = new BalanceValidator();
        this.billPrinter = new ConsoleBillPrinter();
        this.billSaver = new FileBillSaver();
        this.tempFileCleaner = new BillTempFileCleaner();
        return this;
    }

    public FinalBillServiceBuilder withStudentService(StudentDataService service) {
        this.studentService = service;
        return this;
    }

    public FinalBillServiceBuilder withBillReader(BillReader reader) {
        this.billReader = reader;
        return this;
    }

    public FinalBillServiceBuilder withBalanceValidator(BalanceValidator validator) {
        this.balanceValidator = validator;
        return this;
    }

    public FinalBillServiceBuilder withBillPrinter(BillPrinter printer) {
        this.billPrinter = printer;
        return this;
    }

    public FinalBillServiceBuilder withBillSaver(BillSaver saver) {
        this.billSaver = saver;
        return this;
    }

    public FinalBillServiceBuilder withTempFileCleaner(TempFileCleaner cleaner) {
        this.tempFileCleaner = cleaner;
        return this;
    }

    public FinalBillService build() {
        if (studentService == null) studentService = new FileStudentDataService();
        if (billReader == null) billReader = new FileBillReader();
        if (balanceValidator == null) balanceValidator = new BalanceValidator();
        if (billPrinter == null) billPrinter = new ConsoleBillPrinter();
        if (billSaver == null) billSaver = new FileBillSaver();
        if (tempFileCleaner == null) tempFileCleaner = new BillTempFileCleaner();

        return new FinalBillService(
                studentService, billReader, balanceValidator,
                billPrinter, billSaver, tempFileCleaner
        );
    }
}

// 12. FACTORY for creating services
class FinalBillServiceFactory {
    private static FinalBillService defaultService;

    public static FinalBillService createDefaultService() {
        if (defaultService == null) {
            defaultService = new FinalBillServiceBuilder()
                    .withDefaultServices()
                    .build();
        }
        return defaultService;
    }

    public static FinalBillService createFreshService() {
        return new FinalBillServiceBuilder()
                .withDefaultServices()
                .build();
    }
}

// 13. MAIN PUBLIC CLASS - Backward compatibility with SOLID principles
public class MyFinalBill {
    private static FinalBillService createDefaultService() {
        return FinalBillServiceFactory.createDefaultService();
    }

    /**
     * Main entry point - maintains backward compatibility
     * Now correctly calculates total by summing all line totals
     *
     * @param regNumber student's registration number
     * @param amount initial account balance
     * @return remaining balance after payment
     */
    public static int finalBill(String regNumber, int amount) {
        FinalBillService service = createDefaultService();
        FinalBillResult result = service.generateFinalBill(regNumber, amount);

        if (result.isSuccess()) {
            return result.getRemainingBalance();
        } else {
            System.out.println(result.getMessage());
            return amount;
        }
    }

    /**
     * Enhanced version that returns detailed result object
     */
    public static FinalBillResult finalBillWithDetails(String regNumber, int amount) {
        FinalBillService service = createDefaultService();
        return service.generateFinalBill(regNumber, amount);
    }

    /**
     * Utility method to clear all temporary bill files
     */
    public static void clearAllTempFiles() {
        new BillTempFileCleaner().clearTempFiles();
    }

    /**
     * Utility method to check if there are pending bills
     */
    public static boolean hasPendingBills() {
        return !BillFileConfiguration.getBillFiles().isEmpty();
    }

    /**
     * Utility method to get pending bill total
     */
    public static int getPendingBillTotal() {
        BillReader reader = new FileBillReader();
        BillReadingResult result = reader.readAllBills();
        return result.getTotalAmount();
    }
}