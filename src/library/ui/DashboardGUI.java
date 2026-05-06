package library.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.List;
import javax.swing.*;
import library.dao.*;
import library.model.*;

public class DashboardGUI extends JFrame {

    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowRecordDAO borrowDAO = new BorrowRecordDAO();

    public DashboardGUI() {
        setTitle("St Mary's Digital Library - Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.addTab("📊 Dashboard", buildDashboardPanel());
        tabs.addTab("📚 Books", buildModulePanel("Books"));
        tabs.addTab("👤 Members", buildModulePanel("Members"));
        tabs.addTab("📋 Borrow Records", buildModulePanel("Borrow Records"));
        add(tabs, BorderLayout.CENTER);

        JLabel statusBar = new JLabel("  Welcome, Admin!");
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setPreferredSize(new Dimension(0, 25));
        add(statusBar, BorderLayout.SOUTH);
    }

    //  HEADER 

    private JPanel buildHeader() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(30, 80, 140));
        headerPanel.setPreferredSize(new Dimension(0, 65));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        leftPanel.setBackground(new Color(30, 80, 140));

        try {
            File logoFile = new File("src/library/resources/logo.png");
            if (!logoFile.exists()) logoFile = new File("bin/library/resources/logo.png");
            if (logoFile.exists()) {
                ImageIcon icon = new ImageIcon(logoFile.getAbsolutePath());
                Image scaled = icon.getImage().getScaledInstance(48, 48, Image.SCALE_SMOOTH);
                leftPanel.add(new JLabel(new ImageIcon(scaled)));
            }
        } catch (Exception ignored) {}

        JLabel title = new JLabel("St Mary's Digital Library System");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        leftPanel.add(title);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
        rightPanel.setBackground(new Color(30, 80, 140));

        JButton logoutBtn = new JButton("⏻  Logout");
        logoutBtn.setBackground(new Color(178, 34, 34));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { logoutBtn.setBackground(new Color(139, 20, 20)); }
            public void mouseExited(MouseEvent e)  { logoutBtn.setBackground(new Color(178, 34, 34)); }
        });
        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
            }
        });
        rightPanel.add(logoutBtn);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);
        return headerPanel;
    }

    //  DASHBOARD PANEL 

    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 245));

        JLabel welcome = new JLabel("Library Dashboard — Overview", SwingConstants.CENTER);
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        welcome.setForeground(new Color(30, 80, 140));
        welcome.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(welcome, BorderLayout.NORTH);

        List<Book> allBooks        = bookDAO.getAllBooks();
        List<Member> allMembers    = memberDAO.getAllMembers();
        List<BorrowRecord> records = borrowDAO.getAllRecords();
        List<BorrowRecord> overdue = borrowDAO.getOverdueRecords();
        long borrowed = allBooks.stream()
            .filter(b -> b.getAvailabilityStatus().equals("Borrowed")).count();

        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        cardsPanel.setBackground(new Color(245, 245, 245));
        cardsPanel.add(createStatCard("📚 Total Books",    String.valueOf(allBooks.size()),  new Color(70, 130, 180)));
        cardsPanel.add(createStatCard("👤 Total Members",  String.valueOf(allMembers.size()), new Color(46, 139, 87)));
        cardsPanel.add(createStatCard("📖 Active Borrows", String.valueOf(borrowed),          new Color(200, 120, 0)));
        cardsPanel.add(createStatCard("⚠ Overdue Books",  String.valueOf(overdue.size()),    new Color(178, 34, 34)));
        panel.add(cardsPanel, BorderLayout.CENTER);

        JPanel infoPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        infoPanel.setBackground(new Color(245, 245, 245));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Recent borrows
        JPanel borrowPanel = new JPanel(new BorderLayout());
        borrowPanel.setBorder(BorderFactory.createTitledBorder("Recent Borrow Records"));
        DefaultListModel<String> borrowModel = new DefaultListModel<>();
        if (records.isEmpty()) {
            borrowModel.addElement("No records found.");
        } else {
            records.stream().limit(5).forEach(r ->
                borrowModel.addElement("  #" + r.getRecordId() +
                    "  Book:" + r.getBookId() +
                    "  Member:" + r.getMemberId() +
                    "  " + r.getReturnStatus()));
        }
        JList<String> borrowList = new JList<>(borrowModel);
        borrowList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        borrowPanel.add(new JScrollPane(borrowList));

        // Available books
        JPanel availPanel = new JPanel(new BorderLayout());
        availPanel.setBorder(BorderFactory.createTitledBorder("Available Books"));
        DefaultListModel<String> availModel = new DefaultListModel<>();
        List<Book> available = allBooks.stream()
            .filter(b -> b.getAvailabilityStatus().equals("Available")).toList();
        if (available.isEmpty()) {
            availModel.addElement("No available books.");
        } else {
            available.stream().limit(5).forEach(b ->
                availModel.addElement("  " + b.getBookId() + ".  " + b.getTitle() + " — " + b.getAuthor()));
        }
        JList<String> availList = new JList<>(availModel);
        availList.setFont(new Font("SansSerif", Font.PLAIN, 12));
        availPanel.add(new JScrollPane(availList));

        infoPanel.add(borrowPanel);
        infoPanel.add(availPanel);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    //  MODULE PANELS 

    private JPanel buildModulePanel(String moduleName) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Back button — just arrow
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backBtn = new JButton("←");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 16));
        backBtn.setBackground(new Color(100, 100, 100));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setToolTipText("Back to Dashboard");
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { backBtn.setBackground(new Color(60, 60, 60)); }
            public void mouseExited(MouseEvent e)  { backBtn.setBackground(new Color(100, 100, 100)); }
        });
        backBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardGUI().setVisible(true));
        });
        topBar.add(backBtn);
        panel.add(topBar, BorderLayout.NORTH);

        // Center content
        JPanel center = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel label = new JLabel("Click below to manage " + moduleName, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        center.add(label, gbc);

        gbc.gridy = 1;
        JButton openBtn = new JButton("Open " + moduleName + " Manager");
        openBtn.setBackground(new Color(30, 80, 140));
        openBtn.setForeground(Color.WHITE);
        openBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        openBtn.setFocusPainted(false);
        openBtn.setBorderPainted(false);
        openBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        openBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { openBtn.setBackground(new Color(20, 60, 120)); }
            public void mouseExited(MouseEvent e)  { openBtn.setBackground(new Color(30, 80, 140)); }
        });
        openBtn.addActionListener(e -> {
            LibraryGUI gui = new LibraryGUI();
            gui.setVisible(true);
        });
        center.add(openBtn, gbc);
        panel.add(center, BorderLayout.CENTER);

        return panel;
    }

    //  STAT CARD WITH HOVER EFFECT

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        card.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                card.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                card.setBackground(color);
            }
        });

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(Color.WHITE);

        JLabel valueLabel = new JLabel(value, SwingConstants.CENTER);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }
}