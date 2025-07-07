import java.io.*;
import java.util.*;

// Abstract base class using Abstraction
abstract class Person {
    private String id;
    private String name;

    public Person(String id, String name) {
        if (id == null || name == null || id.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException("ID and Name must not be null or empty.");
        }
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class Student extends Person {
    private List<Course> courses;

    public Student(String id, String name) {
        super(id, name);
        courses = new ArrayList<>();
    }

    public void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null.");
        }
        courses.add(course);
    }

    public List<Course> getCourses() {
        return courses;
    }
}

class Course {
    private String courseCode;
    private double test1, test2, test3, finalExam;

    public Course(String courseCode, double test1, double test2, double test3, double finalExam) {
        if (courseCode == null || courseCode.isEmpty()) {
            throw new IllegalArgumentException("Course code must not be empty.");
        }
        for (double grade : new double[]{test1, test2, test3, finalExam}) {
            if (grade < 0 || grade > 100) {
                throw new IllegalArgumentException("Grade values must be between 0 and 100.");
            }
        }
        this.courseCode = courseCode;
        this.test1 = test1;
        this.test2 = test2;
        this.test3 = test3;
        this.finalExam = finalExam;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public double computeFinalGrade() {
        return roundToOneDecimal((test1 + test2 + test3) * 0.2 + finalExam * 0.4);
    }

    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

public class StudentGradeProcessor {
    private static Map<String, Student> students = new HashMap<>();

    public static void main(String[] args) {
        try {
            readNameFile("Text Files/NameFile.txt");
            readCourseFile("Text Files/CourseFile.txt");
            writeOutputFile("Text Files/FinalGrades.txt");
            System.out.println("Processing complete. Output written to Text Files/FinalGrades.txt");
        } catch (IOException e) {
            System.err.println("File error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Processing error: " + e.getMessage());
        }
    }

    public static void readNameFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2)
                    throw new IllegalArgumentException("Malformed line in NameFile: " + line);
                String id = parts[0].trim();
                String name = parts[1].trim();
                students.put(id, new Student(id, name));
            }
        }
    }

    public static void readCourseFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 6)
                    throw new IllegalArgumentException("Malformed line in CourseFile: " + line);

                String id = parts[0].trim();
                String courseCode = parts[1].trim();
                double t1 = Double.parseDouble(parts[2].trim());
                double t2 = Double.parseDouble(parts[3].trim());
                double t3 = Double.parseDouble(parts[4].trim());
                double finalExam = Double.parseDouble(parts[5].trim());

                Student student = students.get(id);
                if (student == null) {
                    System.err.println("Warning: Student ID " + id + " not found in name file. Skipping...");
                    continue;
                }

                student.addCourse(new Course(courseCode, t1, t2, t3, finalExam));
            }
        }
    }

    public static void writeOutputFile(String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("Student ID, Student Name, Course Code, Final Grade");
    
            // Sort students by ID (TreeMap automatically sorts by key)
            Map<String, Student> sortedStudents = new TreeMap<>(students);
    
            for (Student student : sortedStudents.values()) {
                for (Course course : student.getCourses()) {
                    writer.printf("%s, %s, %s, %.1f%n",
                        student.getId(),
                        student.getName(),
                        course.getCourseCode(),
                        course.computeFinalGrade());
                }
            }
        }
    }
}
