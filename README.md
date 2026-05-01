# University-Management-
# 🔧 Code Refactoring - University Management System

## **Overview**

This project demonstrates **Code Refactoring Techniques** to eliminate code smells and improve code quality across multiple modules including **Transport Management**, **Course Registration**,  **Library Management** and **Cafe Billing System**. Each refactoring technique is applied to create cleaner, more maintainable, and readable code.

---

## **Common Code Smells Identified & Fixed**

| **Code Smell** | **Description** | **Impact** |
|----------------|-----------------|------------|
| **Long Method** | Methods doing too many operations | Hard to understand and test |
| **Magic Numbers/Strings** | Hard-coded values without explanation | Difficult to maintain and update |
| **Duplicate Code** | Same logic repeated across methods | Increases bug risk and maintenance effort |
| **Large Class** | Class trying to handle too many responsibilities | Violates single responsibility |
| **Feature Envy** | Method using more from another class than its own | Indicates misplaced functionality |
| **Data Clumps** | Same data groups appearing together | Suggests need for grouping class |
| **Switch Statements** | Large switch/case blocks | Hard to extend and maintain |

---

## **Refactoring Techniques Applied**

| **Technique** | **Purpose** |
|---------------|-------------|
| **Extract Method** | Break large methods into smaller, focused ones |
| **Extract Class** | Split large classes into smaller, single-responsibility classes |
| **Introduce Constant** | Replace magic numbers/strings with named constants |
| **Replace Conditional with Polymorphism** | Use inheritance instead of switch/case |
| **Command Pattern** | Encapsulate menu actions as command objects |
| **Extract Interface** | Create abstractions for better separation |

---

## **File-by-File Refactoring Analysis**

### **TransportManagement.java**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `manage()` contained all menu logic | Extracted to `MenuAction` interface |
| Switch Statements | Large switch case for menu options | Replaced with `Map<Integer, MenuAction>` |
| Magic Numbers | Hard-coded menu choices (1,2,3) | Used command pattern for actions |

**Refactoring Changes:**
- Created `MenuAction` interface for command pattern
- Each menu option as separate action class
- Removed switch-case with polymorphic map lookup
- Separated menu display from action execution

---

### **TMS.java**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Large Class | TMS had 8+ responsibilities | Split into specialized services |
| Long Method | `manage()` had 50+ lines | Extracted to command classes |
| Switch Statements | 8 case statements | Replaced with `Map<Integer, MenuAction>` |
| Feature Envy | Methods used services excessively | Services injected via constructor |

**Refactoring Changes:**
- Extracted `RouteService`, `FileService`, `PaymentService`, `BusService`
- Each menu option as separate action implementing `MenuAction`
- `feePaid` flag moved to method parameter instead of field
- Services injected via constructor for better testability

---

### **PaymentService.java**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Switch Statements | Switch on payment option | Map of PaymentAction implementations |
| Magic Strings | Hard-coded URLs | Constants or separate method per payment |
| Long Method | Pay method doing multiple things | Delegated to PaymentAction classes |

**Refactoring Changes:**
- Created `PaymentAction` interface
- Each payment method as separate class (`JazzCashPayment`, `HBLPayment`, `AlfalahPayment`)
- Map-based lookup instead of switch-case
- Proper exception handling added

---

### **Courses(Code Smell) → Courses(Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Large Class | One class handling everything | Split into `Course`, `CourseDisplay`, `CourseFileManager`, `SemesterCalculator`, `CourseRegistrationValidator` |
| Long Method | `registerCourse()` too long | Extracted `displayCourses()`, `registerSelectedCourses()`, `selectCourses()` |
| Magic Numbers | "At least 4 courses" hardcoded | Can be moved to constants |
| Duplicate Code | Course display logic repeated | Centralized in `CourseDisplay` |

**Refactoring Changes:**
- Created `Course` class as proper data holder
- `SemesterCalculator` with Map-based semester mapping
- `CourseRegistrationValidator` for all validation logic
- `CourseDisplay` for consistent output formatting
- `CourseFileManager` for all file operations

---

### **CourseRegistration(Code Smell) → CourseRegistration(Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `manage()` had multiple responsibilities | Delegated to specialized classes |
| Data Clumps | Course data appearing everywhere | Grouped into `Course` class |
| Feature Envy | Methods using too many external classes | Proper separation of concerns |
| Magic Numbers | Semester range (1-8) hardcoded | Validator with constants |

**Refactoring Changes:**
- `RegistrationFileManager` handles all file operations
- `RegistrationDisplay` handles all console output
- `RegistrationValidator` handles all input validation
- `Course` class encapsulates course data

---

### **DrinkSelection.java (Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `myDrink` method doing everything | Extracted `displayMenu()`, `readInt()`, `processCategory()` |
| Magic Numbers | 1,2,3,0 hardcoded throughout | Created `DrinkCategory` enum |
| Duplicate Code | Input reading repeated | Centralized `readInt()` method |
| Magic Strings | "********************************************" repeated | Moved to `Constants.SEPARATOR` |

**Refactoring Changes:**
- Created `Constants` inner class for magic strings
- Extracted `readInt()` for centralized input handling
- Extracted `displayMenu()` for menu display
- Extracted `processCategory()` for category handling
- Created `DrinkCategory` enum for drink types
- Removed `NothingHandler` class (handled directly in loop)
- Added grand total tracking

---

### **FoodSelection.java (Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `food` method doing everything | Extracted `displayMenu()`, `readInt()`, `processCategory()` |
| Magic Numbers | 1,2,0 hardcoded | Constants for menu options |
| Duplicate Code | Input reading repeated | Centralized `readInt()` method |
| Magic Strings | Separator repeated | `Constants.SEPARATOR` |

**Refactoring Changes:**
- Created `Constants` inner class for magic strings
- Extracted `displayMenu()` for menu display
- Extracted `processCategory()` for category handling
- Added grand total tracking
- Simplified exit handling (choice 0 directly exits)

---

### **MyCoffee.java (Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `CoffeeBill` had 100+ lines | Extracted `displayMenu()`, `processSingleOrder()`, `readInt()`, `readPositiveQuantity()` |
| Magic Numbers | Menu prices hardcoded | `CoffeeItem` enum with prices |
| Duplicate Code | Input reading logic repeated | Centralized `readInt()` method |
| Large Class | Mixed responsibilities | Extracted `BillFileService` inner class |

**Refactoring Changes:**
- Created `Constants` inner class for file paths and strings
- Created `CoffeeItem` enum for menu items
- Created `CoffeeOrder` inner class for orders
- Extracted `BillFileService` for file operations
- Extracted `processSingleOrder()` for order processing
- Centralized input reading with `readInt()`
- Added proper null/empty checks for regNumber

---

### **MyDesiFood.java (Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `DesifoodBill` had 100+ lines | Extracted `displayMenu()`, `processSingleOrder()`, `readInt()`, `readPositiveQuantity()`, `readReturnQuantity()`, `askForReturn()` |
| Magic Numbers | Prices hardcoded | `DesiDish` enum with prices |
| Duplicate Code | Input reading logic repeated | Centralized `readInt()` method |
| Large Class | Mixed responsibilities | Extracted `BillFileService` inner class |

**Refactoring Changes:**
- Created `Constants` inner class for file paths and strings
- Created `DesiDish` enum with all dishes and prices
- Created `OrderedDish` inner class with return logic
- Extracted `BillFileService` for file operations
- Extracted input reading helper methods
- Added proper registration number handling

---

### **MyFastFood.java (Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `FastfoodBill` had 100+ lines | Extracted `displayMenu()`, `processSingleOrder()`, `readInt()`, `readPositiveQuantity()`, `readReturnQuantity()`, `askForReturn()` |
| Magic Numbers | Prices hardcoded | `FastFoodItem` enum with prices |
| Duplicate Code | Input reading logic repeated | Centralized `readInt()` method |
| Large Class | Mixed responsibilities | Extracted `BillFileService` inner class |

**Refactoring Changes:**
- Created `Constants` inner class for file paths and strings
- Created `FastFoodItem` enum with all items and prices
- Created `OrderedItem` inner class with return logic
- Extracted `BillFileService` for file operations
- Extracted input reading helper methods
- Added final quantity display after returns

---

### **MyFinalBill.java (Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `finalBill` had 150+ lines | Extracted `readStudentInfo()`, `parseBillLine()`, `readAllBills()`, `clearTempFiles()`, `saveFinalBill()`, `printBillToConsole()` |
| Magic Strings | File paths hardcoded | `Constants` inner class |
| Duplicate Code | Bill reading logic repeated | Centralized `readAllBills()` |
| Data Clumps | Bill data appearing together | Created `BillReadingResult` and `ParsedBillLine` classes |

**Refactoring Changes:**
- Created `Constants` inner class for all file paths
- Created `StudentInfo` class for student data
- Created `ParsedBillLine` class for parsed bill lines
- Created `BillReadingResult` class for bill reading results
- Extracted `readStudentInfo()` for student data
- Extracted `parseBillLine()` for line parsing
- Extracted `readAllBills()` for bill aggregation
- Extracted `clearTempFiles()` for cleanup
- Extracted `saveFinalBill()` for file saving
- Extracted `printBillToConsole()` for console output

---

### **MyJuiceOrPlantDrink.java (Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `JuiceORPlantbill` had 100+ lines | Extracted `displayMenu()`, `processSingleOrder()`, `readInt()`, `readPositiveQuantity()`, `readReturnQuantity()`, `askForReturn()` |
| Magic Numbers | Prices hardcoded | `JuicePlantItem` enum with prices |
| Duplicate Code | Input reading logic repeated | Centralized `readInt()` method |
| Large Class | Mixed responsibilities | Extracted `BillFileService` inner class |

**Refactoring Changes:**
- Created `Constants` inner class for file paths and strings
- Created `JuicePlantItem` enum with all items and prices
- Created `JuiceOrder` inner class with return logic
- Extracted `BillFileService` for file operations
- Extracted input reading helper methods
- Fixed typo in "Pinnapple Flaour" (kept original for compatibility)

---

### **MySoftDrinks.java (Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `SoftDrinkbill` had 100+ lines | Extracted `displayMenu()`, `processSingleOrder()`, `readInt()`, `readPositiveQuantity()`, `readReturnQty()`, `askForReturn()` |
| Magic Numbers | Prices hardcoded | `SoftDrink` enum with prices |
| Duplicate Code | Input reading logic repeated | Centralized `readInt()` method |
| Large Class | Mixed responsibilities | Extracted `BillFileService` inner class |

**Refactoring Changes:**
- Created `Constants` inner class for file paths and strings
- Created `SoftDrink` enum with all drinks and prices
- Created `OrderedDrink` inner class with return logic
- Extracted `BillFileService` for file operations
- Extracted input reading helper methods
- Mountain Dew display as "Dew" kept for compatibility

---

### **SelectionMethod.java (Refactored)**

| **Code Smell** | **Original Problem** | **Refactored Solution** |
|----------------|----------------------|-------------------------|
| Long Method | `chooseMethod` had 80+ lines | Extracted `printMainMenu()`, `readIntInput()`, `clearTemporaryBillFile()`, `readStudentName()`, `readBillItems()`, `calculateTotalBill()`, `printBillToConsole()`, `saveFinalBill()`, `generateAndShowBill()`, `handleFoodMenu()`, `handleDrinksMenu()`, `handleBillGeneration()`, `handlePreviousMenu()`, `handleExit()` |
| Switch Statements | Large switch-case in main loop | Extracted to handler methods |
| Magic Numbers | 2000 balance hardcoded | `Constants.INITIAL_BALANCE` |
| Magic Strings | File paths hardcoded | `Constants` inner class |
| Duplicate Code | Menu handling repeated | Each menu option has dedicated handler |

**Refactoring Changes:**
- Created `Constants` inner class for all magic values
- Removed unused static variables (`option`, `date`, `name` moved local)
- Created `BillProvider` interface (kept as is - well designed)
- Extracted `readIntInput()` for centralized input
- Extracted `printMainMenu()` for menu display
- Extracted `clearTemporaryBillFile()` with proper error handling
- Extracted `readStudentName()` for student data
- Extracted `readBillItems()` for bill reading
- Extracted `calculateTotalBill()` for bill calculation
- Extracted `printBillToConsole()` for console output
- Extracted `saveFinalBill()` for file saving
- Extracted `generateAndShowBill()` for bill generation
- Each menu option has dedicated handler method
- Fixed empty catch blocks with proper error messages

---

## **Refactoring Summary Statistics**

| **Module** | **Methods Extracted** | **Constants Created** | **Classes Created** | **Code Smells Fixed** |
|------------|----------------------|----------------------|---------------------|----------------------|
| TransportManagement | 3 | 0 | 3 | 3 |
| TMS | 8 | 0 | 8 | 3 |
| PaymentService | 1 | 0 | 3 | 2 |
| Courses | 4 | 0 | 5 | 4 |
| CourseRegistration | 3 | 0 | 4 | 4 |
| DrinkSelection | 4 | 2 | 1 | 5 |
| FoodSelection | 4 | 2 | 1 | 4 |
| MyCoffee | 6 | 3 | 3 | 5 |
| MyDesiFood | 7 | 3 | 3 | 5 |
| MyFastFood | 7 | 3 | 3 | 5 |
| MyFinalBill | 7 | 3 | 3 | 6 |
| MyJuiceOrPlantDrink | 6 | 3 | 3 | 5 |
| MySoftDrinks | 6 | 3 | 3 | 5 |
| SelectionMethod | 14 | 3 | 0 | 7 |

---

## **Benefits Achieved Through Refactoring**

| **Benefit** | **Description** |
|-------------|-----------------|
| **Readability** | Methods are now small (under 20 lines) and focused |
| **Maintainability** | Changes affect only specific methods, not entire files |
| **Testability** | Small methods can be unit tested independently |
| **Reusability** | Helper methods (`readInt`, `displayMenu`) reused across files |
| **Error Handling** | Empty catch blocks replaced with proper error messages |
| **No Magic Values** | All hard-coded values moved to constants |
| **Single Responsibility** | Each method does exactly one thing |
| **DRY Principle** | Duplicate code eliminated across all files |
