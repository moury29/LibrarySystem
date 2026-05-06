package library.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.*;
import javax.swing.table.*;
import library.dao.*;
import library.model.*;

public class LibraryGUI extends JFrame {

    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowRecordDAO borrowDAO = new BorrowRecordDAO();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private DefaultTableModel bookTableModel;
    private DefaultTableModel memberTableModel;
    private DefaultTableModel borrowTableModel;
    private JLabel statusBar;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JButton activeBtn = null;

    public LibraryGUI() {
        setTitle("St Mary's Digital Library - Management");
        setSize(1100, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        // ── HEADER ────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 80, 140));
        header.setPreferredSize(new Dimension(0, 55));

        JPanel leftHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        leftHeader.setBackground(new Color(30, 80, 140));

        JButton backBtn = new JButton("←");
        backBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        backBtn.setBackground(new Color(30, 80, 140));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorderPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.setToolTipText("Back to Dashboard");
        backBtn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                backBtn.setBackground(new Color(20, 60, 120));
            }
            public void mouseExited(MouseEvent e) {
                backBtn.setBackground(new Color(30, 80, 140));
            }
        });
        backBtn.addActionListener(e -> {
            executor.shutdown();
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardGUI().setVisible(true));
        });
        leftHeader.add(backBtn);

        JLabel titleLabel = new JLabel("Library Management System",
            SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ── BODY ──────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout());

        // ── SIDEBAR ───────────────────────────────────────────────────
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 60));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JLabel menuLabel = new JLabel("  MENU");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        menuLabel.setForeground(new Color(150, 150, 180));
        menuLabel.setBorder(BorderFactory.createEmptyBorder(20, 15, 10, 0));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(menuLabel);

        JButton booksBtn   = sidebarButton("📚  Books");
        JButton membersBtn = sidebarButton("👤  Members");
        JButton borrowBtn  = sidebarButton("📋  Borrow Records");
        JButton searchBtn  = sidebarButton("🔍  Search");

        sidebar.add(booksBtn);
        sidebar.add(membersBtn);
        sidebar.add(borrowBtn);
        sidebar.add(searchBtn);
        sidebar.add(Box.createVerticalGlue());

        // ── CONTENT ───────────────────────────────────────────────────
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(buildBooksPanel(),   "Books");
        contentPanel.add(buildMembersPanel(), "Members");
        contentPanel.add(buildBorrowPanel(),  "Borrow Records");
        contentPanel.add(buildSearchPanel(),  "Search");

        body.add(sidebar, BorderLayout.WEST);
        body.add(contentPanel, BorderLayout.CENTER);
        add(body, BorderLayout.CENTER);

        // ── STATUS BAR ────────────────────────────────────────────────
        statusBar = new JLabel("  Ready");
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setPreferredSize(new Dimension(0, 25));
        add(statusBar, BorderLayout.SOUTH);

        // Sidebar actions
        booksBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Books");
            setActiveBtn(booksBtn);
            refreshBooks();
        });
        membersBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Members");
            setActiveBtn(membersBtn);
            refreshMembers();
        });
        borrowBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Borrow Records");
            setActiveBtn(borrowBtn);
            refreshBorrows();
        });
        searchBtn.addActionListener(e -> {
            cardLayout.show(contentPanel, "Search");
            setActiveBtn(searchBtn);
        });

        // Default view
        cardLayout.show(contentPanel, "Books");
        setActiveBtn(booksBtn);
        refreshBooks();
        refreshMembers();
        refreshBorrows();
    }

    // ── SIDEBAR BUTTON ────────────────────────────────────────────────────

    private JButton sidebarButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btn.setForeground(new Color(200, 200, 220));
        btn.setBackground(new Color(40, 40, 60));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (btn != activeBtn)
                    btn.setBackground(new Color(60, 60, 90));
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeBtn)
                    btn.setBackground(new Color(40, 40, 60));
            }
        });
        return btn;
    }

    private void setActiveBtn(JButton btn) {
        if (activeBtn != null) {
            activeBtn.setBackground(new Color(40, 40, 60));
            activeBtn.setForeground(new Color(200, 200, 220));
            activeBtn.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }
        activeBtn = btn;
        activeBtn.setBackground(new Color(30, 80, 140));
        activeBtn.setForeground(Color.WHITE);
        activeBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
    }

    // ── BOOKS PANEL ───────────────────────────────────────────────────────

    private JPanel buildBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("📚  Book Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 140));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.setBackground(Color.WHITE);
        JTextField bookSearch = new JTextField(20);
        bookSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));
        bookSearch.setPreferredSize(new Dimension(200, 30));
        bookSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        JButton bookSearchBtn = styledButton("🔍 Search",
            new Color(30, 80, 140), null);
        JButton bookClearBtn  = styledButton("✕ Clear",
            new Color(120, 120, 120), null);
        searchBar.add(new JLabel("Search: "));
        searchBar.add(bookSearch);
        searchBar.add(bookSearchBtn);
        searchBar.add(bookClearBtn);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(searchBar, BorderLayout.CENTER);
        panel.add(topSection, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Title", "Author", "Category", "Status"};
        bookTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(bookTableModel);
        styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnBar.setBackground(Color.WHITE);
        btnBar.add(styledButton("➕ Add Book",
            new Color(46, 139, 87),   e -> showAddBookDialog()));
        btnBar.add(styledButton("✏ Update",
            new Color(70, 130, 180),  e -> showEditBookDialog(table)));
        btnBar.add(styledButton("🗑 Delete",
            new Color(178, 34, 34),   e -> deleteSelectedBook(table)));
        btnBar.add(styledButton("🔄 Refresh",
            new Color(100, 100, 100), e -> refreshBooks()));
        panel.add(btnBar, BorderLayout.SOUTH);

        // Search actions
        bookSearchBtn.addActionListener(e -> {
            String kw = bookSearch.getText().trim();
            if (kw.isEmpty()) { refreshBooks(); return; }
            List<Book> results = bookDAO.searchBooks(kw);
            bookTableModel.setRowCount(0);
            for (Book b : results)
                bookTableModel.addRow(new Object[]{
                    b.getBookId(), b.getTitle(),
                    b.getAuthor(), b.getCategory(),
                    b.getAvailabilityStatus()});
            setStatus("Found " + results.size() + " book(s).");
        });
        bookSearch.addActionListener(e -> bookSearchBtn.doClick());
        bookClearBtn.addActionListener(e -> {
            bookSearch.setText("");
            refreshBooks();
        });

        return panel;
    }

    private void refreshBooks() {
        setStatus("Loading books...");
        executor.submit(() -> {
            List<Book> books = bookDAO.getAllBooks();
            SwingUtilities.invokeLater(() -> {
                bookTableModel.setRowCount(0);
                for (Book b : books)
                    bookTableModel.addRow(new Object[]{
                        b.getBookId(), b.getTitle(),
                        b.getAuthor(), b.getCategory(),
                        b.getAvailabilityStatus()});
                setStatus("Books loaded: " + books.size() + " record(s).");
            });
        });
    }

    private void showAddBookDialog() {
        JTextField titleF    = new JTextField(20);
        JTextField authorF   = new JTextField(20);
        JTextField categoryF = new JTextField(20);

        // Add book — only Available or Reserved
        // Borrowed is set automatically when borrow record is created
        JComboBox<String> statusBox = new JComboBox<>(
            new String[]{"Available", "Reserved"});

        JPanel form = buildFormPanel(
            "Title:",    titleF,
            "Author:",   authorF,
            "Category:", categoryF,
            "Status:",   statusBox
        );

        int result = JOptionPane.showConfirmDialog(this, form,
            "Add New Book", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String t = titleF.getText().trim();
            String a = authorF.getText().trim();
            String c = categoryF.getText().trim();

            if (!Validator.isNotEmpty(t)) {
                showError("Title cannot be empty."); return;
            }
            if (!Validator.isNotEmpty(a)) {
                showError("Author cannot be empty."); return;
            }
            if (!Validator.isNotEmpty(c)) {
                showError("Category cannot be empty."); return;
            }

            Book book = new Book(t, a, c,
                (String) statusBox.getSelectedItem());
            if (bookDAO.addBook(book)) {
                showSuccess("Book added successfully!");
                refreshBooks();
            } else {
                showError("Failed to add book. Please try again.");
            }
        }
    }

    private void showEditBookDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a book to update."); return;
        }
        int bookId = (int) table.getValueAt(row, 0);
        Book book  = bookDAO.getBookById(bookId);
        if (book == null) { showError("Book not found."); return; }

        JTextField titleF    = new JTextField(book.getTitle(), 20);
        JTextField authorF   = new JTextField(book.getAuthor(), 20);
        JTextField categoryF = new JTextField(book.getCategory(), 20);

        // Update book — all 3 statuses available
        JComboBox<String> statusBox = new JComboBox<>(
            new String[]{"Available", "Borrowed", "Reserved"});
        statusBox.setSelectedItem(book.getAvailabilityStatus());

        JPanel form = buildFormPanel(
            "Title:",    titleF,
            "Author:",   authorF,
            "Category:", categoryF,
            "Status:",   statusBox
        );

        int result = JOptionPane.showConfirmDialog(this, form,
            "Update Book #" + bookId, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String t = titleF.getText().trim();
            String a = authorF.getText().trim();
            String c = categoryF.getText().trim();

            if (!Validator.isNotEmpty(t)) {
                showError("Title cannot be empty."); return;
            }
            if (!Validator.isNotEmpty(a)) {
                showError("Author cannot be empty."); return;
            }
            if (!Validator.isNotEmpty(c)) {
                showError("Category cannot be empty."); return;
            }

            book.setTitle(t);
            book.setAuthor(a);
            book.setCategory(c);
            book.setAvailabilityStatus((String) statusBox.getSelectedItem());

            if (bookDAO.updateBook(book)) {
                showSuccess("Book updated successfully!");
                refreshBooks();
            } else {
                showError("Failed to update book.");
            }
        }
    }

    private void deleteSelectedBook(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a book to delete."); return;
        }
        int bookId   = (int) table.getValueAt(row, 0);
        String title = (String) table.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete:\n\"" + title + "\"?\n" +
            "This cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(bookId)) {
                showSuccess("Book deleted successfully!");
                refreshBooks();
            } else {
                showError("Failed to delete book.");
            }
        }
    }

    // ── MEMBERS PANEL ─────────────────────────────────────────────────────

    private JPanel buildMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("👤  Member Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 140));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchBar.setBackground(Color.WHITE);
        JTextField memberSearch = new JTextField(20);
        memberSearch.setFont(new Font("SansSerif", Font.PLAIN, 13));
        memberSearch.setPreferredSize(new Dimension(200, 30));
        memberSearch.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        JButton memberSearchBtn = styledButton("🔍 Search",
            new Color(30, 80, 140), null);
        JButton memberClearBtn  = styledButton("✕ Clear",
            new Color(120, 120, 120), null);
        searchBar.add(new JLabel("Search: "));
        searchBar.add(memberSearch);
        searchBar.add(memberSearchBtn);
        searchBar.add(memberClearBtn);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(searchBar, BorderLayout.CENTER);
        panel.add(topSection, BorderLayout.NORTH);

        // Table
        String[] cols = {"ID", "Name", "Email", "Membership Type"};
        memberTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(memberTableModel);
        styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnBar.setBackground(Color.WHITE);
        btnBar.add(styledButton("➕ Add Member",
            new Color(46, 139, 87),   e -> showAddMemberDialog()));
        btnBar.add(styledButton("✏ Update",
            new Color(70, 130, 180),  e -> showEditMemberDialog(table)));
        btnBar.add(styledButton("🗑 Delete",
            new Color(178, 34, 34),   e -> deleteSelectedMember(table)));
        btnBar.add(styledButton("🔄 Refresh",
            new Color(100, 100, 100), e -> refreshMembers()));
        panel.add(btnBar, BorderLayout.SOUTH);

        // Search actions
        memberSearchBtn.addActionListener(e -> {
            String kw = memberSearch.getText().trim();
            if (kw.isEmpty()) { refreshMembers(); return; }
            List<Member> results = memberDAO.searchMembers(kw);
            memberTableModel.setRowCount(0);
            for (Member m : results)
                memberTableModel.addRow(new Object[]{
                    m.getMemberId(), m.getMemberName(),
                    m.getEmail(), m.getMembershipType()});
            setStatus("Found " + results.size() + " member(s).");
        });
        memberSearch.addActionListener(e -> memberSearchBtn.doClick());
        memberClearBtn.addActionListener(e -> {
            memberSearch.setText("");
            refreshMembers();
        });

        return panel;
    }

    private void refreshMembers() {
        setStatus("Loading members...");
        executor.submit(() -> {
            List<Member> members = memberDAO.getAllMembers();
            SwingUtilities.invokeLater(() -> {
                memberTableModel.setRowCount(0);
                for (Member m : members)
                    memberTableModel.addRow(new Object[]{
                        m.getMemberId(), m.getMemberName(),
                        m.getEmail(), m.getMembershipType()});
                setStatus("Members loaded: " +
                    members.size() + " record(s).");
            });
        });
    }

    private void showAddMemberDialog() {
        JTextField nameF  = new JTextField(20);
        JTextField emailF = new JTextField(20);
        JComboBox<String> typeBox = new JComboBox<>(
            new String[]{"Student", "Staff"});

        JPanel form = buildFormPanel(
            "Name:",            nameF,
            "Email:",           emailF,
            "Membership Type:", typeBox
        );

        int result = JOptionPane.showConfirmDialog(this, form,
            "Add New Member", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name  = nameF.getText().trim();
            String email = emailF.getText().trim();
            String type  = (String) typeBox.getSelectedItem();

            if (!Validator.isNotEmpty(name)) {
                showError("Name cannot be empty."); return;
            }
            if (!Validator.isValidEmail(email)) {
                showError("Invalid email format.\n" +
                    "Example: john@stmarys.ac.uk"); return;
            }
            if (memberDAO.emailExists(email)) {
                showError("A member with this email already exists."); return;
            }

            if (memberDAO.addMember(new Member(name, email, type))) {
                showSuccess("Member registered successfully!");
                refreshMembers();
            } else {
                showError("Failed to register member.");
            }
        }
    }

    private void showEditMemberDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a member to update."); return;
        }
        int memberId = (int) table.getValueAt(row, 0);
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) { showError("Member not found."); return; }

        JTextField nameF  = new JTextField(member.getMemberName(), 20);
        JTextField emailF = new JTextField(member.getEmail(), 20);
        JComboBox<String> typeBox = new JComboBox<>(
            new String[]{"Student", "Staff"});
        typeBox.setSelectedItem(member.getMembershipType());

        JPanel form = buildFormPanel(
            "Name:",            nameF,
            "Email:",           emailF,
            "Membership Type:", typeBox
        );

        int result = JOptionPane.showConfirmDialog(this, form,
            "Update Member #" + memberId, JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name  = nameF.getText().trim();
            String email = emailF.getText().trim();

            if (!Validator.isNotEmpty(name)) {
                showError("Name cannot be empty."); return;
            }
            if (!Validator.isValidEmail(email)) {
                showError("Invalid email format."); return;
            }

            member.setMemberName(name);
            member.setEmail(email);
            member.setMembershipType((String) typeBox.getSelectedItem());

            if (memberDAO.updateMember(member)) {
                showSuccess("Member updated successfully!");
                refreshMembers();
            } else {
                showError("Failed to update member.");
            }
        }
    }

    private void deleteSelectedMember(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a member to delete."); return;
        }
        int memberId = (int) table.getValueAt(row, 0);
        String name  = (String) table.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete member:\n\"" +
            name + "\"?\nThis cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (memberDAO.deleteMember(memberId)) {
                showSuccess("Member deleted successfully!");
                refreshMembers();
            } else {
                showError("Failed to delete member.");
            }
        }
    }

    // ── BORROW PANEL ──────────────────────────────────────────────────────

    private JPanel buildBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("📋  Borrow Records Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 140));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        // Table with fine and return date columns
        String[] cols = {"Record ID", "Book ID", "Member ID",
                         "Borrow Date", "Due Date", "Status",
                         "Return Date", "Fine (£)"};
        borrowTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(borrowTableModel);
        styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // Buttons
        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnBar.setBackground(Color.WHITE);
        btnBar.add(styledButton("➕ Add Record",
            new Color(46, 139, 87),   e -> showAddBorrowDialog()));
        btnBar.add(styledButton("📤 Return Book",
            new Color(70, 130, 180),  e -> showUpdateStatusDialog(table)));
        btnBar.add(styledButton("🗑 Delete",
            new Color(178, 34, 34),   e -> deleteSelectedRecord(table)));
        btnBar.add(styledButton("⚠ Overdue",
            new Color(200, 120, 0),   e -> showOverdueRecords()));
        btnBar.add(styledButton("🔄 Refresh",
            new Color(100, 100, 100), e -> refreshBorrows()));
        panel.add(btnBar, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshBorrows() {
        setStatus("Loading borrow records...");
        executor.submit(() -> {
            List<BorrowRecord> records = borrowDAO.getAllRecords();
            SwingUtilities.invokeLater(() -> {
                borrowTableModel.setRowCount(0);
                for (BorrowRecord r : records)
                    borrowTableModel.addRow(new Object[]{
                        r.getRecordId(),
                        r.getBookId(),
                        r.getMemberId(),
                        r.getBorrowDate(),
                        r.getDueDate(),
                        r.getReturnStatus(),
                        r.getReturnDate().isEmpty() ? "—" : r.getReturnDate(),
                        r.getFine() > 0
                            ? String.format("£%.2f", r.getFine()) : "—"
                    });
                setStatus("Records loaded: " +
                    records.size() + " record(s).");
            });
        });
    }

    private void showAddBorrowDialog() {
        JTextField bookIdF   = new JTextField(10);
        JTextField memberIdF = new JTextField(10);
        JTextField borrowF   = new JTextField(
            java.time.LocalDate.now().toString(), 10);
        JTextField dueF      = new JTextField(
            java.time.LocalDate.now().plusDays(14).toString(), 10);

        JPanel form = buildFormPanel(
            "Book ID:",                  bookIdF,
            "Member ID:",                memberIdF,
            "Borrow Date (yyyy-MM-dd):", borrowF,
            "Due Date (yyyy-MM-dd):",    dueF
        );

        int result = JOptionPane.showConfirmDialog(this, form,
            "Add Borrow Record", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String bid    = bookIdF.getText().trim();
            String mid    = memberIdF.getText().trim();
            String borrow = borrowF.getText().trim();
            String due    = dueF.getText().trim();

            if (!Validator.isPositiveInteger(bid)) {
                showError("Invalid Book ID. Must be a positive number.");
                return;
            }
            if (!Validator.isPositiveInteger(mid)) {
                showError("Invalid Member ID. Must be a positive number.");
                return;
            }
            if (!Validator.isValidDate(borrow)) {
                showError("Invalid borrow date.\nUse format: yyyy-MM-dd");
                return;
            }
            if (!Validator.isValidDate(due)) {
                showError("Invalid due date.\nUse format: yyyy-MM-dd");
                return;
            }
            if (!Validator.isDueDateAfterBorrow(borrow, due)) {
                showError("Due date must be after borrow date."); return;
            }

            int bookId   = Integer.parseInt(bid);
            int memberId = Integer.parseInt(mid);

            if (bookDAO.getBookById(bookId) == null) {
                showError("No book found with ID: " + bookId +
                    "\nPlease check the Book ID."); return;
            }
            if (memberDAO.getMemberById(memberId) == null) {
                showError("No member found with ID: " + memberId +
                    "\nPlease check the Member ID."); return;
            }
            if (borrowDAO.isBookBorrowed(bookId)) {
                showError("Book #" + bookId +
                    " is already borrowed.\nPlease choose another book.");
                return;
            }

            BorrowRecord rec = new BorrowRecord(
                bookId, memberId, borrow, due, "Borrowed");

            if (borrowDAO.addRecord(rec)) {
                showSuccess("Borrow record created successfully!");
                refreshBorrows();
                refreshBooks();
            } else {
                showError("Failed to create borrow record.\n" +
                    "Please try again.");
            }
        }
    }

    private void showUpdateStatusDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a record to update."); return;
        }

        int recordId         = (int) table.getValueAt(row, 0);
        String currentStatus = (String) table.getValueAt(row, 5);

        if (currentStatus.equals("Returned")) {
            showError("This book has already been returned."); return;
        }

        String[] options = {"Return Book", "Mark as Overdue", "Cancel"};
        int choice = JOptionPane.showOptionDialog(this,
            "What would you like to do with Record #" + recordId + "?",
            "Update Record",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null, options, options[0]);

        if (choice == 0) {
            BorrowRecord rec = borrowDAO.getRecordById(recordId);
            if (rec != null) {
                double fine = Validator.calculateFine(rec.getDueDate());
                String msg  = "Confirm return for Record #" + recordId;
                if (fine > 0) {
                    msg += "\n\n⚠ Overdue fine: £" +
                        String.format("%.2f", fine) +
                        "\nThis will be charged to the member.";
                } else {
                    msg += "\n\n✓ No fine — returned on time!";
                }
                int confirm = JOptionPane.showConfirmDialog(this,
                    msg, "Confirm Return", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    if (borrowDAO.returnBook(recordId)) {
                        if (fine > 0) {
                            showSuccess("Book returned!\n" +
                                "Overdue fine charged: £" +
                                String.format("%.2f", fine));
                        } else {
                            showSuccess(
                                "Book returned successfully! No fine.");
                        }
                        refreshBorrows();
                        refreshBooks();
                    } else {
                        showError("Failed to return book.");
                    }
                }
            }
        } else if (choice == 1) {
            if (borrowDAO.updateStatus(recordId, "Overdue")) {
                showSuccess("Record #" + recordId +
                    " marked as Overdue.");
                refreshBorrows();
            } else {
                showError("Failed to update status.");
            }
        }
    }

    private void deleteSelectedRecord(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) {
            showError("Please select a record to delete."); return;
        }
        int recordId = (int) table.getValueAt(row, 0);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete Record #" +
            recordId + "?\nThis cannot be undone.",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            if (borrowDAO.deleteRecord(recordId)) {
                showSuccess("Record deleted successfully!");
                refreshBorrows();
            } else {
                showError("Failed to delete record.");
            }
        }
    }

    private void showOverdueRecords() {
        List<BorrowRecord> overdue = borrowDAO.getOverdueRecords();
        if (overdue.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "✓ No overdue books found!",
                "Overdue Books",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] cols = {"Record ID", "Book ID", "Member ID",
                         "Borrow Date", "Due Date", "Status", "Fine (£)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (BorrowRecord r : overdue)
            model.addRow(new Object[]{
                r.getRecordId(), r.getBookId(), r.getMemberId(),
                r.getBorrowDate(), r.getDueDate(), r.getReturnStatus(),
                r.getFine() > 0
                    ? String.format("£%.2f", r.getFine()) : "—"
            });
        JTable t = new JTable(model);
        styleTable(t);
        t.setPreferredScrollableViewportSize(new Dimension(700, 200));
        JOptionPane.showMessageDialog(this,
            new JScrollPane(t),
            "⚠ Overdue Records (" + overdue.size() + ")",
            JOptionPane.WARNING_MESSAGE);
    }

    // ── SEARCH PANEL ──────────────────────────────────────────────────────

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("🔍  Search Records");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 140));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(Color.WHITE);

        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            BorderFactory.createEmptyBorder(3, 8, 3, 8)));

        JComboBox<String> typeBox = new JComboBox<>(new String[]{
            "Books (title/author/ID/category)",
            "Books by Category",
            "Members (name/email/ID)",
            "Records by Member ID",
            "Records by Book ID",
            "Records by Date Range"
        });

        JButton searchBtn = styledButton("🔍 Search",
            new Color(30, 80, 140), null);
        JButton clearBtn  = styledButton("✕ Clear",
            new Color(120, 120, 120), null);

        topBar.add(new JLabel("Search: "));
        topBar.add(searchField);
        topBar.add(new JLabel("  In: "));
        topBar.add(typeBox);
        topBar.add(searchBtn);
        topBar.add(clearBtn);

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(Color.WHITE);
        topSection.add(title, BorderLayout.NORTH);
        topSection.add(topBar, BorderLayout.CENTER);
        panel.add(topSection, BorderLayout.NORTH);

        DefaultTableModel resultModel = new DefaultTableModel();
        JTable resultTable = new JTable(resultModel);
        styleTable(resultTable);
        panel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        JLabel countLabel = new JLabel("  Results: 0");
        countLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        panel.add(countLabel, BorderLayout.SOUTH);

        Runnable doSearch = () -> {
            String kw = searchField.getText().trim();
            resultModel.setRowCount(0);
            resultModel.setColumnCount(0);
            int idx = typeBox.getSelectedIndex();

            if (idx == 0) {
                resultModel.setColumnIdentifiers(new Object[]{
                    "ID","Title","Author","Category","Status"});
                for (Book b : bookDAO.searchBooks(kw))
                    resultModel.addRow(new Object[]{
                        b.getBookId(), b.getTitle(), b.getAuthor(),
                        b.getCategory(), b.getAvailabilityStatus()});
            } else if (idx == 1) {
                resultModel.setColumnIdentifiers(new Object[]{
                    "ID","Title","Author","Category","Status"});
                for (Book b : bookDAO.filterByCategory(kw))
                    resultModel.addRow(new Object[]{
                        b.getBookId(), b.getTitle(), b.getAuthor(),
                        b.getCategory(), b.getAvailabilityStatus()});
            } else if (idx == 2) {
                resultModel.setColumnIdentifiers(new Object[]{
                    "ID","Name","Email","Type"});
                for (Member m : memberDAO.searchMembers(kw))
                    resultModel.addRow(new Object[]{
                        m.getMemberId(), m.getMemberName(),
                        m.getEmail(), m.getMembershipType()});
            } else if (idx == 3) {
                resultModel.setColumnIdentifiers(new Object[]{
                    "Record ID","Book ID","Member ID",
                    "Borrow Date","Due Date","Status","Fine"});
                try {
                    for (BorrowRecord r :
                        borrowDAO.getRecordsByMember(Integer.parseInt(kw)))
                        resultModel.addRow(new Object[]{
                            r.getRecordId(), r.getBookId(), r.getMemberId(),
                            r.getBorrowDate(), r.getDueDate(),
                            r.getReturnStatus(),
                            r.getFine() > 0
                                ? String.format("£%.2f", r.getFine()) : "—"});
                } catch (NumberFormatException ex) {
                    showError("Please enter a numeric Member ID.");
                }
            } else if (idx == 4) {
                resultModel.setColumnIdentifiers(new Object[]{
                    "Record ID","Book ID","Member ID",
                    "Borrow Date","Due Date","Status","Fine"});
                try {
                    for (BorrowRecord r :
                        borrowDAO.getRecordsByBook(Integer.parseInt(kw)))
                        resultModel.addRow(new Object[]{
                            r.getRecordId(), r.getBookId(), r.getMemberId(),
                            r.getBorrowDate(), r.getDueDate(),
                            r.getReturnStatus(),
                            r.getFine() > 0
                                ? String.format("£%.2f", r.getFine()) : "—"});
                } catch (NumberFormatException ex) {
                    showError("Please enter a numeric Book ID.");
                }
            } else if (idx == 5) {
                String from = JOptionPane.showInputDialog(this,
                    "From date (yyyy-MM-dd):");
                String to   = JOptionPane.showInputDialog(this,
                    "To date (yyyy-MM-dd):");
                if (from != null && to != null &&
                    Validator.isValidDate(from) &&
                    Validator.isValidDate(to)) {
                    resultModel.setColumnIdentifiers(new Object[]{
                        "Record ID","Book ID","Member ID",
                        "Borrow Date","Due Date","Status","Fine"});
                    for (BorrowRecord r :
                        borrowDAO.filterByDateRange(from, to))
                        resultModel.addRow(new Object[]{
                            r.getRecordId(), r.getBookId(), r.getMemberId(),
                            r.getBorrowDate(), r.getDueDate(),
                            r.getReturnStatus(),
                            r.getFine() > 0
                                ? String.format("£%.2f", r.getFine()) : "—"});
                } else {
                    showError("Invalid date format. Use yyyy-MM-dd");
                }
            }
            countLabel.setText("  Results: " + resultModel.getRowCount());
        };

        searchBtn.addActionListener(e -> doSearch.run());
        searchField.addActionListener(e -> doSearch.run());
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            resultModel.setRowCount(0);
            resultModel.setColumnCount(0);
            countLabel.setText("  Results: 0");
        });

        return panel;
    }

    // HELPERS 

    private void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setAutoCreateRowSorter(true);
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setGridColor(new Color(220, 220, 220));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(
            new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(30, 80, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel buildFormPanel(Object... items) {
        JPanel p = new JPanel(
            new GridLayout(items.length / 2, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        for (Object item : items) {
            if (item instanceof String s) {
                JLabel lbl = new JLabel(s);
                lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
                p.add(lbl);
            } else {
                p.add((Component) item);
            }
        }
        return p;
    }

    private JButton styledButton(String text, Color bg,
                                  ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        if (action != null) btn.addActionListener(action);
        return btn;
    }

    private void showSuccess(String msg) {
        setStatus("✓ " + msg);
        JOptionPane.showMessageDialog(this, msg,
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        setStatus("✗ " + msg);
        JOptionPane.showMessageDialog(this, msg,
            "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void setStatus(String msg) {
        SwingUtilities.invokeLater(() -> statusBar.setText("  " + msg));
    }

    @Override
    public void dispose() {
        executor.shutdown();
        super.dispose();
    }
}