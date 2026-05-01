# University-Management-
SOLID Principles Overview
Principle	Description
S - Single Responsibility	A class should have only one reason to change
O - Open/Closed	Classes should be open for extension, closed for modification
L - Liskov Substitution	Subtypes must be substitutable for their base types
I - Interface Segregation	Clients should not depend on interfaces they don't use
D - Dependency Inversion	Depend on abstractions, not concrete implementations
File-by-File Analysis
1. Bus.java
Principle	Applied?	Explanation
Single Responsibility	✅	Class only manages bus data (registrationNo, busId, route, etc.)
Open/Closed	✅	Can be extended with subclasses for different bus types
Dependency Inversion	N/A	No external dependencies
java
// Clean data holder with focused responsibility
public class Bus {
    private String registrationNo;
    private String busId;
    // ... only bus-related properties
}
2. CourseRegistration(Solid).java
Principle	Applied?	Explanation
Single Responsibility	✅	Split into RegistrationFileManager, RegistrationDisplay, RegistrationValidator
Open/Closed	✅	New validation rules can be added without modifying existing code
java
// Each class has ONE job
class RegistrationFileManager { }  // File operations only
class RegistrationDisplay { }       // Display only
class RegistrationValidator { }     // Validation only
3. Courses(Solid).java
Principle	Applied?	Explanation
Single Responsibility	✅	CourseDisplay, CourseFileManager, SemesterCalculator, CourseRegistrationValidator
Dependency Inversion	✅	Classes depend on abstractions (interfaces)
Open/Closed	✅	New semester calculation logic can be added via extension
java
class SemesterCalculator {
    // Single responsibility: calculate semester from registration number
    public int getSemesterFromRegNumber(String regNumber) { }
}
4. TimeService.java
Principle	Applied?	Explanation
Single Responsibility	✅	Only manages pickup/drop time data
Open/Closed	✅	New time slots can be added without modifying existing code
Liskov Substitution	✅	Can be extended with different time formats
java
public class TimeService {
    private final List<String> pickupTimes = Arrays.asList("8 AM", "10 AM", "12 PM");
    // Only time-related operations
}
5. TMS.java (Transport Management System)
Principle	Applied?	Explanation
Single Responsibility	✅	Main orchestrator, delegates to specialized services
Dependency Inversion	✅	Depends on RouteService, FileService, PaymentService, BusService abstractions
Open/Closed	✅	New menu options can be added via switch extensions
java
public class TMS {
    private RouteService routeService = new RouteService();      // Delegated
    private FileService fileService = new FileService();         // Delegated
    private PaymentService paymentService = new PaymentService(); // Delegated
    private BusService busService = new BusService(routeService); // Composition
}
6. RouteService.java
Principle	Applied?	Explanation
Single Responsibility	✅	Only manages route and fee data
Open/Closed	✅	New routes/fees can be added to maps without code changes
java
public class RouteService {
    private final Map<String, List<String>> routes = new HashMap<>();
    private final Map<String, String> fees = new HashMap<>();
    // Only route-related operations
}
7. BusService.java
Principle	Applied?	Explanation
Single Responsibility	✅	Only handles bus registration and update logic
Dependency Injection	✅	RouteService and TimeService are injected
Open/Closed	✅	New registration rules can be added without modification
java
public class BusService {
    private RouteService routeService;   // Injected dependency
    private TimeService timeService;      // Injected dependency
    
    public BusService(RouteService routeService) {
        this.routeService = routeService;
    }
}
8. DrinkSelection.java ⭐ Best Example
Principle	Applied?	Explanation
Single Responsibility	✅	10+ focused classes (enum, handlers, factory, service, builder, observer)
Open/Closed	✅	New drink types can be added by extending BaseDrinkHandler
Liskov Substitution	✅	All handlers extend BaseDrinkHandler
Interface Segregation	✅	DrinkMenuDisplayer, DrinkInputReader, DrinkCategoryHandler are minimal interfaces
Dependency Inversion	✅	DrinkSelectionService depends on abstractions, not concrete classes
java
// Interface Segregation - Small, focused interfaces
interface DrinkMenuDisplayer { void display(); }
interface DrinkInputReader { int readInt(); void clearBuffer(); }
interface DrinkCategoryHandler { void handle(String regNumber); }

// Dependency Inversion - Depends on abstractions
class DrinkSelectionService {
    private final DrinkMenuDisplayer menuDisplayer;  // Abstraction
    private final DrinkInputReader inputReader;       // Abstraction
    private final DrinkHandlerFactory handlerFactory; // Abstraction
}

// Open/Closed - New drink type without modifying existing code
class NewDrinkHandler extends BaseDrinkHandler { }
9. FoodSelection.java
Principle	Applied?	Explanation
Single Responsibility	✅	Only handles food category selection and delegation
Open/Closed	✅	New food categories can be added via new case statements
Dependency Inversion	✅	Delegates to MyFastFood and MyDesiFood abstractions
java
private static void handleFastFood(String regNumber) {
    MyFastFood.FastfoodBill();  // Delegated to specialized class
}
10. MyCoffee.java ⭐ Best Example
Principle	Applied?	Explanation
Single Responsibility	✅	CoffeeItem (enum), CoffeeOrder, CoffeeMenuDisplayer, CoffeeInputReader, CoffeeOrderProcessor, CoffeeBillWriter
Open/Closed	✅	New coffee items can be added to enum without modifying business logic
Liskov Substitution	✅	CoffeePersonalBillWriter and CoffeeSharedBillWriter implement CoffeeBillWriter
Interface Segregation	✅	CoffeeMenuDisplayer, CoffeeInputReader, CoffeeBillWriter are segregated
Dependency Inversion	✅	CoffeeBillGenerationService depends on interfaces
java
// Interface Segregation
interface CoffeeMenuDisplayer { void displayMenu(); }
interface CoffeeInputReader { int readInt(); int readQuantity(); }
interface CoffeeBillWriter { void writeHeader(); void writeOrder(); }

// Dependency Inversion
class CoffeeBillGenerationService {
    private final CoffeeMenuDisplayer menuDisplayer;
    private final CoffeeInputReader inputReader;
    private final CoffeeBillWriter personalWriter;
    private final CoffeeBillWriter sharedWriter;
}
11. MyDesiFood.java
Principle	Applied?	Explanation
Single Responsibility	✅	Enum for dishes, OrderedDish inner class, focused helper methods
Open/Closed	✅	New dishes can be added to DesiDish enum without changing logic
Liskov Substitution	N/A	No inheritance hierarchy
java
enum DesiDish {
    HALEEM("Haleem", 120),
    BIRYANI("Biryani", 200);
    // Easy to add more dishes
}
12. MyFastFood.java
Principle	Applied?	Explanation
Single Responsibility	✅	Separate concerns: enum, OrderedItem, menu display, file I/O
Open/Closed	✅	New items to FastFoodItem enum without modifying core logic
Dependency Inversion	N/A	Self-contained module
java
enum FastFoodItem {
    BURGER("Burger", 80),
    ZINGER_BURGER("Zinger Burger", 250);
    // Extensible
}
13. MyFinalBill.java ⭐ Best Example
Principle	Applied?	Explanation
Single Responsibility	✅	StudentDataService, BillLineParser, BillReader, BalanceValidator, BillPrinter, BillSaver, TempFileCleaner (7+ focused classes)
Open/Closed	✅	New bill formats via new BillPrinter implementations
Liskov Substitution	✅	FileStudentDataService implements StudentDataService
Interface Segregation	✅	StudentDataService, BillReader, BillPrinter, BillSaver are minimal
Dependency Inversion	✅	FinalBillService depends on abstractions, uses Builder pattern
java
// Multiple small interfaces (Interface Segregation)
interface StudentDataService { StudentInfo getStudentInfo(String regNumber); }
interface BillReader { BillReadingResult readAllBills(); }
interface BillPrinter { void printBill(StudentInfo student, BillReadingResult data, int remaining); }
interface BillSaver { void saveBill(StudentInfo student, BillReadingResult data); }
interface TempFileCleaner { void clearTempFiles(); }

// Dependency Inversion + Builder Pattern
class FinalBillService {
    private final StudentDataService studentService;
    private final BillReader billReader;
    private final BalanceValidator balanceValidator;
    private final BillPrinter billPrinter;
    private final BillSaver billSaver;
    private final TempFileCleaner tempFileCleaner;
}

// Builder for flexible construction
class FinalBillServiceBuilder {
    public FinalBillServiceBuilder withDefaultServices() { }
    public FinalBillService build() { }
}
14. MyJuiceOrPlantDrink.java
Principle	Applied?	Explanation
Single Responsibility	✅	JuicePlantItem (enum), JuicePlantOrder, JuicePlantMenuService, JuicePlantInputReader, JuicePlantBillWriter, JuicePlantOrderProcessor
Open/Closed	✅	New drink items to enum without modifying logic
Interface Segregation	✅	Separated menu, input, bill writing interfaces
Dependency Inversion	✅	Service depends on interfaces, Factory pattern for creation
java
interface JuicePlantMenuService { void displayMenu() throws Exception; }
interface JuicePlantInputReader { int readInt(); int readQuantity(); }
interface JuicePlantBillWriter { void writeHeader(); void writeOrder(); }
15. MySoftDrinks.java
Principle	Applied?	Explanation
Single Responsibility	✅	SoftDrink enum, OrderedDrink inner class, focused helper methods
Open/Closed	✅	New drinks to enum without modifying billing logic
Liskov Substitution	N/A	Self-contained module
java
enum SoftDrink {
    COCA_COLA("Coca-Cola", 60),
    PEPSI("Pepsi", 60);
    // Extensible for new drinks
}
16. SelectionMethod.java
Principle	Applied?	Explanation
Single Responsibility	✅	BillProvider interface and implementations separate concerns
Open/Closed	✅	New bill providers can be added without modifying existing code
Interface Segregation	✅	BillProvider interface has single method getBillAmount()
Dependency Inversion	✅	Depends on BillProvider abstraction, not concrete classes
Liskov Substitution	✅	All *BillProvider classes implement BillProvider
java
// Interface Segregation - Single method interface
interface BillProvider {
    int getBillAmount();
}

// Open/Closed - Add new provider without modifying existing code
class NewCategoryBillProvider implements BillProvider {
    @Override public int getBillAmount() { return new NewCategory().calculate(); }
}

// Dependency Inversion - Depends on abstraction
private static void generateAndShowBill(List<BillProvider> providers) {
    for (BillProvider provider : providers) {
        total += provider.getBillAmount();
    }
}
