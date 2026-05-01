import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Single Responsibility: Each class has one job
class CourseDisplay {
    private static final int[] COLUMN_WIDTHS = {15, 30, 15, 30};
    
    public void displayCourseHeader() {
        System.out.printf("%-5s%-15s%-30s%-15s%-30s%n", 
            "ID", "Course_Code", "Course_Title", "Credit_Hours", "Instructor");
        System.out.println("------------------------------------------------------------------------------------------");
    }
    
    public void displayCourseLine(String line, int index) {
        String[] data = line.split(",", 4);
        System.out.printf("%-5d", index);
        for (int j = 0; j < data.length; j++) {
            System.out.printf("%-" + COLUMN_WIDTHS[j] + "s", data[j]);
        }
        System.out.println();
    }
}

class CourseFileManager {
    public String[] loadCoursesForSemester(int semester) {
        List<String> courses = new ArrayList<>();
        try {
            File file = new File("courses/" + semester + ".txt");
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                courses.add(sc.nextLine());
            }
            sc.close();
        } catch (Exception e) {
            System.out.print("Error loading courses: " + e.getMessage());
        }
        return courses.toArray(new String[0]);
    }
    
    public void saveStudentCourses(String regNumber, String[] courses) {
        try {
            FileWriter fileWriter = new FileWriter("students_data/courses/" + regNumber + ".txt", true);
            for (String course : courses) {
                fileWriter.write(course + "\n");
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.print("Error saving courses: " + e.getMessage());
        }
    }
    
    public String[] loadStudentCourses(String regNumber) {
        List<String> courses = new ArrayList<>();
        try {
            File file = new File("students_data/courses/" + regNumber + ".txt");
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                courses.add(sc.nextLine());
            }
            sc.close();
        } catch (Exception e) {
            System.out.print("No courses registered yet or error reading: " + e.getMessage());
        }
        return courses.toArray(new String[0]);
    }
    
    public boolean hasRegisteredCourses(String regNumber) {
        File file = new File("students_data/courses/" + regNumber + ".txt");
        return file.exists();
    }
}

class SemesterCalculator {
    public int getSemesterFromRegNumber(String regNumber) {
        String prefix = regNumber.substring(0, 4);
        
        switch(prefix) {
            case "SP18": return 8;
            case "FA18": return 7;
            case "SP19": return 6;
            case "FA19": return 5;
            case "SP20": return 4;
            case "FA20": return 3;
            case "SP21": return 2;
            case "FA21": return 1;
            default: return 0;
        }
    }
}

class CourseRegistrationValidator {
    public boolean validateCourseCount(int selectedCount, int availableCount) {
        if (selectedCount < 4) {
            System.out.print("At least register 4 courses\n");
            return false;
        }
        if (selectedCount > availableCount) {
            System.out.print("Only " + availableCount + " courses are being offered!\n");
            return false;
        }
        return true;
    }
    
    public boolean validateCourseSelection(int choice, int courseCount, int[] selectedIds, int currentIndex) {
        if (choice < 0 || choice >= courseCount) {
            System.out.print("Invalid Id, Try again!\n");
            return false;
        }
        
        for (int i = 0; i < currentIndex; i++) {
            if (selectedIds[i] == choice) {
                System.out.print("Already Registered!\n");
                return false;
            }
        }
        return true;
    }
}

public class Courses {
    static Scanner input = new Scanner(System.in);
    static String[] courseDetails;
    
    private static CourseDisplay courseDisplay = new CourseDisplay();
    private static CourseFileManager fileManager = new CourseFileManager();
    private static SemesterCalculator semesterCalculator = new SemesterCalculator();
    private static CourseRegistrationValidator validator = new CourseRegistrationValidator();
    
    public static void manage(String regNumber) {
        System.out.println("\n*******************************************************\n");
        System.out.println("\tManage Courses\n");
        System.out.println("*******************************************************\n\n");
        
        while (true) {
            displayMenu();
            
            try {
                int c = input.nextInt();
                
                if (c == 3) {
                    // Go back to previous menu
                    return;
                } else if (c == 4) {
                    System.exit(0);
                }
                
                handleMenuChoice(c, regNumber);
                
            } catch (Exception e) {
                System.out.print("Invalid input! Try again!\n");
                input.nextLine();
            }
        }
    }
    
    private static void displayMenu() {
        System.out.print("*****************************************************\n");
        System.out.print("1. Register Courses\n");
        System.out.print("2. View Registered Courses\n");
        System.out.print("3. Previous Menu\n");
        System.out.print("4. Exit\n");
        System.out.print("\nEnter your choice : ");
    }
    
    private static void handleMenuChoice(int choice, String regNumber) {
        switch(choice) {
            case 1:
                registerCourse(regNumber);
                break;
            case 2:
                viewCourse(regNumber);
                break;
        }
    }
    
    public static void registerCourse(String regNumber) {
        try {
            if (fileManager.hasRegisteredCourses(regNumber)) {
                System.out.print("Course already registered!\n");
                return;
            }
            
            int semester = semesterCalculator.getSemesterFromRegNumber(regNumber);
            if (semester == 0) {
                System.out.print("Invalid semester in registration number!\n");
                return;
            }
            
            courseDetails = fileManager.loadCoursesForSemester(semester);
            if (courseDetails.length == 0) {
                System.out.print("No courses available for this semester!\n");
                return;
            }
            
            displayAvailableCourses();
            registerSelectedCourses(regNumber);
            
        } catch (Exception e) {
            System.out.print("Error occurred!\n");
        }
    }
    
    private static void displayAvailableCourses() {
        System.out.print("\n\n");
        courseDisplay.displayCourseHeader();
        
        for (int i = 0; i < courseDetails.length; i++) {
            courseDisplay.displayCourseLine(courseDetails[i], i);
        }
        
        System.out.print("At least register 4 courses\n");
    }
    
    private static void registerSelectedCourses(String regNumber) {
        while (true) {
            try {
                System.out.print("\nHow many courses you want to register: ");
                int amount = input.nextInt();
                
                if (!validator.validateCourseCount(amount, courseDetails.length)) {
                    continue;
                }
                
                int[] selectedIds = selectCourseIds(amount);
                saveSelectedCourses(regNumber, selectedIds);
                System.out.println("Courses Registered Successfully!");
                break;
                
            } catch (Exception e) {
                System.out.print("Invalid input! Try again");
                input.nextLine();
            }
        }
    }
    
    private static int[] selectCourseIds(int amount) {
        int[] ids = new int[amount];
        int currentIndex = 0;
        
        while (currentIndex < amount) {
            try {
                System.out.print("\n*************course" + (currentIndex + 1) + "*******************\n");
                System.out.print("Enter choice: ");
                int choice = input.nextInt();
                
                if (validator.validateCourseSelection(choice, courseDetails.length, ids, currentIndex)) {
                    ids[currentIndex] = choice;
                    currentIndex++;
                }
                
            } catch (Exception e) {
                System.out.print("Invalid input! Try again...");
                input.nextLine();
            }
        }
        
        return ids;
    }
    
    private static void saveSelectedCourses(String regNumber, int[] selectedIds) {
        String[] selectedCourses = new String[selectedIds.length];
        for (int i = 0; i < selectedIds.length; i++) {
            selectedCourses[i] = courseDetails[selectedIds[i]];
        }
        fileManager.saveStudentCourses(regNumber, selectedCourses);
    }
    
    public static void viewCourse(String regNumber) {
        System.out.print("You are registered in following courses: \n");
        
        String[] registeredCourses = fileManager.loadStudentCourses(regNumber);
        if (registeredCourses.length == 0) {
            System.out.print("No courses registered yet!\n");
            return;
        }
        
        System.out.print("\n\n");
        courseDisplay.displayCourseHeader();
        
        for (int i = 0; i < registeredCourses.length; i++) {
            courseDisplay.displayCourseLine(registeredCourses[i], i);
        }
    }
}