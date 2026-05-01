import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class RegistrationFileManager {
    public boolean hasCourseRegistration(String regNumber) {
        try {
            File myfile = new File("students_data/" + regNumber + ".txt");
            if (!myfile.exists()) {
                return false;
            }
            
            Scanner sc = new Scanner(myfile);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains("Courses")) {
                    sc.close();
                    return true;
                }
            }
            sc.close();
        } catch (Exception e) {
            System.out.println("Error checking registration: " + e.getMessage());
        }
        return false;
    }
    
    public String[][] loadCourseDetails(String semester) {
        File myfile = new File("courses/" + semester + ".txt");
        int total_courses = countCourses(myfile);
        
        if (total_courses == 0) {
            return new String[0][0];
        }
        
        String[][] courseDetails = new String[total_courses][4];
        try {
            Scanner sc = new Scanner(myfile);
            int i = 0;
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                String[] data = line.split(",", 4);
                for (int j = 0; j < data.length; j++) {
                    courseDetails[i][j] = data[j];
                }
                i++;
            }
            sc.close();
        } catch (Exception e) {
            System.out.print("Error loading courses: " + e.getMessage());
        }
        return courseDetails;
    }
    
    public void saveRegisteredCourses(String regNumber, String[][] courseDetails, String[] selectedIds) {
        try {
            FileWriter fileWriter = new FileWriter("students_data/" + regNumber + ".txt", true);
            fileWriter.write("\nCourses:");
            
            for (int j = 0; j < selectedIds.length; j++) {
                int courseId = Integer.parseInt(selectedIds[j]);
                for (int i = 0; i < courseDetails[courseId].length; i++) {
                    fileWriter.write(courseDetails[courseId][i]);
                    if (i < 3) {
                        fileWriter.write(",");
                    }
                }
                if (j < selectedIds.length - 1) {
                    fileWriter.write(" | ");
                }
            }
            fileWriter.close();
        } catch (IOException e) {
            System.out.print("Error saving courses: " + e.getMessage());
        }
    }
    
    private int countCourses(File file) {
        int count = 0;
        try {
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                sc.nextLine();
                count++;
            }
            sc.close();
        } catch (Exception e) {
            return 0;
        }
        return count;
    }
}

class RegistrationDisplay {
    private static final int[] COLUMN_WIDTHS = {15, 30, 15, 30};
    
    public void displayCourseHeader() {
        System.out.printf("%-5s%-15s%-30s%-15s%-30s%n", 
            "ID", "Course_Code", "Course_Title", "Credit_Hours", "Instructor");
        System.out.println("------------------------------------------------------------------------------------------");
    }
    
    public void displayCourses(String[][] courseDetails) {
        System.out.print("\n\n");
        displayCourseHeader();
        
        for (int i = 0; i < courseDetails.length; i++) {
            System.out.printf("%-5d", i);
            for (int j = 0; j < courseDetails[i].length; j++) {
                System.out.printf("%-" + COLUMN_WIDTHS[j] + "s", courseDetails[i][j]);
            }
            System.out.println();
        }
    }
}

class RegistrationValidator {
    public boolean validateSemester(String semester) {
        try {
            int sem = Integer.parseInt(semester);
            return sem > 0 && sem <= 8;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public boolean validateCourseIds(String[] ids, int maxCourseId) {
        try {
            for (String id : ids) {
                int courseId = Integer.parseInt(id);
                if (courseId < 0 || courseId > maxCourseId) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

public class CourseRegistration {
    static Scanner input = new Scanner(System.in);
    
    public static void manage(String regNumber) {
        RegistrationFileManager fileManager = new RegistrationFileManager();
        RegistrationDisplay display = new RegistrationDisplay();
        RegistrationValidator validator = new RegistrationValidator();
        
        System.out.println("\n*******************************************************\n");
        System.out.println("\tCourse Registration\n");
        System.out.println("*******************************************************\n");
        
        if (fileManager.hasCourseRegistration(regNumber)) {
            System.out.print("Courses already registered!\n");
            return;
        }
        
        String semester = getValidSemester();
        String[][] courseDetails = fileManager.loadCourseDetails(semester);
        
        if (courseDetails.length == 0) {
            System.out.println("No courses available for this semester!");
            return;
        }
        
        display.displayCourses(courseDetails);
        
        String[] selectedIds = getValidCourseIds(courseDetails.length);
        
        try {
            FileWriter fileWriter = new FileWriter("students_data/" + regNumber + ".txt", true);
            fileWriter.write("\nCourses:");
            
            for (int j = 0; j < selectedIds.length; j++) {
                int courseId = Integer.parseInt(selectedIds[j]);
                for (int i = 0; i < courseDetails[courseId].length; i++) {
                    fileWriter.write(courseDetails[courseId][i]);
                    if (i < 3) {
                        fileWriter.write(",");
                    }
                }
                if (j < selectedIds.length - 1) {
                    fileWriter.write(" | ");
                }
            }
            fileWriter.close();
            System.out.println("Courses Registered Successfully!");
        } catch (IOException e) {
            System.out.print("Error saving courses: " + e.getMessage());
        }
    }
    
    private static String getValidSemester() {
        while (true) {
            try {
                System.out.print("Enter your semester(1 for first semester ..'1-8') : ");
                String semester = input.next();
                
                try {
                    int sem = Integer.parseInt(semester);
                    if (sem > 0 && sem <= 8) {
                        return semester;
                    } else {
                        System.out.println("Invalid input, Try again!");
                        input.nextLine();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input, Try again!");
                    input.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Invalid input, Try again!");
                input.nextLine();
            }
        }
    }
    
    private static String[] getValidCourseIds(int maxCourseId) {
        while (true) {
            try {
                System.out.print("Enter the ID's of courses you want to register(comma separated) : ");
                String[] ids = input.next().split(",");
                
                boolean valid = true;
                try {
                    for (String id : ids) {
                        int courseId = Integer.parseInt(id);
                        if (courseId < 0 || courseId > maxCourseId) {
                            valid = false;
                            break;
                        }
                    }
                    if (valid) {
                        return ids;
                    } else {
                        System.out.println("Invalid course IDs, Try again!");
                        input.nextLine();
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input, Try again!");
                    input.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Invalid input, Try again!");
                input.nextLine();
            }
        }
    }
}
