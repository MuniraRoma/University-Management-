import java.util.*;
import java.io.*;

// ==============================================
// SIMPLIFIED VERSION WITH FACADE PATTERN
// ==============================================

// 1. DATA CLASSES (Keep as is)
class StudentInfo {
    public final String regNumber;
    public final String name;
    public boolean isGuest;

    public StudentInfo(String regNumber, String name) {
        this.regNumber = regNumber;
        this.name = name;
        this.isGuest = false;
    }

    public static StudentInfo guest(String regNumber) {
        StudentInfo s = new StudentInfo(regNumber, "Guest");
        s.isGuest = true;
        return s;
    }
}

class BillItem {
    public final String line;
    public final int price;

    public BillItem(String line, int price) {
        this.line = line;
        this.price = price;
    }
}

// 2. SIMPLE FILE READER (Simplified)
class SimpleBillReader {
    private static final String[] BILL_FILES = {
            "cafe/CafeBills/BillFastfood.txt",
            "cafe/CafeBills/BillDesiFood.txt",
            "cafe/CafeBills/BillSoftDrinks.txt",
            "cafe/CafeBills/BillCoffee.txt",
            "cafe/CafeBills/BillJuiceOrPlant.txt"
    };

    public List<BillItem> readAllBills() {
        List<BillItem> items = new ArrayList<>();

        for (String filePath : BILL_FILES) {
            File file = new File(filePath);
            if (file.exists()) {
                try (Scanner sc = new Scanner(file)) {
                    while (sc.hasNextLine()) {
                        String line = sc.nextLine().trim();
                        if (!line.isEmpty()) {
                            int price = extractPrice(line);
                            items.add(new BillItem(line, price));
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error reading " + filePath + ": " + e.getMessage());
                }
            }
        }
        return items;
    }

    private int extractPrice(String line) {
        String[] parts = line.split("\\s+");
        try {
            return Integer.parseInt(parts[parts.length - 1]);
        } catch (Exception e) {
            return 0;
        }
    }

    public void clearAllBills() {
        for (String filePath : BILL_FILES) {
            try (PrintWriter writer = new PrintWriter(filePath)) {
                writer.print("");
            } catch (Exception e) {
                // Ignore
            }
        }
    }

    public boolean hasBills() {
        for (String filePath : BILL_FILES) {
            File file = new File(filePath);
            if (file.exists() && file.length() > 0) {
                try (Scanner sc = new Scanner(file)) {
                    if (sc.hasNextLine()) return true;
                } catch (Exception e) {}
            }
        }
        return false;
    }
}

// 3. STUDENT DATA READER (Simplified)
class SimpleStudentReader {
    private static final String STUDENT_DIR = "students_data/";

    public StudentInfo getStudent(String regNumber) {
        File studentFile = new File(STUDENT_DIR + regNumber + ".txt");

        if (!studentFile.exists()) {
            return StudentInfo.guest(regNumber);
        }

        try (Scanner sc = new Scanner(studentFile)) {
            if (sc.hasNextLine()) {
                String line = sc.nextLine();
                String name = line.contains(":") ? line.split(":", 2)[1].trim() : line.trim();
                return new StudentInfo(regNumber, name);
            }
        } catch (Exception e) {
            System.err.println("Error reading student: " + e.getMessage());
        }

        return StudentInfo.guest(regNumber);
    }
}

// 4. THE FACADE - Simplified interface for the entire system
class BillSystemFacade {
    private final SimpleBillReader billReader;
    private final SimpleStudentReader studentReader;
    private static final String FINAL_BILL_DIR = "cafe/CafeBills/FinalBills/";

    public BillSystemFacade() {
        this.billReader = new SimpleBillReader();
        this.studentReader = new SimpleStudentReader();
    }

    /**
     * Main method - Generate final bill
     */
    public BillResult generateBill(String regNumber, int balance) {
        // Get student info
        StudentInfo student = studentReader.getStudent(regNumber);

        // Read all bills
        List<BillItem> items = billReader.readAllBills();

        if (items.isEmpty()) {
            return BillResult.noItems(balance);
        }

        // Calculate total
        int total = 0;
        for (BillItem item : items) {
            total += item.price;
        }

        // Check balance
        if (balance < total) {
            return BillResult.insufficientBalance(balance, total);
        }

        int remaining = balance - total;

        // Print bill
        printBill(student, items, total, remaining);

        // Save bill
        saveBill(student, items, total);

        // Clear temp files
        billReader.clearAllBills();

        return BillResult.success(remaining, total, items.size());
    }

    private void printBill(StudentInfo student, List<BillItem> items, int total, int remaining) {
        System.out.println("\n\t\t\t-----------------------------------");
        System.out.println("\t\t\tDate: " + new Date());
        System.out.println("\t\t\tName: " + student.name);
        System.out.println("\t\t\tStudent ID: " + student.regNumber);

        if (student.isGuest) {
            System.out.println("\t\t\t** GUEST USER **");
        }

        System.out.println("\t\t\t-----------Your Bill Is------------\n");
        System.out.println("\t\t\tItems                    Qty   Price");

        for (BillItem item : items) {
            System.out.println("\t\t\t" + item.line);
        }

        System.out.println("\t\t\t-----------------------------------");
        System.out.println("\t\t\tTotal Bill:                 " + total + " TK");
        System.out.println("\t\t\tRemaining Balance:          " + remaining + " TK");
        System.out.println("\t\t\t-----------------------------------");
    }

    private void saveBill(StudentInfo student, List<BillItem> items, int total) {
        File folder = new File(FINAL_BILL_DIR);
        if (!folder.exists()) folder.mkdirs();

        File billFile = new File(folder, student.regNumber + ".txt");

        try (PrintWriter pw = new PrintWriter(new FileWriter(billFile, true))) {
            pw.println("Date: " + new Date());
            pw.println("ID: " + student.regNumber);
            pw.println("Name: " + student.name);
            pw.println("-----------------------------------");

            for (BillItem item : items) {
                pw.println(item.line);
            }

            pw.println("-----------------------------------");
            pw.println("Total: " + total + " TK");
            pw.println();
        } catch (Exception e) {
            System.err.println("Error saving bill: " + e.getMessage());
        }
    }

    // Utility methods
    public boolean hasPendingBills() {
        return billReader.hasBills();
    }

    public int getPendingTotal() {
        List<BillItem> items = billReader.readAllBills();
        int total = 0;
        for (BillItem item : items) {
            total += item.price;
        }
        return total;
    }

    public void clearPendingBills() {
        billReader.clearAllBills();
    }
}

// 5. RESULT CLASS
class BillResult {
    public final boolean success;
    public final int remainingBalance;
    public final int totalBill;
    public final int itemCount;
    public final String message;

    private BillResult(boolean success, int remainingBalance, int totalBill, int itemCount, String message) {
        this.success = success;
        this.remainingBalance = remainingBalance;
        this.totalBill = totalBill;
        this.itemCount = itemCount;
        this.message = message;
    }

    public static BillResult success(int remaining, int total, int count) {
        return new BillResult(true, remaining, total, count, "Bill generated successfully");
    }

    public static BillResult noItems(int balance) {
        return new BillResult(false, balance, 0, 0, "No items to bill");
    }

    public static BillResult insufficientBalance(int balance, int required) {
        return new BillResult(false, balance, required, 0,
                String.format("Insufficient Balance! Need %d TK but only have %d TK", required, balance));
    }
}

// 6. MAIN CLASS - Super Simple!
public class MyFinalBill {
    private static BillSystemFacade billSystem = new BillSystemFacade();

    /**
     * Original method - maintains backward compatibility
     */
    public static int finalBill(String regNumber, int amount) {
        BillResult result = billSystem.generateBill(regNumber, amount);

        if (!result.success) {
            System.out.println(result.message);
        }

        return result.remainingBalance;
    }

    /**
     * Enhanced method with details
     */
    public static BillResult finalBillWithDetails(String regNumber, int amount) {
        return billSystem.generateBill(regNumber, amount);
    }

    /**
     * Check if there are pending bills
     */
    public static boolean hasPendingBills() {
        return billSystem.hasPendingBills();
    }

    /**
     * Get pending bill total
     */
    public static int getPendingBillTotal() {
        return billSystem.getPendingTotal();
    }

    /**
     * Clear all temp files
     */
    public static void clearAllTempFiles() {
        billSystem.clearPendingBills();
    }

    /**
     * Debug method
     */
    public static void debugBillFiles() {
        System.out.println("\n=== DEBUG: Checking Bill Files ===");
        BillSystemFacade debug = new BillSystemFacade();
        System.out.println("Has pending bills: " + debug.hasPendingBills());
        System.out.println("Pending total: " + debug.getPendingTotal());
    }
}