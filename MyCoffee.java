import java.util.*;
import java.io.*;

// ─────────────────────────────────────────────
// SINGLE RESPONSIBILITY PRINCIPLE
// Each class has one reason to change
// ─────────────────────────────────────────────

// 1. ENUM for Coffee Items - Single Responsibility: Define coffee items
enum CoffeeItem {
    ESPRESSO("Espresso", 120, 1),
    CAPPUCCINO("Cappuccino", 150, 2),
    LATTE("Latte", 170, 3),
    MOCHA("Mocha", 200, 4),
    BLACK_COFFEE("Black Coffee", 100, 5);

    private final String displayName;
    private final int price;
    private final int code;

    CoffeeItem(String displayName, int price, int code) {
        this.displayName = displayName;
        this.price = price;
        this.code = code;
    }

    public String getDisplayName() { return displayName; }
    public int getPrice() { return price; }
    public int getCode() { return code; }

    public static Optional<CoffeeItem> fromCode(int code) {
        return Arrays.stream(values())
                .filter(item -> item.code == code)
                .findFirst();
    }

    public static List<CoffeeItem> getAllItems() {
        return Arrays.asList(values());
    }
}

// 2. ORDER ITEM - Single Responsibility: Represent an order item (Immutable)
class CoffeeOrder {
    private final CoffeeItem item;
    private final int quantity;
    private final int totalPrice;

    public CoffeeOrder(CoffeeItem item, int quantity) {
        this.item = Objects.requireNonNull(item);
        this.quantity = validateQuantity(quantity);
        this.totalPrice = this.quantity * item.getPrice();
    }

    private int validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return quantity;
    }

    public CoffeeItem getItem() { return item; }
    public int getQuantity() { return quantity; }
    public int getTotalPrice() { return totalPrice; }

    public String getDisplayString() {
        return String.format("%-15s | Quantity: %d | Subtotal: %d",
                item.getDisplayName(), quantity, totalPrice);
    }

    public String getBillLine() {
        return String.format("%-22s %3d    %6d",
                item.getDisplayName(), quantity, totalPrice);
    }

    public String getPersonalBillLine() {
        return String.format("You've ordered a %s\nQuantity: %d\n",
                item.getDisplayName(), quantity);
    }
}

// 3. MENU DISPLAYER - Single Responsibility: Display menu
interface CoffeeMenuDisplayer {
    void displayMenu();
}

class ConsoleCoffeeMenuDisplayer implements CoffeeMenuDisplayer {

    @Override
    public void displayMenu() {
        System.out.println("\n===== COFFEE MENU =====");
        System.out.println("1. Espresso      - 120");
        System.out.println("2. Cappuccino    - 150");
        System.out.println("3. Latte         - 170");
        System.out.println("4. Mocha         - 200");
        System.out.println("5. Black Coffee  - 100");
        System.out.println("0. Exit");
        System.out.println("=======================");
    }
}

// 4. INPUT READER - Single Responsibility: Handle user input
interface CoffeeInputReader {
    int readInt();
    int readPositiveInt();
    int readQuantity();
    Optional<Integer> readChoice();
    void close();
}

class CoffeeConsoleInputReader implements CoffeeInputReader {
    private final Scanner scanner;

    public CoffeeConsoleInputReader() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public int readInt() {
        while (true) {
            try {
                return scanner.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    @Override
    public int readPositiveInt() {
        while (true) {
            int val = readInt();
            if (val >= 0) return val;
            System.out.println("Please enter a non-negative number.");
        }
    }

    @Override
    public int readQuantity() {
        System.out.print("Enter quantity: ");
        while (true) {
            int qty = readInt();
            if (qty > 0) return qty;
            System.out.println("Invalid quantity! Quantity must be positive.");
            System.out.print("Enter quantity: ");
        }
    }

    @Override
    public Optional<Integer> readChoice() {
        System.out.print("Enter your choice: ");
        int choice = readInt();
        return choice == 0 ? Optional.empty() : Optional.of(choice);
    }

    @Override
    public void close() {
        // Don't close System.in
    }
}

// 5. ORDER PROCESSOR - Single Responsibility: Process orders
class CoffeeOrderProcessor {
    private final CoffeeInputReader inputReader;
    private final List<CoffeeOrder> orders;
    private int grandTotal;

    public CoffeeOrderProcessor(CoffeeInputReader inputReader) {
        this.inputReader = Objects.requireNonNull(inputReader);
        this.orders = new ArrayList<>();
        this.grandTotal = 0;
    }

    public Optional<CoffeeOrder> processOrder() {
        Optional<Integer> choiceOpt = inputReader.readChoice();

        if (!choiceOpt.isPresent()) {
            return Optional.empty();
        }

        int choice = choiceOpt.get();
        Optional<CoffeeItem> selectedItem = CoffeeItem.fromCode(choice);

        if (!selectedItem.isPresent()) {
            System.out.println("Invalid choice!");
            return Optional.empty();
        }

        int quantity = inputReader.readQuantity();
        CoffeeOrder order = new CoffeeOrder(selectedItem.get(), quantity);

        System.out.println(order.getDisplayString());
        System.out.println("-----------------------------");

        return Optional.of(order);
    }

    public void addOrder(CoffeeOrder order) {
        orders.add(order);
        grandTotal += order.getTotalPrice();
    }

    public List<CoffeeOrder> getOrders() {
        return Collections.unmodifiableList(orders);
    }

    public int getGrandTotal() {
        return grandTotal;
    }

    public void clear() {
        orders.clear();
        grandTotal = 0;
    }

    public boolean hasOrders() {
        return !orders.isEmpty();
    }
}

// 6. BILL WRITER - Single Responsibility: Handle bill writing
interface CoffeeBillWriter {
    void writeHeader(String regNumber, Date date) throws IOException;
    void writeOrder(CoffeeOrder order) throws IOException;
    void writeTotal(int total) throws IOException;
    void close() throws IOException;
}

class CoffeePersonalBillWriter implements CoffeeBillWriter {
    private final FileWriter writer;
    private static final String BASE_DIR = "cafe/CafeBills/Coffee/";

    public CoffeePersonalBillWriter(String regNumber) throws IOException {
        File dir = new File(BASE_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, regNumber + ".txt");
        file.createNewFile();
        this.writer = new FileWriter(file, true);
    }

    @Override
    public void writeHeader(String regNumber, Date date) throws IOException {
        writer.write("\nDate: " + date + "\n");
    }

    @Override
    public void writeOrder(CoffeeOrder order) throws IOException {
        writer.write(order.getPersonalBillLine());
    }

    @Override
    public void writeTotal(int total) throws IOException {
        writer.write("Total Amount: " + total + "\n\n");
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}

class CoffeeSharedBillWriter implements CoffeeBillWriter {
    private final FileWriter writer;
    private static final String SHARED_BILL_FILE = "cafe/CafeBills/BillCoffee.txt";

    public CoffeeSharedBillWriter() throws IOException {
        File dir = new File("cafe/CafeBills");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        this.writer = new FileWriter(new File(dir, "BillCoffee.txt"), true);
    }

    @Override
    public void writeHeader(String regNumber, Date date) throws IOException {
        writer.write("\nCoffee Order - Date: " + date + "\n");
        writer.write("Registration: " + regNumber + "\n");
        writer.write("----------------------------------------\n");
    }

    @Override
    public void writeOrder(CoffeeOrder order) throws IOException {
        writer.write(order.getBillLine() + "\n");
    }

    @Override
    public void writeTotal(int total) throws IOException {
        writer.write("----------------------------------------\n");
        writer.write("Total: " + total + "\n\n");
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}

// 7. BILL GENERATION SERVICE - Single Responsibility: Orchestrate bill generation
class CoffeeBillGenerationService {
    private final CoffeeMenuDisplayer menuDisplayer;
    private final CoffeeInputReader inputReader;
    private final CoffeeOrderProcessor orderProcessor;
    private final CoffeeBillWriter personalWriter;
    private final CoffeeBillWriter sharedWriter;
    private final String regNumber;
    private final Date date;

    public CoffeeBillGenerationService(String regNumber,
                                       CoffeeMenuDisplayer menuDisplayer,
                                       CoffeeInputReader inputReader,
                                       CoffeeOrderProcessor orderProcessor,
                                       CoffeeBillWriter personalWriter,
                                       CoffeeBillWriter sharedWriter) {
        this.regNumber = Objects.requireNonNull(regNumber);
        this.menuDisplayer = Objects.requireNonNull(menuDisplayer);
        this.inputReader = Objects.requireNonNull(inputReader);
        this.orderProcessor = Objects.requireNonNull(orderProcessor);
        this.personalWriter = Objects.requireNonNull(personalWriter);
        this.sharedWriter = Objects.requireNonNull(sharedWriter);
        this.date = new Date();
    }

    public int generateBill() throws IOException {
        orderProcessor.clear();

        personalWriter.writeHeader(regNumber, date);
        sharedWriter.writeHeader(regNumber, date);

        while (true) {
            menuDisplayer.displayMenu();

            Optional<CoffeeOrder> orderOpt = orderProcessor.processOrder();

            if (!orderOpt.isPresent()) {
                break;
            }

            CoffeeOrder order = orderOpt.get();
            orderProcessor.addOrder(order);

            personalWriter.writeOrder(order);
            sharedWriter.writeOrder(order);
        }

        int grandTotal = orderProcessor.getGrandTotal();

        if (orderProcessor.hasOrders()) {
            personalWriter.writeTotal(grandTotal);
            sharedWriter.writeTotal(grandTotal);
        }

        return grandTotal;
    }

    public void close() throws IOException {
        personalWriter.close();
        sharedWriter.close();
    }
}

// 8. SERVICE FACTORY - Dependency Inversion
class CoffeeServiceFactory {

    public static CoffeeBillGenerationService createService(String regNumber) throws IOException {
        CoffeeMenuDisplayer menuDisplayer = new ConsoleCoffeeMenuDisplayer();
        CoffeeInputReader inputReader = new CoffeeConsoleInputReader();
        CoffeeOrderProcessor orderProcessor = new CoffeeOrderProcessor(inputReader);
        CoffeeBillWriter personalWriter = new CoffeePersonalBillWriter(regNumber);
        CoffeeBillWriter sharedWriter = new CoffeeSharedBillWriter();

        return new CoffeeBillGenerationService(
                regNumber, menuDisplayer, inputReader,
                orderProcessor, personalWriter, sharedWriter
        );
    }

    public static CoffeeBillGenerationService createServiceWithoutFileWriting(String regNumber) {
        CoffeeMenuDisplayer menuDisplayer = new ConsoleCoffeeMenuDisplayer();
        CoffeeInputReader inputReader = new CoffeeConsoleInputReader();
        CoffeeOrderProcessor orderProcessor = new CoffeeOrderProcessor(inputReader);

        return new CoffeeBillGenerationService(
                regNumber, menuDisplayer, inputReader,
                orderProcessor, null, null
        ) {
            @Override
            public int generateBill() throws IOException {
                orderProcessor.clear();

                while (true) {
                    menuDisplayer.displayMenu();

                    Optional<CoffeeOrder> orderOpt = orderProcessor.processOrder();

                    if (!orderOpt.isPresent()) {
                        break;
                    }

                    CoffeeOrder order = orderOpt.get();
                    orderProcessor.addOrder(order);
                }

                return orderProcessor.getGrandTotal();
            }

            @Override
            public void close() {
                // No files to close
            }
        };
    }
}

// 9. MAIN PUBLIC CLASS - Backward compatibility
public class MyCoffee {

    // Static fields for backward compatibility
    static Scanner input = new Scanner(System.in);
    static int totalAmount = 0;

    /**
     * Main entry point - maintains backward compatibility
     */
    public static void main(String[] args) {
        System.out.print("Enter your registration number: ");
        String regNumber = input.nextLine();

        int total = CoffeeBill(regNumber);

        if (total > 0) {
            System.out.println("TOTAL COFFEE BILL: " + total);
            System.out.println("Thank you ☕");
        }
    }

    /**
     * Clean bill method - returns total for billing system
     */
    public static int CoffeeBill() {
        String regNumber = getRegistrationNumber();
        return CoffeeBill(regNumber);
    }

    /**
     * Overloaded method with registration number
     */
    public static int CoffeeBill(String regNumber) {
        try {
            // Reset static field for new session
            totalAmount = 0;

            CoffeeBillGenerationService service = CoffeeServiceFactory.createService(regNumber);
            int total = service.generateBill();
            service.close();

            // Update static field for backward compatibility
            totalAmount = total;

            return total;

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Simple version without file writing (for testing)
     */
    public static int CoffeeBillSimple(String regNumber) {
        CoffeeBillGenerationService service = CoffeeServiceFactory.createServiceWithoutFileWriting(regNumber);
        try {
            return service.generateBill();
        } catch (IOException e) {
            return 0;
        }
    }

    /**
     * Original showMenu method - maintained for backward compatibility
     */
    static void showMenu() {
        ConsoleCoffeeMenuDisplayer displayer = new ConsoleCoffeeMenuDisplayer();
        displayer.displayMenu();
    }

    /**
     * Original getPrice method - maintained for backward compatibility
     */
    static int getPrice(int choice) {
        Optional<CoffeeItem> item = CoffeeItem.fromCode(choice);
        return item.map(CoffeeItem::getPrice).orElse(0);
    }

    /**
     * Original getName method - maintained for backward compatibility
     */
    static String getName(int choice) {
        Optional<CoffeeItem> item = CoffeeItem.fromCode(choice);
        return item.map(CoffeeItem::getDisplayName).orElse("Unknown");
    }

    /**
     * Original CoffeeBill method - maintained for backward compatibility
     */
    public int CoffeeBillOld() {
        throw new UnsupportedOperationException(
                "Use static CoffeeBill() or CoffeeBill(String regNumber) instead."
        );
    }

    // Helper methods
    private static String getRegistrationNumber() {
        // Try Main.regNumber first
        try {
            if (Main.regNumber != null && !Main.regNumber.isEmpty()) {
                return Main.regNumber;
            }
        } catch (Exception e) {
            // Main class might not exist
        }

        // Try system property
        String regNumber = System.getProperty("user.regNumber");
        if (regNumber != null && !regNumber.isEmpty()) {
            return regNumber;
        }

        // Try environment variable
        regNumber = System.getenv("STUDENT_REG_NUMBER");
        if (regNumber != null && !regNumber.isEmpty()) {
            return regNumber;
        }

        return "UNKNOWN";
    }
}

