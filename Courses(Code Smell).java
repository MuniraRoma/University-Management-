import java.io.*;
import java.util.*;

class Course {
    private String code;
    private String title;
    private String creditHours;
    private String instructor;

    public Course(String code, String title, String creditHours, String instructor) {
        this.code = code;
        this.title = title;
        this.creditHours = creditHours;
        this.instructor = instructor;
    }

    public static Course fromLine(String line) {
        String[] data = line.split(",", 4);
        return new Course(data[0], data[1], data[2], data[3]);
    }

    public String toFileString() {
        return code + "," + title + "," + creditHours + "," + instructor;
    }

    public String getCode(){ return code; }
    public String getTitle(){ return title; }
    public String getCreditHours(){ return creditHours; }
    public String getInstructor(){ return instructor; }
}

class CourseDisplay {

    public void displayHeader() {
        System.out.printf("%-5s%-15s%-30s%-15s%-30s%n",
                "ID","Course_Code","Course_Title","Credit_Hours","Instructor");
        System.out.println("------------------------------------------------------------------------------------------");
    }

    public void displayCourse(Course course, int index) {
        System.out.printf("%-5d%-15s%-30s%-15s%-30s%n",
                index,
                course.getCode(),
                course.getTitle(),
                course.getCreditHours(),
                course.getInstructor());
    }
}

class CourseFileManager {

    public List<Course> loadCoursesForSemester(int semester) {

        List<Course> courses = new ArrayList<>();

        try (Scanner sc = new Scanner(new File("courses/" + semester + ".txt"))) {

            while (sc.hasNextLine()) {
                courses.add(Course.fromLine(sc.nextLine()));
            }

        } catch (IOException e) {
            System.out.println("Error loading courses: " + e.getMessage());
        }

        return courses;
    }

    public void saveStudentCourses(String regNumber, List<Course> courses) {

        try (FileWriter writer = new FileWriter("students_data/courses/" + regNumber + ".txt", true)) {

            for (Course course : courses) {
                writer.write(course.toFileString() + "\n");
            }

        } catch (IOException e) {
            System.out.println("Error saving courses: " + e.getMessage());
        }
    }

    public List<Course> loadStudentCourses(String regNumber) {

        List<Course> courses = new ArrayList<>();

        try (Scanner sc = new Scanner(new File("students_data/courses/" + regNumber + ".txt"))) {

            while (sc.hasNextLine()) {
                courses.add(Course.fromLine(sc.nextLine()));
            }

        } catch (Exception e) {
            System.out.println("No courses registered yet.");
        }

        return courses;
    }

    public boolean hasRegisteredCourses(String regNumber) {

        File file = new File("students_data/courses/" + regNumber + ".txt");
        return file.exists();
    }
}

class SemesterCalculator {

    private static final Map<String,Integer> semesterMap = new HashMap<>();

    static {
        semesterMap.put("SP18",8);
        semesterMap.put("FA18",7);
        semesterMap.put("SP19",6);
        semesterMap.put("FA19",5);
        semesterMap.put("SP20",4);
        semesterMap.put("FA20",3);
        semesterMap.put("SP21",2);
        semesterMap.put("FA21",1);
    }

    public int getSemesterFromRegNumber(String regNumber) {

        String prefix = regNumber.substring(0,4);

        return semesterMap.getOrDefault(prefix,0);
    }
}

class CourseRegistrationValidator {

    public boolean validateCourseCount(int selectedCount, int availableCount) {

        if(selectedCount < 4){
            System.out.println("At least register 4 courses");
            return false;
        }

        if(selectedCount > availableCount){
            System.out.println("Only "+availableCount+" courses offered!");
            return false;
        }

        return true;
    }

    public boolean validateCourseSelection(int choice,int courseCount,Set<Integer> selected){

        if(choice < 0 || choice >= courseCount){
            System.out.println("Invalid ID!");
            return false;
        }

        if(selected.contains(choice)){
            System.out.println("Already Registered!");
            return false;
        }

        return true;
    }
}

public class Courses {

    private static Scanner input = new Scanner(System.in);

    private static CourseDisplay display = new CourseDisplay();
    private static CourseFileManager fileManager = new CourseFileManager();
    private static SemesterCalculator semesterCalculator = new SemesterCalculator();
    private static CourseRegistrationValidator validator = new CourseRegistrationValidator();

    public static void manage(String regNumber) {

        while(true){

            showMenu();

            int menuChoice = input.nextInt();

            switch(menuChoice){

                case 1:
                    registerCourse(regNumber);
                    break;

                case 2:
                    viewCourses(regNumber);
                    break;

                case 3:
                    return;

                case 4:
                    System.exit(0);

                default:
                    System.out.println("Invalid choice");
            }
        }
    }

    private static void showMenu(){

        System.out.println("\n1. Register Courses");
        System.out.println("2. View Registered Courses");
        System.out.println("3. Previous Menu");
        System.out.println("4. Exit");

        System.out.print("Enter choice: ");
    }

    public static void registerCourse(String regNumber){

        if(fileManager.hasRegisteredCourses(regNumber)){
            System.out.println("Courses already registered!");
            return;
        }

        int semester = semesterCalculator.getSemesterFromRegNumber(regNumber);

        if(semester == 0){
            System.out.println("Invalid registration number!");
            return;
        }

        List<Course> courses = fileManager.loadCoursesForSemester(semester);

        if(courses.isEmpty()){
            System.out.println("No courses available!");
            return;
        }

        displayCourses(courses);

        registerSelectedCourses(regNumber,courses);
    }

    private static void displayCourses(List<Course> courses){

        display.displayHeader();

        for(int i=0;i<courses.size();i++){
            display.displayCourse(courses.get(i),i);
        }
    }

    private static void registerSelectedCourses(String regNumber,List<Course> courses){

        while(true){

            System.out.print("How many courses: ");

            int count = input.nextInt();

            if(!validator.validateCourseCount(count,courses.size()))
                continue;

            List<Course> selected = selectCourses(count,courses);

            fileManager.saveStudentCourses(regNumber,selected);

            System.out.println("Courses Registered Successfully!");

            break;
        }
    }

    private static List<Course> selectCourses(int amount,List<Course> courses){

        Set<Integer> selectedIds = new HashSet<>();
        List<Course> selectedCourses = new ArrayList<>();

        while(selectedCourses.size() < amount){

            System.out.print("Enter course ID: ");

            int id = input.nextInt();

            if(validator.validateCourseSelection(id,courses.size(),selectedIds)){

                selectedIds.add(id);

                selectedCourses.add(courses.get(id));
            }
        }

        return selectedCourses;
    }

    public static void viewCourses(String regNumber){

        List<Course> courses = fileManager.loadStudentCourses(regNumber);

        if(courses.isEmpty()){
            System.out.println("No courses registered!");
            return;
        }

        display.displayHeader();

        for(int i=0;i<courses.size();i++){
            display.displayCourse(courses.get(i),i);
        }
    }
}