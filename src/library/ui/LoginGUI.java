package library.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;

public class LoginGUI extends JFrame {

    public LoginGUI() {
        setTitle("St Mary's Library - Login");
        setSize(750, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        // ── MAIN PANEL (left blue + right white) ─────────────────────
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        // ── LEFT SIDE (blue background with logo) ────────────────────
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(30, 80, 140));
        leftPanel.setLayout(new GridBagLayout());

        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setBackground(new Color(30, 80, 140));

        // Logo
        try {
            File logoFile = new File("src/library/resources/logo.png");
            if (!logoFile.exists()) logoFile = new File("bin/library/resources/logo.png");
            if (logoFile.exists()) {
                ImageIcon icon = new ImageIcon(logoFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                JLabel logoLabel = new JLabel(new ImageIcon(scaled));
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                leftContent.add(logoLabel);
                leftContent.add(Box.createVerticalStrut(15));
            }
        } catch (Exception e) {
            System.out.println("Logo error: " + e.getMessage());
        }

        JLabel uniName = new JLabel("St Mary's University");
        uniName.setFont(new Font("SansSerif", Font.BOLD, 16));
        uniName.setForeground(Color.WHITE);
        uniName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel uniSub = new JLabel("Twickenham, London");
        uniSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        uniSub.setForeground(new Color(180, 210, 255));
        uniSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel libName = new JLabel("Digital Library System");
        libName.setFont(new Font("SansSerif", Font.ITALIC, 13));
        libName.setForeground(new Color(200, 225, 255));
        libName.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftContent.add(uniName);
        leftContent.add(Box.createVerticalStrut(5));
        leftContent.add(uniSub);
        leftContent.add(Box.createVerticalStrut(10));
        leftContent.add(libName);

        leftPanel.add(leftContent);

        // ── RIGHT SIDE (white login form) ────────────────────────────
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(280, 300));

        JLabel loginTitle = new JLabel("Welcome Back");
        loginTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        loginTitle.setForeground(new Color(30, 80, 140));
        loginTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel loginSub = new JLabel("Please login to continue");
        loginSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        loginSub.setForeground(Color.GRAY);
        loginSub.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Username
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        userLabel.setForeground(new Color(60, 60, 60));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userField = new JTextField();
        userField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        userField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Password
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        passLabel.setForeground(new Color(60, 60, 60));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login button with hover
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(30, 80, 140));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.setBorderPainted(false);
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(new Color(20, 60, 120));
            }
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(new Color(30, 80, 140));
            }
        });

        // Error message
        JLabel messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Assemble form
        formPanel.add(loginTitle);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(loginSub);
        formPanel.add(Box.createVerticalStrut(25));
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(userField);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(passLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(passField);
        formPanel.add(Box.createVerticalStrut(20));
        formPanel.add(loginBtn);
        formPanel.add(Box.createVerticalStrut(8));
        formPanel.add(messageLabel);

        rightPanel.add(formPanel);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        add(mainPanel, BorderLayout.CENTER);

        // Footer
        JLabel footer = new JLabel("© 2026 St Mary's University Library", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.ITALIC, 10));
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        add(footer, BorderLayout.SOUTH);

        // Login logic
        Runnable doLogin = () -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            if (username.equals("admin") && password.equals("library26")) {
                dispose();
                SwingUtilities.invokeLater(() -> new DashboardGUI().setVisible(true));
            } else {
                messageLabel.setText("❌ Invalid username or password!");
                passField.setText("");
                userField.requestFocus();
            }
        };

        loginBtn.addActionListener(e -> doLogin.run());
        passField.addActionListener(e -> doLogin.run());
        userField.addActionListener(e -> passField.requestFocus());
    }
}