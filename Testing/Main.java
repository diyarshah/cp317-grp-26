// Main.java
public class Main {
    public static void main(String[] args) {
        javafx.application.Application.launch(MainWindow.class);
    }
}

// Student.java
public class Student {
    private final String studentId;
    private final String studentName;

    public Student(String studentId, String studentName) {
        this.studentId = studentId;
        this.studentName = studentName;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
}

// Course.java
public class Course {
    private final String studentId;
    private final String courseCode;
    private final double test1, test2, test3, finalExam;

    public Course(String studentId, String courseCode, double test1, double test2, double test3, double finalExam) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.test1 = test1;
        this.test2 = test2;
        this.test3 = test3;
        this.finalExam = finalExam;
    }

    public String getStudentId() { return studentId; }
    public String getCourseCode() { return courseCode; }
    public double getTest1() { return test1; }
    public double getTest2() { return test2; }
    public double getTest3() { return test3; }
    public double getFinalExam() { return finalExam; }
}

// StudentCourseRecord.java
public class StudentCourseRecord {
    private final String studentId;
    private final String studentName;
    private final String courseCode;
    private final double finalGrade;

    public StudentCourseRecord(String studentId, String studentName, String courseCode, double finalGrade) {
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseCode = courseCode;
        this.finalGrade = Math.round(finalGrade * 10.0) / 10.0;
    }

    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getCourseCode() { return courseCode; }
    public double getFinalGrade() { return finalGrade; }
}

// AbstractTextParser.java
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public abstract class AbstractTextParser<T> {
    public List<T> parse(File file) throws FileNotFoundException {
        List<T> result = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] tokens = scanner.nextLine().trim().split(" ");
                if (!isValid(tokens)) {
                    throw new IllegalArgumentException("Invalid line format: " + Arrays.toString(tokens));
                }
                result.add(toModel(tokens));
            }
        }
        return result;
    }

    protected abstract boolean isValid(String[] tokens);
    protected abstract T toModel(String[] tokens);
}

// NameFileParser.java
public class NameFileParser extends AbstractTextParser<Student> {
    @Override
    protected boolean isValid(String[] tokens) {
        return tokens.length >= 2;
    }

    @Override
    protected Student toModel(String[] tokens) {
        return new Student(tokens[0], String.join(" ", Arrays.copyOfRange(tokens, 1, tokens.length)));
    }
}

// CourseFileParser.java
public class CourseFileParser extends AbstractTextParser<Course> {
    @Override
    protected boolean isValid(String[] tokens) {
        return tokens.length == 6;
    }

    @Override
    protected Course toModel(String[] tokens) {
        return new Course(
            tokens[0],
            tokens[1],
            Double.parseDouble(tokens[2]),
            Double.parseDouble(tokens[3]),
            Double.parseDouble(tokens[4]),
            Double.parseDouble(tokens[5])
        );
    }
}

// CourseProcessor.java
import java.util.*;

public class CourseProcessor {
    public List<StudentCourseRecord> process(List<Student> students, List<Course> courses) {
        Map<String, String> studentMap = new HashMap<>();
        for (Student s : students) {
            studentMap.put(s.getStudentId(), s.getStudentName());
        }

        List<StudentCourseRecord> result = new ArrayList<>();
        for (Course c : courses) {
            String name = studentMap.get(c.getStudentId());
            if (name != null) {
                double grade = (c.getTest1() + c.getTest2() + c.getTest3()) * 0.2 + c.getFinalExam() * 0.4;
                result.add(new StudentCourseRecord(c.getStudentId(), name, c.getCourseCode(), grade));
            }
        }
        result.sort(Comparator.comparing(StudentCourseRecord::getStudentId));
        return result;
    }
}

// TextFileWriter.java
import java.io.*;
import java.util.List;

public class TextFileWriter {
    public void write(List<StudentCourseRecord> records, File outputFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(outputFile)) {
            for (StudentCourseRecord r : records) {
                writer.printf("%s %s %s %.1f\n",
                    r.getStudentId(),
                    r.getStudentName(),
                    r.getCourseCode(),
                    r.getFinalGrade());
            }
        }
    }
}

// MainWindow.java
import javafx.application.Application;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;
import java.io.File;
import java.util.List;

public class MainWindow extends Application {
    private Label status = new Label("Select input files and click Generate.");
    private File nameFile, courseFile;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Grade Report Generator");
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        Button nameBtn = new Button("Select Name File");
        nameBtn.setOnAction(e -> nameFile = chooseFile(stage));

        Button courseBtn = new Button("Select Course File");
        courseBtn.setOnAction(e -> courseFile = chooseFile(stage));

        Button generateBtn = new Button("Generate Report");
        generateBtn.setOnAction(e -> generateReport());

        root.getChildren().addAll(nameBtn, courseBtn, generateBtn, status);
        stage.setScene(new Scene(root, 400, 200));
        stage.show();
    }

    private File chooseFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        return chooser.showOpenDialog(stage);
    }

    private void generateReport() {
        if (nameFile == null || courseFile == null) {
            status.setText("Both input files must be selected.");
            return;
        }

        try {
            List<Student> students = new NameFileParser().parse(nameFile);
            List<Course> courses = new CourseFileParser().parse(courseFile);
            List<StudentCourseRecord> records = new CourseProcessor().process(students, courses);

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Save Report");
            File output = chooser.showSaveDialog(null);
            if (output != null) {
                new TextFileWriter().write(records, output);
                status.setText("Report generated: " + output.getAbsolutePath());
            }
        } catch (Exception e) {
            status.setText("Error: " + e.getMessage());
        }
    }
}
