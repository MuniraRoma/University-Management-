import java.io.*;
import java.util.*;

class Course {
    private String code;
    private String title;
    private String creditHours;
    private String instructor;

    public Course(String code,String title,String creditHours,String instructor){
        this.code = code;
        this.title = title;
        this.creditHours = creditHours;
        this.instructor = instructor;
    }

    public static Course fromLine(String line){
        String[] data = line.split(",",4);
        return new Course(data[0],data[1],data[2],data[3]);
    }

    public String toFileString(){
        return code + "," + title + "," + creditHours + "," + instructor;
    }

    public String getCode(){ return code; }
    public String getTitle(){ return title; }
    public String getCreditHours(){ return creditHours; }
    public String getInstructor(){ return instructor; }
}

class RegistrationFileManager {

    public boolean hasCourseRegistration(String regNumber){

        File file = new File("students_data/" + regNumber + ".txt");

        if(!file.exists())
            return false;

        try(Scanner sc = new Scanner(file)){

            while(sc.hasNextLine()){
                if(sc.nextLine().contains("Courses")){
                    return true;
                }
            }

        }catch(Exception e){
            System.out.println("Error checking registration");
        }

        return false;
    }

    public List<Course> loadCourseDetails(String semester){

        List<Course> courses = new ArrayList<>();

        try(Scanner sc = new Scanner(new File("courses/" + semester + ".txt"))){

            while(sc.hasNextLine()){
                courses.add(Course.fromLine(sc.nextLine()));
            }

        }catch(Exception e){
            System.out.println("Error loading courses");
        }

        return courses;
    }

    public void saveRegisteredCourses(String regNumber,List<Course> selectedCourses){

        try(FileWriter writer = new FileWriter("students_data/" + regNumber + ".txt",true)){

            writer.write("\nCourses:");

            for(int i=0;i<selectedCourses.size();i++){

                writer.write(selectedCourses.get(i).toFileString());

                if(i < selectedCourses.size()-1)
                    writer.write(" | ");
            }

        }catch(IOException e){
            System.out.println("Error saving courses");
        }
    }

    public List<Course> loadRegisteredCourses(String regNumber){

        List<Course> courses = new ArrayList<>();

        try(Scanner sc = new Scanner(new File("students_data/" + regNumber + ".txt"))){

            while(sc.hasNextLine()){

                String line = sc.nextLine();

                if(line.startsWith("Courses:")){

                    line = line.replace("Courses:","");

                    String[] courseLines = line.split("\\|");

                    for(String c : courseLines){
                        courses.add(Course.fromLine(c.trim()));
                    }
                }
            }

        }catch(Exception e){
            return courses;
        }

        return courses;
    }
}

class RegistrationDisplay {

    public void displayHeader(){

        System.out.printf("%-5s%-15s%-30s%-15s%-30s%n",
                "ID","Course_Code","Course_Title","Credit_Hours","Instructor");

        System.out.println("------------------------------------------------------------------------------------------");
    }

    public void displayCourses(List<Course> courses){

        displayHeader();

        for(int i=0;i<courses.size();i++){

            Course c = courses.get(i);

            System.out.printf("%-5d%-15s%-30s%-15s%-30s%n",
                    i,
                    c.getCode(),
                    c.getTitle(),
                    c.getCreditHours(),
                    c.getInstructor());
        }
    }
}

class RegistrationValidator {

    public boolean validateSemester(String semester){

        try{

            int sem = Integer.parseInt(semester);

            return sem >=1 && sem <=8;

        }catch(NumberFormatException e){
            return false;
        }
    }

    public boolean validateCourseIds(List<Integer> ids,int max){

        for(int id : ids){

            if(id < 0 || id >= max)
                return false;
        }

        return true;
    }
}

public class CourseRegistration {

    static Scanner input = new Scanner(System.in);

    public static void manage(String regNumber){

        RegistrationFileManager fileManager = new RegistrationFileManager();
        RegistrationDisplay display = new RegistrationDisplay();
        RegistrationValidator validator = new RegistrationValidator();

        System.out.println("\n************ Course Registration ************\n");

        if(fileManager.hasCourseRegistration(regNumber)){
            System.out.println("Courses already registered!");
            viewRegisteredCourses(regNumber);
            return;
        }

        String semester = getValidSemester(validator);

        List<Course> courses = fileManager.loadCourseDetails(semester);

        if(courses.isEmpty()){
            System.out.println("No courses available!");
            return;
        }

        display.displayCourses(courses);

        List<Integer> ids = getValidCourseIds(courses.size(),validator);

        List<Course> selectedCourses = new ArrayList<>();

        for(int id : ids){
            selectedCourses.add(courses.get(id));
        }

        fileManager.saveRegisteredCourses(regNumber,selectedCourses);

        System.out.println("Courses Registered Successfully!");

        viewRegisteredCourses(regNumber);
    }

    private static String getValidSemester(RegistrationValidator validator){

        while(true){

            System.out.print("Enter semester (1-8): ");

            String semester = input.next();

            if(validator.validateSemester(semester))
                return semester;

            System.out.println("Invalid semester!");
        }
    }

    private static List<Integer> getValidCourseIds(int maxCourses,RegistrationValidator validator){

        while(true){

            System.out.print("Enter course IDs (comma separated): ");

            String[] inputIds = input.next().split(",");

            List<Integer> ids = new ArrayList<>();

            try{

                for(String id : inputIds){
                    ids.add(Integer.parseInt(id));
                }

                if(validator.validateCourseIds(ids,maxCourses))
                    return ids;

            }catch(Exception e){}

            System.out.println("Invalid course IDs!");
        }
    }

    public static void viewRegisteredCourses(String regNumber){

        RegistrationFileManager fileManager = new RegistrationFileManager();
        RegistrationDisplay display = new RegistrationDisplay();

        List<Course> courses = fileManager.loadRegisteredCourses(regNumber);

        if(courses.isEmpty()){
            System.out.println("No courses registered yet!");
            return;
        }

        System.out.println("\nRegistered Courses:\n");

        display.displayCourses(courses);
    }
}