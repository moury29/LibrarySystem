package library.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.sql.*;
import javax.swing.*;
import library.dao.DatabaseConnection;

public class LoginGUI extends JFrame {

    public LoginGUI() {
        setTitle("St Mary's Library - Login");
        setSize(750, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(1, 2));

        //  LEFT SIDE 
        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(30, 80, 140));
        leftPanel.setLayout(new GridBagLayout());

        JPanel leftContent = new JPanel();
        leftContent.setLayout(new BoxLayout(leftContent, BoxLayout.Y_AXIS));
        leftContent.setBackground(new Color(30, 80, 140));
        leftContent.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Logo in white rounded box
        JPanel logoBox = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        logoBox.setOpaque(false);
        logoBox.setLayout(new GridBagLayout());
        logoBox.setMaximumSize(new Dimension(170, 130));
        logoBox.setPreferredSize(new Dimension(170, 130));
        logoBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Load logo
        try {
            URL logoURL = getClass().getClassLoader().getResource("library/resources/logo.png");
            ImageIcon icon = null;
            if (logoURL != null) {
                icon = new ImageIcon(logoURL);
            } else {
                File logoFile = new File("src/library/resources/logo.png");
                if (!logoFile.exists()) logoFile = new File("bin/library/resources/logo.png");
                if (logoFile.exists()) icon = new ImageIcon(logoFile.getAbsolutePath());
            }
            if (icon != null) {
                int origW = icon.getIconWidth();
                int origH = icon.getIconHeight();
                int targetH = 100;
                int targetW = (int) ((double) origW / origH * targetH);
                Image scaled = icon.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
                logoBox.add(new JLabel(new ImageIcon(scaled)));
            }
        } catch (Exception e) {
            System.out.println("Logo error: " + e.getMessage());
        }

        JLabel uniName = new JLabel("St Mary's University");
        uniName.setFont(new Font("SansSerif", Font.BOLD, 15));
        uniName.setForeground(Color.WHITE);
        uniName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel locationLabel = new JLabel("Twickenham, London");
        locationLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        locationLabel.setForeground(new Color(180, 210, 255));
        locationLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel libLabel = new JLabel("Digital Library System");
        libLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        libLabel.setForeground(new Color(200, 225, 255));
        libLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        leftContent.add(Box.createVerticalStrut(15));
        leftContent.add(logoBox);
        leftContent.add(Box.createVerticalStrut(15));
        leftContent.add(uniName);
        leftContent.add(Box.createVerticalStrut(5));
        leftContent.add(locationLabel);
        leftContent.add(Box.createVerticalStrut(5));
        leftContent.add(libLabel);

        leftPanel.add(leftContent);

        //  RIGHT SIDE 
        JPanel rightPanel = new JPanel();
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setLayout(new GridBagLayout());

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setPreferredSize(new Dimension(280, 290));

        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        welcomeLabel.setForeground(new Color(30, 80, 140));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subWelcome = new JLabel("Login to access the library system");
        subWelcome.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subWelcome.setForeground(Color.GRAY);
        subWelcome.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        userLabel.setForeground(new Color(40, 40, 40));
        userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userField = new JTextField();
        userField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        userField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        userField.setAlignmentX(Component.LEFT_ALIGNMENT);
        userField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        passLabel.setForeground(new Color(40, 40, 40));
        passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPasswordField passField = new JPasswordField();
        passField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        passField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        passField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(30, 80, 140));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setBorderPainted(false);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                loginBtn.setBackground(new Color(20, 60, 120));
            }
            public void mouseExited(MouseEvent e) {
                loginBtn.setBackground(new Color(30, 80, 140));
            }
        });

        JLabel messageLabel = new JLabel("");
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        formPanel.add(welcomeLabel);
        formPanel.add(Box.createVerticalStrut(4));
        formPanel.add(subWelcome);
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

        JLabel footer = new JLabel(
            "© 2026 St Mary's University Library, Twickenham, London",
            SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 11));
        footer.setForeground(Color.GRAY);
        footer.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        add(footer, BorderLayout.SOUTH);

        // LOGIN LOGIC using database connection
        Runnable doLogin = () -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                messageLabel.setText("❌ Please enter username and password!");
                return;
            }

            try {
                String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
                PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    dispose();
                    SwingUtilities.invokeLater(() -> new DashboardGUI().setVisible(true));
                } else {
                    messageLabel.setText("❌ Invalid username or password!");
                    passField.setText("");
                    userField.requestFocus();
                }
            } catch (Exception ex) {
                messageLabel.setText("❌ Database error: " + ex.getMessage());
            }
        };

        loginBtn.addActionListener(e -> doLogin.run());
        passField.addActionListener(e -> doLogin.run());
        userField.addActionListener(e -> passField.requestFocus());
    }
}