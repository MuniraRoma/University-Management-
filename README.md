SOLID Principles Implementation Guide
Overview
This project demonstrates the SOLID Principles of Object-Oriented Design across multiple modules including Transport Management, Course Registration, Cafe Billing System, and Food/Drink Selection. Each principle is strategically applied to create maintainable, extensible, and testable code.

The Five SOLID Principles
Principle	Description
Single Responsibility	A class should have only one reason to change
Open/Closed	Classes should be open for extension, closed for modification
Liskov Substitution	Subtypes must be substitutable for their base types
Interface Segregation	Clients should not depend on interfaces they don't use
Dependency Inversion	Depend on abstractions, not concrete implementations
File-by-File Analysis
Bus.java
Single Responsibility - The class only manages bus data (registration number, bus ID, route, stop, fee, pickup/drop times)

Open/Closed - Can be extended with subclasses for different bus types like SchoolBus or TouristBus

CourseRegistration(Solid).java
Single Responsibility - Responsibilities are split into three dedicated classes: FileManager (handles file operations), Display (handles console output), and Validator (handles input validation)

Open/Closed - New validation rules can be added by extending the Validator class without modifying existing registration logic

Courses(Solid).java
Single Responsibility - Four focused classes: CourseDisplay (menu presentation), CourseFileManager (file I/O), SemesterCalculator (semester logic), and CourseRegistrationValidator (validation rules)

Dependency Inversion - High-level modules depend on abstractions rather than concrete implementations

Open/Closed - New semester calculation logic can be added by modifying the SemesterCalculator independently

TimeService.java
Single Responsibility - Exclusively manages pickup and drop time data

Open/Closed - New time slots can be added to the internal lists without modifying any other code

TMS.java (Transport Management System)
Single Responsibility - Acts as an orchestrator, delegating all specific operations to specialized services

Dependency Inversion - Depends on abstractions like RouteService, FileService, PaymentService, and BusService

Open/Closed - New menu options can be added by extending the switch statement without breaking existing functionality

RouteService.java
Single Responsibility - Only manages route and fee data stored in HashMaps

Open/Closed - New routes and fees can be added to the internal maps without code changes

BusService.java
Single Responsibility - Handles only bus registration and update operations

Dependency Injection - RouteService and TimeService are injected via constructor, not created internally

Open/Closed - New registration rules can be added without modifying the core registration method

DrinkSelection.java (Best Example - All 5 Principles)
Single Responsibility - Over 10 focused classes including enums, handlers, factory, service, builder, and observer, each with one job

Open/Closed - New drink types can be added by creating a new handler class that extends BaseDrinkHandler, no need to modify existing handlers

Liskov Substitution - All drink handlers extend BaseDrinkHandler and can be used interchangeably

Interface Segregation - Small, focused interfaces like DrinkMenuDisplayer (only displays), DrinkInputReader (only reads input), and DrinkCategoryHandler (only handles categories)

Dependency Inversion - DrinkSelectionService depends on abstractions (interfaces) not concrete classes, and uses Factory pattern for object creation

FoodSelection.java
Single Responsibility - Only handles food category selection and delegates to specialized food handlers

Open/Closed - New food categories can be added by adding new case statements without modifying existing category handlers

Dependency Inversion - Delegates to MyFastFood and MyDesiFood abstractions rather than implementing food logic directly

MyCoffee.java (Best Example - All 5 Principles)
Single Responsibility - Six separate components: CoffeeItem enum (defines items), CoffeeOrder (represents orders), CoffeeMenuDisplayer (displays menu), CoffeeInputReader (handles input), CoffeeOrderProcessor (processes orders), and CoffeeBillWriter (writes bills)

Open/Closed - New coffee items can be added to the CoffeeItem enum without modifying any business logic

Liskov Substitution - Both CoffeePersonalBillWriter and CoffeeSharedBillWriter implement the CoffeeBillWriter interface and can be swapped

Interface Segregation - Separate interfaces for menu display, input reading, and bill writing - each with minimal methods

Dependency Inversion - CoffeeBillGenerationService depends on interfaces, not concrete classes; Factory pattern creates the complete service

MyDesiFood.java
Single Responsibility - DesiDish enum defines dishes, OrderedDish inner class tracks orders, helper methods each have one clear purpose

Open/Closed - New desi dishes can be added to the DesiDish enum without modifying billing calculation logic

Liskov Substitution - The enum provides consistent behavior across all dish types

MyFastFood.java
Single Responsibility - FastFoodItem enum defines items, OrderedItem tracks orders, separate methods for menu display and file operations

Open/Closed - New fast food items can be added to the FastFoodItem enum without changing the billing logic

MyFinalBill.java (Best Example - All 5 Principles)
Single Responsibility - Seven+ focused classes: StudentDataService (gets student info), BillLineParser (parses bill lines), BillReader (reads bills), BalanceValidator (validates balance), BillPrinter (prints bills), BillSaver (saves bills), TempFileCleaner (cleans temp files)

Open/Closed - New bill formats can be added by creating new BillPrinter implementations without modifying existing code

Liskov Substitution - FileStudentDataService implements StudentDataService interface and can be replaced with any other implementation

Interface Segregation - Minimal interfaces: StudentDataService (one method), BillReader (one method), BillPrinter (one method), BillSaver (one method)

Dependency Inversion - FinalBillService depends entirely on abstractions (all interfaces); Builder pattern enables flexible service construction; Factory pattern provides default implementations

MyJuiceOrPlantDrink.java
Single Responsibility - JuicePlantItem enum defines drinks, JuicePlantOrder represents orders, separate services for menu display, input reading, and bill writing

Open/Closed - New juice items can be added to the JuicePlantItem enum without modifying order processing logic

Interface Segregation - Three separate interfaces: JuicePlantMenuService (menu operations), JuicePlantInputReader (input operations), JuicePlantBillWriter (bill operations)

Dependency Inversion - JuicePlantBillGenerationService depends on interfaces; Factory pattern handles service creation

MySoftDrinks.java
Single Responsibility - SoftDrink enum defines drinks, OrderedDrink inner class tracks orders, helper methods each have one clear responsibility

Open/Closed - New soft drinks can be added to the SoftDrink enum without modifying the billing calculation

Liskov Substitution - Consistent behavior across all soft drink types through enum implementation

SelectionMethod.java
Single Responsibility - BillProvider interface and its implementations separate concerns; each provider knows only how to calculate its own bill

Open/Closed - New bill providers can be added by creating new classes implementing BillProvider without modifying existing providers or the main menu loop

Interface Segregation - BillProvider interface has a single method (getBillAmount) - the interface segregation principle perfectly applied

Dependency Inversion - The main generateAndShowBill method depends on the BillProvider abstraction, not on concrete food or drink classes

Liskov Substitution - All provider classes (FastFoodBillProvider, DesiFoodBillProvider, etc.) implement BillProvider and can be used interchangeably

Why We Use SOLID Principles
Maintainability
Changes affect only specific classes, not the entire codebase. When a bug is found in bill calculation, only the bill-related classes need to be examined.

Testability
Each small class can be tested independently. The BalanceValidator can be unit tested without needing file I/O or user input.

Readability
Code is organized with clear separation of concerns. A developer can understand what a class does just by reading its name and brief description.

Reusability
Classes can be reused in different contexts. The TimeService could be used by both bus scheduling and cafe ordering systems.

Extensibility
New features can be added without modifying existing code. Adding a new drink type only requires creating a new handler class, not changing existing handlers.

Parallel Development
Multiple developers can work on different classes simultaneously without merge conflicts. One developer can work on coffee billing while another works on final bill generation.

Reduced Bugs
Localized changes reduce unexpected side effects. Changing the coffee menu display doesn't affect drink selection logic.
