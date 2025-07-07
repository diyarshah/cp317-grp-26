import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

/**
 * A simple Swing application that reads FinalGrades.txt
 * and displays its contents in a JTable.
 */
public class FinalGradesViewer {
    private static final String FILE_PATH = "Text Files/FinalGrades.txt";

    public static void main(String[] args) {
        // Ensure GUI creation runs on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Create and show the UI
                new FinalGradesViewer().createAndShowGUI();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Error reading grades file: " + e.getMessage(),
                        "File Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Sets up the JFrame and JTable with data from the grades file.
     */
    private void createAndShowGUI() throws IOException {
        // Frame setup
        JFrame frame = new JFrame("Final Grades Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Read data and column names from the file
        DefaultTableModel tableModel = loadTableModel(FILE_PATH);

        // Create the table with non-editable cells
        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFillsViewportHeight(true);

        // Put the table in a scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Frame sizing and display
        frame.pack();
        frame.setLocationRelativeTo(null);  // center on screen
        frame.setVisible(true);
    }

    /**
     * Reads the CSV file and returns a DefaultTableModel
     * populated with column headers and rows.
     *
     * @param filename path to the CSV file
     * @return table model with data
     * @throws IOException if file I/O fails
     */
    private DefaultTableModel loadTableModel(String filename) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String headerLine = br.readLine();
            if (headerLine == null) {
                throw new IOException("The file is empty.");
            }

            // Split header into column names
            String[] columns = headerLine.split(",");
            for (int i = 0; i < columns.length; i++) {
                columns[i] = columns[i].trim();
            }

            // Use Vector for compatibility with DefaultTableModel
            Vector<String> columnNames = new Vector<>();
            for (String col : columns) {
                columnNames.add(col);
            }

            Vector<Vector<Object>> data = new Vector<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Vector<Object> row = new Vector<>();
                for (String part : parts) {
                    row.add(part.trim());
                }
                data.add(row);
            }

            return new DefaultTableModel(data, columnNames);
        }
    }
}
