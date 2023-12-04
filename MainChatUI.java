//import javax.swing.*;
//import javax.swing.text.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class MainChatUI extends JFrame {
//    private JTextPane chatArea;
//    private JTextField inputField;
//
//    public MainChatUI() {
//        setTitle("ChatBot");
//        setSize(400, 500);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        initUI();
//        displayOpeningMessage();
//    }
//
//    private void displayOpeningMessage() {
//        String openingMessage = "ChatBot: Hi there! How can I assist you today?";
//        appendMessage(openingMessage, Color.BLACK, Color.GREEN, StyleConstants.ALIGN_LEFT);
//
//    }
//
//    private void initUI() {
//        chatArea = new JTextPane();
//        chatArea.setEditable(false);
//        JScrollPane scrollPane = new JScrollPane(chatArea);
//        add(scrollPane, BorderLayout.CENTER);
//
//        JPanel inputPanel = new JPanel(new BorderLayout());
//        inputField = new JTextField();
//        JButton sendButton = new JButton("Send");
//        sendButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                sendMessage();
//            }
//        });
//
//        inputPanel.add(inputField, BorderLayout.CENTER);
//        inputPanel.add(sendButton, BorderLayout.EAST);
//
//        add(inputPanel, BorderLayout.SOUTH);
//    }
//
//    private void sendMessage() {
//        String userMessage = inputField.getText();
//        appendMessage("You: " + userMessage, Color.BLACK, Color.BLUE, StyleConstants.ALIGN_RIGHT);
//
//        // Simulate chatbot response (replace this with actual chatbot logic)
//        String chatbotResponse = "ChatBot: Hello! I'm a simple chatbot.";
//        appendMessage(chatbotResponse, Color.BLACK, Color.GREEN, StyleConstants.ALIGN_LEFT);
//
//        inputField.setText("");
//    }
//
//
//
//    private void appendMessage(String message, Color textColor, Color bgColor, int alignment) {
//        StyledDocument doc = chatArea.getStyledDocument();
//
//        SimpleAttributeSet set = new SimpleAttributeSet();
//        StyleConstants.setForeground(set, textColor);
//        StyleConstants.setAlignment(set, alignment);
//
//        // Set background color for the entire message
//        StyleConstants.setBackground(set, bgColor);
//        
//
//        try {
//            int length = doc.getLength();
//            doc.insertString(length, message + "\n", set);
//            doc.setParagraphAttributes(length, 4, set, false);
//        } catch (BadLocationException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//
//
//
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                new MainChatUI().setVisible(true);
//            }
//        });
//    }
//} works without conversations

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainChatUI extends JFrame {
    private JTextPane chatArea;
    private JTextField inputField;

    public MainChatUI() {
        setTitle("ChatBot");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        displayOpeningMessage();
    }

    private void displayOpeningMessage() {
        String openingMessage = "ChatBot: Hi there! How can I assist you today?";
        appendMessage(openingMessage, Color.BLACK, Color.GREEN, StyleConstants.ALIGN_LEFT);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Conversation buttons panel
        JPanel conversationPanel = new JPanel();
        JButton newConversationButton = new JButton("New Conversation");
        newConversationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearChatArea();
                displayOpeningMessage();
            }
        });
        conversationPanel.add(newConversationButton);

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
                sendMessage();
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

    private void sendMessage() {
        String userMessage = inputField.getText();
        appendMessage("You: " + userMessage, Color.BLACK, Color.BLUE, StyleConstants.ALIGN_RIGHT);

        // Simulate chatbot response (replace this with actual chatbot logic)
        String chatbotResponse = "ChatBot: Hello! I'm a simple chatbot.";
        appendMessage(chatbotResponse, Color.BLACK, Color.GREEN, StyleConstants.ALIGN_LEFT);

        inputField.setText("");
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

