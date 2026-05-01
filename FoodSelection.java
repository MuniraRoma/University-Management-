import java.util.*;

// ===========================
// STRATEGY PATTERN IMPLEMENTATION
// ===========================

// Context class that uses the strategy
class FoodOrderContext {
    private FoodBillingStrategy strategy;
    private int runningTotal = 0;

    public void setStrategy(FoodBillingStrategy strategy) {
        this.strategy = strategy;
    }

    public OrderResult executeStrategy(String regNumber) {
        OrderResult result = strategy.calculateBill(regNumber);
        runningTotal += result.getTotal();
        result.setRunningTotal(runningTotal);
        return result;
    }

    public int getRunningTotal() {
        return runningTotal;
    }
}

// Strategy Interface
interface FoodBillingStrategy {
    OrderResult calculateBill(String regNumber);
}

// Concrete Strategy 1: Fast Food
class FastFoodBillingStrategy implements FoodBillingStrategy {
    @Override
    public OrderResult calculateBill(String regNumber) {
        MyFastFood fastFood = new MyFastFood();
        int total = fastFood.FastfoodBill();

        List<String> billLines = new ArrayList<>();
        billLines.add("Fast Food total: " + total + " TK");

        System.out.println("Fast Food Total: " + total + " TK");
        System.out.println("********************************************");

        return new OrderResult(total, billLines, "Fast Food");
    }
}

// Concrete Strategy 2: Desi Food
class DesiFoodBillingStrategy implements FoodBillingStrategy {
    @Override
    public OrderResult calculateBill(String regNumber) {
        MyDesiFood desiFood = new MyDesiFood();
        int total = desiFood.DesifoodBill();

        List<String> billLines = new ArrayList<>();
        billLines.add("Desi Food total: " + total + " TK");

        System.out.println("Desi Food Total: " + total + " TK");
        System.out.println("********************************************");

        return new OrderResult(total, billLines, "Desi Food");
    }
}

// Result holder class
class OrderResult {
    private final int total;
    private final List<String> billLines;
    private final String category;
    private int runningTotal;

    public OrderResult(int total, List<String> billLines, String category) {
        this.total = total;
        this.billLines = billLines;
        this.category = category;
        this.runningTotal = 0;
    }

    public int getTotal() { return total; }
    public List<String> getBillLines() { return billLines; }
    public String getCategory() { return category; }
    public int getRunningTotal() { return runningTotal; }
    public void setRunningTotal(int runningTotal) { this.runningTotal = runningTotal; }
}

// Strategy Factory
class StrategyFactory {
    private static final Map<Integer, FoodBillingStrategy> strategies = new HashMap<>();

    static {
        strategies.put(1, new FastFoodBillingStrategy());
        strategies.put(2, new DesiFoodBillingStrategy());
    }

    public static FoodBillingStrategy getStrategy(int choice) {
        return strategies.get(choice);
    }

    public static boolean isValidChoice(int choice) {
        return strategies.containsKey(choice);
    }
}

// Observer Pattern for Logging
interface OrderObserver {
    void onOrderPlaced(OrderResult result);
}

class LoggingObserver implements OrderObserver {
    @Override
    public void onOrderPlaced(OrderResult result) {
        System.out.println("[LOG] " + result.getCategory() + " order: " +
                result.getTotal() + " TK (Running Total: " +
                result.getRunningTotal() + " TK)");
    }
}

// Main Food Selection Manager
class FoodSelectionManager {
    private final Scanner scanner;
    private final FoodOrderContext context;
    private final List<OrderObserver> observers;
    private int grandTotal;
    private List<String> allBillLines;

    public FoodSelectionManager(Scanner scanner) {
        this.scanner = scanner;
        this.context = new FoodOrderContext();
        this.observers = new ArrayList<>();
        this.grandTotal = 0;
        this.allBillLines = new ArrayList<>();

        // Add default observer
        addObserver(new LoggingObserver());
    }

    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    private void notifyObservers(OrderResult result) {
        for (OrderObserver observer : observers) {
            observer.onOrderPlaced(result);
        }
    }

    private void displayMenu() {
        System.out.println("\n===== FOOD CATEGORIES =====");
        System.out.println("1. Fast Food");
        System.out.println("2. Desi Food");
        System.out.println("0. Go Back / Exit");
        System.out.println("==========================");
    }

    private int getUserChoice() {
        while (true) {
            System.out.print("Enter Your Choice: ");
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 0 && choice <= 2) {
                    return choice;
                }
                System.out.println("Invalid option, try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input, enter a number.");
            }
        }
    }

    public int selectFood(String regNumber) {
        grandTotal = 0;
        allBillLines.clear();

        while (true) {
            displayMenu();
            int choice = getUserChoice();

            if (choice == 0) {
                break;
            }

            if (!StrategyFactory.isValidChoice(choice)) {
                System.out.println("Invalid option, try again.");
                continue;
            }

            // Get the strategy and execute
            FoodBillingStrategy strategy = StrategyFactory.getStrategy(choice);
            context.setStrategy(strategy);
            OrderResult result = context.executeStrategy(regNumber);

            // Update totals
            grandTotal += result.getTotal();
            allBillLines.addAll(result.getBillLines());

            // Notify observers
            notifyObservers(result);
        }

        displaySummary();
        return grandTotal;
    }

    private void displaySummary() {
        if (grandTotal > 0) {
            System.out.println("\n=== Food Section Summary ===");
            System.out.println("Total Food Bill: " + grandTotal + " TK");
            System.out.println("==============================");
        }
    }
}

// Singleton Pattern for the main FoodSelection class
public class FoodSelection {
    private static FoodSelection instance;
    private static final Scanner scanner = new Scanner(System.in);
    private static FoodSelectionManager manager;

    private FoodSelection() {
        manager = new FoodSelectionManager(scanner);
    }

    public static synchronized FoodSelection getInstance() {
        if (instance == null) {
            instance = new FoodSelection();
        }
        return instance;
    }

    public static int food(String regNumber) {
        return getInstance().selectFood(regNumber);
    }

    private int selectFood(String regNumber) {
        return manager.selectFood(regNumber);
    }
}




