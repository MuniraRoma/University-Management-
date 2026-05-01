import java.util.*;

// ─────────────────────────────────────────────
// SINGLE RESPONSIBILITY PRINCIPLE
// Each class has one reason to change
// ─────────────────────────────────────────────

// 1. ENUM for Drink Categories - Single Responsibility: Define drink types
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

    public String getDisplayName() { return displayName; }
    public int getCode() { return code; }

    public static Optional<DrinkCategory> fromCode(int code) {
        return Arrays.stream(values())
                .filter(category -> category.code == code)
                .findFirst();
    }

    public static List<DrinkCategory> getActiveCategories() {
        return Arrays.asList(SOFT_DRINKS, COFFEE, JUICE_PLANT);
    }
}

// 2. INTERFACE SEGREGATION - Small, focused interfaces
interface DrinkMenuDisplayer {
    void display();
}

class DrinkCategoryConsoleMenuDisplayer implements DrinkMenuDisplayer {
    @Override
    public void display() {
        System.out.println("\nPlease Select a Drink you want:");
        System.out.println("1. Soft Drinks");
        System.out.println("2. Coffee");
        System.out.println("3. Juice or Plant Drinks");
        System.out.println("0. Nothing");
    }
}

interface DrinkInputReader {
    int readInt();
    void clearBuffer();
    void close();
}

class DrinkConsoleInputReader implements DrinkInputReader {
    private final Scanner scanner;

    public DrinkConsoleInputReader() {
        this.scanner = new Scanner(System.in);
    }

    @Override
    public int readInt() {
        while (true) {
            try {
                System.out.print("Enter Your Choice: ");
                int val = scanner.nextInt();
                scanner.nextLine(); // consume newline
                return val;
            } catch (InputMismatchException e) {
                System.out.println("Invalid input! Please enter a number.");
                scanner.nextLine(); // clear buffer
            }
        }
    }

    @Override
    public void clearBuffer() {
        scanner.nextLine();
    }

    @Override
    public void close() {
        // Don't close System.in
    }
}

// 3. STRATEGY PATTERN - Open/Closed Principle
interface DrinkCategoryHandler {
    void handle(String regNumber);
    DrinkCategory getCategory();
    String getHandlerName();
}

abstract class BaseDrinkHandler implements DrinkCategoryHandler {
    protected final String handlerName;

    protected BaseDrinkHandler(String handlerName) {
        this.handlerName = handlerName;
    }

    @Override
    public String getHandlerName() {
        return handlerName;
    }

    protected void printSeparator() {
        System.out.println("********************************************");
    }
}

class SoftDrinksHandler extends BaseDrinkHandler {

    public SoftDrinksHandler() {
        super("Soft Drinks Handler");
    }

    @Override
    public void handle(String regNumber) {
        System.out.println("\n→ Entering Soft Drinks section...");

        // Call static method - proper integration
        int total = MySoftDrinks.SoftDrinkbill(regNumber);

        if (total > 0) {
            System.out.println("Soft Drinks Total: " + total + " TK");
        }

        printSeparator();
    }

    @Override
    public DrinkCategory getCategory() {
        return DrinkCategory.SOFT_DRINKS;
    }
}

class CoffeeHandler extends BaseDrinkHandler {

    public CoffeeHandler() {
        super("Coffee Handler");
    }

    @Override
    public void handle(String regNumber) {
        System.out.println("\n→ Entering Coffee section...");

        // Call static method - proper integration
        int total = MyCoffee.CoffeeBill(regNumber);

        if (total > 0) {
            System.out.println("Coffee Total: " + total + " TK");
        }

        printSeparator();
    }

    @Override
    public DrinkCategory getCategory() {
        return DrinkCategory.COFFEE;
    }
}

class JuicePlantHandler extends BaseDrinkHandler {

    public JuicePlantHandler() {
        super("Juice/Plant Handler");
    }

    @Override
    public void handle(String regNumber) {
        System.out.println("\n→ Entering Juice or Plant Drinks section...");

        // Call static method - proper integration
        int total = MyJuiceOrPlantDrink.JuiceORPlantbill(regNumber);

        if (total > 0) {
            System.out.println("Juice/Plant Drinks Total: " + total + " TK");
        }

        printSeparator();
    }

    @Override
    public DrinkCategory getCategory() {
        return DrinkCategory.JUICE_PLANT;
    }
}

class NothingHandler extends BaseDrinkHandler {

    public NothingHandler() {
        super("Nothing Handler");
    }

    @Override
    public void handle(String regNumber) {
        System.out.println("Exiting Drinks Menu...");
        printSeparator();
    }

    @Override
    public DrinkCategory getCategory() {
        return DrinkCategory.NOTHING;
    }
}

// 4. ENHANCED HANDLER with bill total return
interface EnhancedDrinkHandler extends DrinkCategoryHandler {
    int handleWithTotal(String regNumber);
}

class EnhancedSoftDrinksHandler extends SoftDrinksHandler implements EnhancedDrinkHandler {

    @Override
    public int handleWithTotal(String regNumber) {
        System.out.println("\n→ Entering Soft Drinks section...");
        int total = MySoftDrinks.SoftDrinkbill(regNumber);
        if (total > 0) {
            System.out.println("Soft Drinks Total: " + total + " TK");
        }
        printSeparator();
        return total;
    }
}

class EnhancedCoffeeHandler extends CoffeeHandler implements EnhancedDrinkHandler {

    @Override
    public int handleWithTotal(String regNumber) {
        System.out.println("\n→ Entering Coffee section...");
        int total = MyCoffee.CoffeeBill(regNumber);
        if (total > 0) {
            System.out.println("Coffee Total: " + total + " TK");
        }
        printSeparator();
        return total;
    }
}

class EnhancedJuicePlantHandler extends JuicePlantHandler implements EnhancedDrinkHandler {

    @Override
    public int handleWithTotal(String regNumber) {
        System.out.println("\n→ Entering Juice or Plant Drinks section...");
        int total = MyJuiceOrPlantDrink.JuiceORPlantbill(regNumber);
        if (total > 0) {
            System.out.println("Juice/Plant Drinks Total: " + total + " TK");
        }
        printSeparator();
        return total;
    }
}

// 5. HANDLER FACTORY - Dependency Inversion
interface DrinkHandlerFactory {
    Optional<DrinkCategoryHandler> getHandler(int choice);
    Optional<EnhancedDrinkHandler> getEnhancedHandler(int choice);
    Map<Integer, DrinkCategoryHandler> getAllHandlers();
}

class DefaultDrinkHandlerFactory implements DrinkHandlerFactory {
    private final Map<Integer, DrinkCategoryHandler> handlers;
    private final Map<Integer, EnhancedDrinkHandler> enhancedHandlers;

    public DefaultDrinkHandlerFactory() {
        handlers = new HashMap<>();
        enhancedHandlers = new HashMap<>();

        // Register standard handlers
        registerHandler(new SoftDrinksHandler());
        registerHandler(new CoffeeHandler());
        registerHandler(new JuicePlantHandler());
        registerHandler(new NothingHandler());

        // Register enhanced handlers
        registerEnhancedHandler(new EnhancedSoftDrinksHandler());
        registerEnhancedHandler(new EnhancedCoffeeHandler());
        registerEnhancedHandler(new EnhancedJuicePlantHandler());
    }

    private void registerHandler(DrinkCategoryHandler handler) {
        handlers.put(handler.getCategory().getCode(), handler);
    }

    private void registerEnhancedHandler(EnhancedDrinkHandler handler) {
        enhancedHandlers.put(handler.getCategory().getCode(), handler);
        // Also add to standard handlers for backward compatibility
        handlers.put(handler.getCategory().getCode(), handler);
    }

    @Override
    public Optional<DrinkCategoryHandler> getHandler(int choice) {
        return Optional.ofNullable(handlers.get(choice));
    }

    @Override
    public Optional<EnhancedDrinkHandler> getEnhancedHandler(int choice) {
        return Optional.ofNullable(enhancedHandlers.get(choice));
    }

    @Override
    public Map<Integer, DrinkCategoryHandler> getAllHandlers() {
        return Collections.unmodifiableMap(handlers);
    }
}

// 6. OBSERVER PATTERN - For extensibility
interface DrinkSelectionObserver {
    void onDrinkCategorySelected(DrinkCategory category, String regNumber, int total);
    void onDrinkMenuExited();
}

class LoggingDrinkObserver implements DrinkSelectionObserver {
    private final List<String> selectionHistory = new ArrayList<>();
    private int grandTotal = 0;

    @Override
    public void onDrinkCategorySelected(DrinkCategory category, String regNumber, int total) {
        String logEntry = String.format("[%s] Student %s selected: %s - Total: %d TK",
                new Date(), regNumber, category.getDisplayName(), total);
        selectionHistory.add(logEntry);
        grandTotal += total;
        System.out.println("✓ " + logEntry);
    }

    @Override
    public void onDrinkMenuExited() {
        System.out.println("Drinks menu exited. Total selections: " + selectionHistory.size());
        System.out.println("Grand Total for Drinks: " + grandTotal + " TK");
    }

    public List<String> getSelectionHistory() {
        return Collections.unmodifiableList(selectionHistory);
    }

    public int getGrandTotal() {
        return grandTotal;
    }
}

// 7. MAIN SERVICE - Single Responsibility: Orchestrate drink selection
class DrinkSelectionService {
    private final DrinkMenuDisplayer menuDisplayer;
    private final DrinkInputReader inputReader;
    private final DrinkHandlerFactory handlerFactory;
    private final List<DrinkSelectionObserver> observers;
    private boolean useEnhancedMode;

    public DrinkSelectionService(DrinkMenuDisplayer menuDisplayer,
                                 DrinkInputReader inputReader,
                                 DrinkHandlerFactory handlerFactory) {
        this.menuDisplayer = Objects.requireNonNull(menuDisplayer);
        this.inputReader = Objects.requireNonNull(inputReader);
        this.handlerFactory = Objects.requireNonNull(handlerFactory);
        this.observers = new ArrayList<>();
        this.useEnhancedMode = false;
    }

    public void enableEnhancedMode() {
        this.useEnhancedMode = true;
    }

    public void addObserver(DrinkSelectionObserver observer) {
        observers.add(Objects.requireNonNull(observer));
    }

    public void removeObserver(DrinkSelectionObserver observer) {
        observers.remove(observer);
    }

    private void notifyCategorySelected(DrinkCategory category, String regNumber, int total) {
        for (DrinkSelectionObserver observer : observers) {
            observer.onDrinkCategorySelected(category, regNumber, total);
        }
    }

    private void notifyMenuExited() {
        for (DrinkSelectionObserver observer : observers) {
            observer.onDrinkMenuExited();
        }
    }

    public void selectDrink(String regNumber) {
        while (true) {
            menuDisplayer.display();

            int choice = inputReader.readInt();
            System.out.println();

            if (choice == 0) {
                System.out.println("Exiting Drinks Menu...");
                notifyMenuExited();
                return;
            }

            Optional<DrinkCategoryHandler> handlerOpt = handlerFactory.getHandler(choice);

            if (handlerOpt.isPresent()) {
                DrinkCategoryHandler handler = handlerOpt.get();

                if (useEnhancedMode && handler instanceof EnhancedDrinkHandler) {
                    EnhancedDrinkHandler enhancedHandler = (EnhancedDrinkHandler) handler;
                    int total = enhancedHandler.handleWithTotal(regNumber);
                    notifyCategorySelected(handler.getCategory(), regNumber, total);
                } else {
                    handler.handle(regNumber);
                }
            } else {
                System.out.println("Please choose a correct option!");
                System.out.println("********************************************");
            }
        }
    }

    public DrinkSelectionResult selectDrinkWithTotal(String regNumber) {
        Map<DrinkCategory, Integer> categoryTotals = new LinkedHashMap<>();
        int grandTotal = 0;

        while (true) {
            menuDisplayer.display();

            int choice = inputReader.readInt();
            System.out.println();

            if (choice == 0) {
                System.out.println("Exiting Drinks Menu...");
                notifyMenuExited();
                return new DrinkSelectionResult(categoryTotals, grandTotal);
            }

            Optional<EnhancedDrinkHandler> handlerOpt = handlerFactory.getEnhancedHandler(choice);

            if (handlerOpt.isPresent()) {
                EnhancedDrinkHandler handler = handlerOpt.get();
                int total = handler.handleWithTotal(regNumber);

                categoryTotals.put(handler.getCategory(), total);
                grandTotal += total;

                notifyCategorySelected(handler.getCategory(), regNumber, total);
            } else {
                System.out.println("Please choose a correct option!");
                System.out.println("********************************************");
            }
        }
    }
}

// 8. VALUE OBJECT for results
class DrinkSelectionResult {
    private final Map<DrinkCategory, Integer> categoryTotals;
    private final int grandTotal;

    public DrinkSelectionResult(Map<DrinkCategory, Integer> categoryTotals, int grandTotal) {
        this.categoryTotals = new LinkedHashMap<>(categoryTotals);
        this.grandTotal = grandTotal;
    }

    public Map<DrinkCategory, Integer> getCategoryTotals() {
        return Collections.unmodifiableMap(categoryTotals);
    }

    public int getGrandTotal() {
        return grandTotal;
    }

    public boolean hasOrders() {
        return grandTotal > 0;
    }

    public void printSummary() {
        if (!hasOrders()) {
            System.out.println("No drinks ordered.");
            return;
        }

        System.out.println("\n===== DRINKS BILL SUMMARY =====");
        categoryTotals.forEach((category, total) ->
                System.out.printf("%-25s: %d TK%n", category.getDisplayName(), total));
        System.out.println("===============================");
        System.out.printf("GRAND TOTAL: %d TK%n", grandTotal);
    }
}

// 9. BUILDER PATTERN - For flexible service construction
class DrinkSelectionServiceBuilder {
    private DrinkMenuDisplayer menuDisplayer;
    private DrinkInputReader inputReader;
    private DrinkHandlerFactory handlerFactory;
    private List<DrinkSelectionObserver> observers = new ArrayList<>();
    private boolean enhancedMode = false;

    public DrinkSelectionServiceBuilder withDefaultMenuDisplayer() {
        this.menuDisplayer = new DrinkCategoryConsoleMenuDisplayer();
        return this;
    }

    public DrinkSelectionServiceBuilder withDefaultInputReader() {
        this.inputReader = new DrinkConsoleInputReader();
        return this;
    }

    public DrinkSelectionServiceBuilder withDefaultHandlerFactory() {
        this.handlerFactory = new DefaultDrinkHandlerFactory();
        return this;
    }

    public DrinkSelectionServiceBuilder withEnhancedMode() {
        this.enhancedMode = true;
        return this;
    }

    public DrinkSelectionServiceBuilder withCustomMenuDisplayer(DrinkMenuDisplayer displayer) {
        this.menuDisplayer = displayer;
        return this;
    }

    public DrinkSelectionServiceBuilder withCustomInputReader(DrinkInputReader reader) {
        this.inputReader = reader;
        return this;
    }

    public DrinkSelectionServiceBuilder withCustomHandlerFactory(DrinkHandlerFactory factory) {
        this.handlerFactory = factory;
        return this;
    }

    public DrinkSelectionServiceBuilder withObserver(DrinkSelectionObserver observer) {
        this.observers.add(observer);
        return this;
    }

    public DrinkSelectionService build() {
        if (menuDisplayer == null) {
            menuDisplayer = new DrinkCategoryConsoleMenuDisplayer();
        }
        if (inputReader == null) {
            inputReader = new DrinkConsoleInputReader();
        }
        if (handlerFactory == null) {
            handlerFactory = new DefaultDrinkHandlerFactory();
        }

        DrinkSelectionService service = new DrinkSelectionService(
                menuDisplayer, inputReader, handlerFactory);

        if (enhancedMode) {
            service.enableEnhancedMode();
        }

        for (DrinkSelectionObserver observer : observers) {
            service.addObserver(observer);
        }

        return service;
    }
}

// 10. FACTORY for creating services
class DrinkSelectionServiceFactory {

    private static DrinkSelectionService defaultService;
    private static DrinkSelectionService enhancedService;

    public static DrinkSelectionService createDefaultService() {
        if (defaultService == null) {
            defaultService = new DrinkSelectionServiceBuilder()
                    .withDefaultMenuDisplayer()
                    .withDefaultInputReader()
                    .withDefaultHandlerFactory()
                    .withObserver(new LoggingDrinkObserver())
                    .build();
        }
        return defaultService;
    }

    public static DrinkSelectionService createEnhancedService() {
        if (enhancedService == null) {
            enhancedService = new DrinkSelectionServiceBuilder()
                    .withDefaultMenuDisplayer()
                    .withDefaultInputReader()
                    .withDefaultHandlerFactory()
                    .withEnhancedMode()
                    .withObserver(new LoggingDrinkObserver())
                    .build();
        }
        return enhancedService;
    }

    public static DrinkSelectionService createFreshService() {
        return new DrinkSelectionServiceBuilder()
                .withDefaultMenuDisplayer()
                .withDefaultInputReader()
                .withDefaultHandlerFactory()
                .withObserver(new LoggingDrinkObserver())
                .build();
    }

    public static DrinkSelectionService createTestService() {
        return new DrinkSelectionServiceBuilder()
                .withDefaultMenuDisplayer()
                .withDefaultInputReader()
                .withDefaultHandlerFactory()
                .build();
    }
}

// 11. MAIN PUBLIC CLASS - Backward compatibility
public class DrinkSelection {

    // Static field for backward compatibility
    static Scanner input = new Scanner(System.in);

    /**
     * Original method with array parameter - maintained for backward compatibility
     * Now delegates to the proper implementation
     */
    public static void myDrink(String[] regNumber) {
        String reg = (regNumber != null && regNumber.length > 0) ? regNumber[0] : "";

        if (reg.isEmpty()) {
            System.out.print("Enter registration number: ");
            reg = input.nextLine().trim();
        }

        myDrink(reg);
    }

    /**
     * Proper implementation - accepts String regNumber
     * This is the method that should be used
     */
    public static void myDrink(String regNumber) {
        DrinkSelectionService service = DrinkSelectionServiceFactory.createDefaultService();
        service.selectDrink(regNumber);
    }

    /**
     * Enhanced version that returns total bill amount
     */
    public static int myDrinkWithTotal(String regNumber) {
        DrinkSelectionService service = DrinkSelectionServiceFactory.createEnhancedService();
        DrinkSelectionResult result = service.selectDrinkWithTotal(regNumber);
        result.printSummary();
        return result.getGrandTotal();
    }

    /**
     * Overloaded method with custom service (for dependency injection)
     */
    public static void myDrink(String regNumber, DrinkSelectionService customService) {
        Objects.requireNonNull(customService).selectDrink(regNumber);
    }

    /**
     * Utility method to get available drink categories
     */
    public static List<DrinkCategory> getAvailableCategories() {
        return DrinkCategory.getActiveCategories();
    }

    /**
     * Utility method to check if a choice is valid
     */
    public static boolean isValidOption(int choice) {
        return DrinkCategory.fromCode(choice).isPresent();
    }
}

