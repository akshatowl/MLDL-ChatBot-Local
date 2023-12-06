import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainChatUI extends JFrame {
    JPanel conversationPanel = new JPanel();
    private JTextPane chatArea;
    private JTextField inputField;
    public final String sessionID = String.valueOf(System.currentTimeMillis());
    private int conversationCount = DataAdapter.sessions.size() + 1;
    public int messageNum = 0;
    private String username;
    public boolean allow = false;
    
    public MainChatUI() {
        setTitle("ChatBot");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        displayOpeningMessage();
        username = LoginPage.SendUsername();
        System.out.println("INMAINCHATUI" + username);
//        DataAdapter.getSessions(username);
        System.out.println("KHALI" + containsOnlyChatBotMessage());
    }
    public void setUsername(String username) {
        this.username = username;
    }

    private void displayOpeningMessage() {
        String openingMessage = "ChatBot: Hi there! How can I assist you today?";
        appendMessage(openingMessage, Color.BLACK, Color.GREEN, StyleConstants.ALIGN_LEFT);
    }
    
    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        conversationPanel.setLayout(new BoxLayout(conversationPanel, BoxLayout.Y_AXIS));
        // Conversation buttons panel
        JButton newConversationButton = new JButton("New Conversation");
        newConversationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearChatArea();
                displayOpeningMessage();
                messageNum = 0;
            }
        });
        conversationPanel.add(newConversationButton);
        
        for(int i = 0 ; i < DataAdapter.sessions.size() ; i++) {
        	JButton newButton = new JButton("Conversation " + (i+1));
            conversationPanel.add(newButton);
        }

        // Chat area panel
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(chatArea);

        // Input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 if(messageNum == 0 && !containsOnlyChatBotMessage())
                 {
                 	addConversationButton();

                 }
                sendMessage();
               
            }
        });
        Document document = inputField.getDocument();

        document.addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSendButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSendButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSendButtonState();
            }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // Add components to the main panel
        mainPanel.add(conversationPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);

    }
    public boolean containsOnlyChatBotMessage() {
        String expectedText = "ChatBot: Hi there! How can I assist you today?";

        // Get the text from the JTextPane
        String chatAreaText = getTextFromJTextComponent(chatArea);

        // Check if the chatAreaText exactly matches the expected string
        return chatAreaText.trim().equals(expectedText);
    }

    // Utility method to get text from JTextComponent
    private String getTextFromJTextComponent(JTextComponent textComponent) {
        Document doc = textComponent.getDocument();
        int length = doc.getLength();
        try {
            return doc.getText(0, length);
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }
    private void addConversationButton() {
        JButton conversationButton = new JButton("Conversation " + conversationCount);
        conversationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle switching to the selected conversation if needed
                // For now, you can print a message indicating the selected conversation.
                System.out.println("Switching to Conversation " + conversationCount);
            }
        });
        conversationCount++;
        conversationPanel.add(conversationButton);
        
        revalidate();
        repaint();
    }
 
    private void sendMessage() {
        String userMessage = inputField.getText();
        if(!userMessage.isEmpty()) {
        	
            appendMessage("You: " + userMessage, Color.BLACK, Color.BLUE, StyleConstants.ALIGN_RIGHT);
            DataAdapter.sendMessage(sessionID, username, userMessage);
            String chatbotResponse = "ChatBot: Hello! I'm a simple chatbot.";
            DataAdapter.sendMessage(sessionID, username,  chatbotResponse);
            appendMessage(chatbotResponse, Color.BLACK, Color.GREEN, StyleConstants.ALIGN_LEFT);
            messageNum++;
            System.out.println("KHALI"+containsOnlyChatBotMessage());
        }else {
//        	allow = false;
            JOptionPane.showMessageDialog(this, "Please enter a non-empty message.", "Empty Message", JOptionPane.WARNING_MESSAGE);
            
        }
        
        inputField.setText("");
        updateSendButtonState();  // Ensure the button state is updated after sending a message

    }

    private void appendMessage(String message, Color textColor, Color bgColor, int alignment) {
        StyledDocument doc = chatArea.getStyledDocument();
        	
        SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setForeground(set, textColor);
        StyleConstants.setAlignment(set, alignment);

        // Set background color for the entire message
        StyleConstants.setBackground(set, bgColor);

        try {
            int length = doc.getLength();
            doc.insertString(length, message + "\n", set);
            doc.setParagraphAttributes(length, 4, set, false);
            
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
    private void updateSendButtonState() {
        String userMessage = inputField.getText().trim();
        JButton sendButton = findSendButton();

        // Ensure sendButton is not null before attempting to set its state
        if (sendButton != null) {
            // Enable or disable the send button based on whether the input is empty or not
            sendButton.setEnabled(!userMessage.isEmpty());
        }
    }

    private JButton findSendButton() {
        Component[] components = getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                Component[] subComponents = ((JPanel) component).getComponents();
                for (Component subComponent : subComponents) {
                    if (subComponent instanceof JButton && ((JButton) subComponent).getText().equals("Send")) {
                        return (JButton) subComponent;
                    }
                }
            }
        }
        return null;
    }
    

    private void clearChatArea() {
        chatArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainChatUI().setVisible(true);
            }
        });
    }
}

