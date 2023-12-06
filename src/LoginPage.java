import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class LoginPage extends JFrame {
	static String username;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        setTitle("Login Page MLDL Chatbot");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        JPanel formPanel = new JPanel(new SpringLayout());
        formPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(8);
        formPanel.add(usernameField);
        formPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(8);
        formPanel.add(passwordField);

        SpringUtilities.makeCompactGrid(formPanel,
                2, 2, //rows, cols
                6, 6, //initX, initY
                6, 6); //xPad, yPad

        mainPanel.add(Box.createVerticalStrut(20)); // Add vertical spacing

        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalStrut(20)); // Add vertical spacing

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        JButton loginButton = new JButton("Login");
        customizeButton(loginButton);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT); // Align the button to the right
        buttonPanel.add(loginButton);

        // Add vertical spacing
        buttonPanel.add(Box.createVerticalStrut(10));

        JButton signUpButton = new JButton("New User Sign Up");
        customizeButton(signUpButton);
        signUpButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSignUp();
            }
        });
        buttonPanel.add(signUpButton);

        mainPanel.add(buttonPanel);

        // Center the mainPanel within the JFrame
        mainPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        getContentPane().add(Box.createVerticalGlue()); // Push components to the top
        getContentPane().add(mainPanel);
        getContentPane().add(Box.createVerticalGlue()); // Push components to the bottom
       
    }
    public String getUsername() {
        return usernameField.getText().trim();
    }

    private String getPassword() {
        return new String(passwordField.getPassword()).trim();
    }
    
    private void customizeButton(JButton button) {
        button.setBackground(new Color(141, 176, 205));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 30));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(141, 176, 205), 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onButtonClick(button);
            }
        });
    }

    private void onButtonClick(JButton button) {
        if (button.getText().equals("Login")) {
            onLogin();
        } else if (button.getText().equals("New User Sign Up")) {
            onSignUp();
        }
    }

    public static String SendUsername() {
    	return username;
    }
    private void onLogin() {
        username = getUsername();
        String password = getPassword();
        
        // Authenticate the user using your authentication logic
        boolean isAuthenticated = true;

        if (isAuthenticated) {
            // If authentication is successful, open the MainPage
        	DataAdapter.getSessions(username);
        	System.out.println("-------------------");
        	System.out.println(DataAdapter.sessions);
        	
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new MainChatUI().setVisible(true);
                }
            });

            // Close the current LoginPage
            dispose();
        } else {
            // If authentication fails, show an error message
            JOptionPane.showMessageDialog(this, "Invalid username or password", "Authentication Failed", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void onSignUp() {
        // Implement your sign-up logic here
        // You can navigate to the sign-up page or perform any other actions
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginPage().setVisible(true);
            }
        });
    }
}
