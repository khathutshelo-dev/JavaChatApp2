package chatapp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class ChatGUI extends JFrame {

    private JPanel chatPanel;
    private JTextField messageField;
    private JButton sendButton;
    private DefaultListModel<String> userModel;
    private JList<String> userList;

    private BufferedReader input;
    private PrintWriter output;

    private String username;

    public ChatGUI(String username) {

        this.username = username;

        setTitle("Java Messenger - " + username);

        setSize(900, 600);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // ===== LEFT USERS PANEL =====
        JPanel usersPanel = new JPanel();

        usersPanel.setPreferredSize(new Dimension(200, 0));

        usersPanel.setBackground(new Color(32, 33, 36));

        usersPanel.setLayout(new BorderLayout());

        JLabel usersLabel = new JLabel(" Online Users");

        usersLabel.setForeground(Color.WHITE);

        usersLabel.setFont(new Font("Arial",
                Font.BOLD, 18));

        usersLabel.setBorder(
                new EmptyBorder(10, 10, 10, 10));

        usersPanel.add(usersLabel,
                BorderLayout.NORTH);

        userModel = new DefaultListModel<>();

        userList = new JList<>(userModel);

        userList.setBackground(
                new Color(44, 47, 51));

        userList.setForeground(Color.WHITE);

        userList.setFont(
                new Font("Arial",
                        Font.PLAIN,
                        16));

        JScrollPane userScroll = new JScrollPane(userList);

        usersPanel.add(userScroll,
                BorderLayout.CENTER);

        add(usersPanel, BorderLayout.WEST);

        // ===== CHAT AREA =====
        chatPanel = new JPanel();

        chatPanel.setLayout(
                new BoxLayout(chatPanel,
                        BoxLayout.Y_AXIS));

        chatPanel.setBackground(
                new Color(54, 57, 63));

        JScrollPane scrollPane = new JScrollPane(chatPanel);

        scrollPane.setBorder(null);

        add(scrollPane, BorderLayout.CENTER);

        // ===== BOTTOM INPUT PANEL =====
        JPanel inputPanel = new JPanel();

        inputPanel.setLayout(
                new BorderLayout());

        inputPanel.setBackground(
                new Color(47, 49, 54));

        // Message field
        messageField = new JTextField();

        messageField.setFont(
                new Font("Arial",
                        Font.PLAIN,
                        16));

        messageField.setBackground(
                new Color(64, 68, 75));

        messageField.setForeground(Color.WHITE);

        messageField.setCaretColor(Color.WHITE);

        messageField.setBorder(
                new EmptyBorder(10, 10, 10, 10));

        inputPanel.add(messageField,
                BorderLayout.CENTER);

        // ===== EMOJI BUTTON =====
        JButton emojiButton = new JButton("😊");

        emojiButton.setFont(
                new Font("Arial",
                        Font.PLAIN,
                        18));

        emojiButton.addActionListener(e -> {

            String[] emojis = {
                    ":)",
                    ":D",
                    "<3",
                    ":P",
                    ";)",
                    ":(",
                    "XD",
                    ":O"
            };
            String emoji = (String) JOptionPane.showInputDialog(
                    this,
                    "Choose Emoji",
                    "Emoji Picker",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    emojis,
                    emojis[0]);

            if (emoji != null) {

                messageField.setText(
                        messageField.getText()
                                + emoji);
            }
        });

        inputPanel.add(emojiButton,
                BorderLayout.WEST);

        // ===== SEND BUTTON =====
        sendButton = new JButton("Send");

        sendButton.setBackground(
                new Color(88, 101, 242));

        sendButton.setForeground(Color.WHITE);

        sendButton.setFont(
                new Font("Arial",
                        Font.BOLD,
                        16));

        inputPanel.add(sendButton,
                BorderLayout.EAST);

        add(inputPanel,
                BorderLayout.SOUTH);

        connectToServer();

        sendButton.addActionListener(
                e -> sendMessage());

        messageField.addActionListener(
                e -> sendMessage());

        setVisible(true);

        receiveMessages();
    }

    // ===== CONNECT =====
    private void connectToServer() {

        try {

            Socket socket = new Socket("localhost",
                    5000);

            input = new BufferedReader(
                    new InputStreamReader(
                            socket.getInputStream()));

            output = new PrintWriter(
                    socket.getOutputStream(),
                    true);

            output.println(username);

        } catch (IOException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Cannot connect to server.");
        }
    }

    // ===== SEND MESSAGE =====
    private void sendMessage() {

        String message = messageField.getText();

        if (!message.isEmpty()) {

            output.println(message);

            messageField.setText("");
        }
    }

    // ===== RECEIVE =====
    private void receiveMessages() {

        Thread thread = new Thread(() -> {

            try {

                String message;

                while ((message = input.readLine()) != null) {

                    // USER LIST
                    if (message.startsWith(
                            "USERLIST:")) {

                        updateUserList(message);

                    } else {

                        addMessageBubble(message);
                    }
                }

            } catch (IOException e) {

                addMessageBubble(
                        "Disconnected from server.");
            }
        });

        thread.start();
    }

    // ===== UPDATE ONLINE USERS =====
    private void updateUserList(String users) {

        SwingUtilities.invokeLater(() -> {

            userModel.clear();

            String[] userArray = users.replace(
                    "USERLIST:",
                    "").split(",");

            for (String user : userArray) {

                if (!user.trim().isEmpty()) {

                    userModel.addElement(user);
                }
            }
        });
    }

    // ===== MESSAGE BUBBLES =====
    private void addMessageBubble(String message) {

        SwingUtilities.invokeLater(() -> {

            JPanel bubblePanel = new JPanel();

            bubblePanel.setLayout(
                    new FlowLayout(
                            FlowLayout.LEFT));

            bubblePanel.setBackground(
                    new Color(54, 57, 63));

            JLabel bubble = new JLabel(
                    "<html><p style='width: 300px;'>"
                            + message
                            + "</p></html>");

            bubble.setOpaque(true);

            bubble.setBackground(
                    new Color(88, 101, 242));

            bubble.setForeground(Color.WHITE);

            bubble.setFont(
                    new Font("Arial",
                            Font.PLAIN,
                            15));

            bubble.setBorder(
                    new EmptyBorder(
                            10, 15, 10, 15));

            bubblePanel.add(bubble);

            chatPanel.add(bubblePanel);

            chatPanel.revalidate();

            JScrollBar vertical = ((JScrollPane) chatPanel.getParent()
                    .getParent())
                    .getVerticalScrollBar();

            vertical.setValue(
                    vertical.getMaximum());
        });
    }

    public static void main(String[] args) {

        String username = JOptionPane.showInputDialog(
                "Enter Username");

        if (username != null &&
                !username.trim().isEmpty()) {

            new ChatGUI(username);
        }
    }
}