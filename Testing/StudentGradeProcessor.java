import java.io.*;
import java.util.*;

import java.nio.file.Files;
import java.nio.file.Paths;

// Abstract base class using Abstraction
abstract class Person {
    // Encapsulated private fields for ID and name
    private String id;
    private String name;

    // Constructor with defensive checks (offensive programming)
    public Person(String id, String name) {
        if (id == null || name == null || id.isEmpty() || name.isEmpty()) {
            throw new IllegalArgumentException("ID and Name must not be null or empty.");
        }
        this.id = id;
        this.name = name;
    }

    // Public getter for ID (encapsulation)
    public String getId() {
        return id;
    }

    // Public getter for Name (encapsulation)
    public String getName() {
        return name;
    }
}

// Student class inherits from Person (Inheritance)
class Student extends Person {
    // List of courses this student is enrolled in
    private List<Course> courses;

    // Constructor
    public Student(String id, String name) {
        super(id, name); // Call base class constructor
        courses = new ArrayList<>();
    }

    // Add a course to the student's list, with null check (offensive programming)
    public void addCourse(Course course) {
        if (course == null) {
            throw new IllegalArgumentException("Course cannot be null.");
        }
        courses.add(course);
    }

    // Get list of courses (encapsulation)
    public List<Course> getCourses() {
        return courses;
    }
}

// Class representing a Course with grades
class Course {
    private String courseCode;
    private double test1, test2, test3, finalExam;

     // Constructor with input validation (offensive programming)
    public Course(String courseCode, double test1, double test2, double test3, double finalExam) {
        if (courseCode == null || courseCode.isEmpty()) {
            throw new IllegalArgumentException("Course code must not be empty.");
        }
        // Validate all grades are between 0 and 100
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

    // Getter for course code
    public String getCourseCode() {
        return courseCode;
    }

    // Compute final grade using weighted average
    public double computeFinalGrade() {
        // Each test counts for 20%, final exam 40%
        return roundToOneDecimal((test1 + test2 + test3) * 0.2 + finalExam * 0.4);
    }

     // Helper method to round to one decimal place
    private double roundToOneDecimal(double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}

// Main class to process the students and their grades
public class StudentGradeProcessor {
    // Map to store Student ID to Student object
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

    /**
     * Reads the student names and IDs from a CSV file.
     * @param filename Path to the NameFile.txt
     * @throws IOException If file reading fails
     */

    public static void readNameFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 2)
                    throw new IllegalArgumentException("Malformed line in NameFile: " + line);
                String id = parts[0].trim();
                String name = parts[1].trim();
                // Add new Student object to map
                students.put(id, new Student(id, name));
            }
        }
    }

    /**
     * Reads course and grades data from a CSV file and assigns courses to students.
     * @param filename Path to the CourseFile.txt
     * @throws IOException If file reading fails
     */
    public static void readCourseFile(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split line by comma, expect six parts
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

                 // Add new course to the student's list of courses
                student.addCourse(new Course(courseCode, t1, t2, t3, finalExam));
            }
        }
    }

    /**
     * Writes the final grades to a file, sorted by Student ID.
     * @param filename Output filename
     * @throws IOException If file writing fails
     */
    public static void writeOutputFile(String filename) throws IOException {
         // Use try-with-resources to auto-close PrintWriter
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Write header line with spaces after commas
            writer.println("Student ID, Student Name, Course Code, Final Grade");
    
            // Sort students by ID using TreeMap (keys are student IDs)
            Map<String, Student> sortedStudents = new TreeMap<>(students);
    
            // Iterate students in sorted order
            for (Student student : sortedStudents.values()) {
                // For each course, write a formatted line with final grade
                for (Course course : student.getCourses()) {
                    writer.printf("%s,%s,%s,%.1f%n",
                        student.getId(),
                        student.getName(),
                        course.getCourseCode(),
                        course.computeFinalGrade());
                }
            }
        }
    }

    /**
     * Reads the given CSV file, sorts all data rows by Student ID (first column),
     * and rewrites the file in sorted order (keeping the header on top).
     */
    public static void sortFinalGradesFile(String filename) throws IOException {
        // Read all lines
        List<String> lines = Files.readAllLines(Paths.get(filename));
        if (lines.size() <= 1) return;  // nothing to sort

        String header = lines.remove(0);  // keep header aside
        // Sort by the first comma-separated field (the Student ID)
        lines.sort(Comparator.comparing(line -> line.split(",")[0].trim()));

        // Rewrite file: header + sorted data
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println(header);
            for (String line : lines) {
                writer.println(line);
            }
        }
    }
}
