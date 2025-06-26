import javax.swing.*;
import java.awt.event.*;

public class SimpleScreen {
    public static void main(String[] args) {
        // Create a new frame (window)
        JFrame frame = new JFrame("Simple Interface");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(null); // Absolute positioning

        // Create a label
        JLabel label = new JLabel("Enter your name:");
        label.setBounds(20, 20, 120, 25);
        frame.add(label);

        // Create a text field
        JTextField textField = new JTextField();
        textField.setBounds(140, 20, 120, 25);
        frame.add(textField);

        // Create a button
        JButton button = new JButton("Submit");
        button.setBounds(90, 60, 100, 30);
        frame.add(button);

        // Create a response label
        JLabel responseLabel = new JLabel("");
        responseLabel.setBounds(20, 100, 250, 25);
        frame.add(responseLabel);

        // Add button action
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = textField.getText();
                responseLabel.setText("Hello, " + name + "!");
            }
        });

        // Make the frame visible
        frame.setVisible(true);
    }
}
