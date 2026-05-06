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

    // Main content panel that switches views
    private JPanel contentPanel;
    private CardLayout cardLayout;

    // Sidebar buttons
    private JButton activeBtn = null;

    public LibraryGUI() {
        setTitle("St Mary's Digital Library - Management");
        setSize(1100, 700);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        setLayout(new BorderLayout());

        //  HEADER 
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
            public void mouseEntered(MouseEvent e) { backBtn.setBackground(new Color(20, 60, 120)); }
            public void mouseExited(MouseEvent e)  { backBtn.setBackground(new Color(30, 80, 140)); }
        });
        backBtn.addActionListener(e -> {
            executor.shutdown();
            dispose();
            SwingUtilities.invokeLater(() -> new DashboardGUI().setVisible(true));
        });
        leftHeader.add(backBtn);

        JLabel titleLabel = new JLabel("Library Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);

        header.add(leftHeader, BorderLayout.WEST);
        header.add(titleLabel, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        //  BODY (sidebar + content) 
        JPanel body = new JPanel(new BorderLayout());

        //  LEFT SIDEBAR 
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(40, 40, 60));
        sidebar.setPreferredSize(new Dimension(200, 0));

        // Sidebar title
        JLabel menuLabel = new JLabel("  MENU");
        menuLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        menuLabel.setForeground(new Color(150, 150, 180));
        menuLabel.setBorder(BorderFactory.createEmptyBorder(20, 15, 10, 0));
        menuLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(menuLabel);

        // Sidebar buttons
        JButton booksBtn   = sidebarButton("📚  Books");
        JButton membersBtn = sidebarButton("👤  Members");
        JButton borrowBtn  = sidebarButton("📋  Borrow Records");
        JButton searchBtn  = sidebarButton("🔍  Search");

        sidebar.add(booksBtn);
        sidebar.add(membersBtn);
        sidebar.add(borrowBtn);
        sidebar.add(searchBtn);
        sidebar.add(Box.createVerticalGlue());

        //  CONTENT PANEL (CardLayout) 
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

        //  STATUS BAR 
        statusBar = new JLabel("  Ready");
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setPreferredSize(new Dimension(0, 25));
        add(statusBar, BorderLayout.SOUTH);

        // Sidebar button actions
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

    // SIDEBAR BUTTONS

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
                if (btn != activeBtn) btn.setBackground(new Color(60, 60, 90));
            }
            public void mouseExited(MouseEvent e) {
                if (btn != activeBtn) btn.setBackground(new Color(40, 40, 60));
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

    // BOOKS PANEL 

    private JPanel buildBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("📚  Book Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 140));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Title", "Author", "Category", "Status"};
        bookTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(bookTableModel);
        styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnBar.setBackground(Color.WHITE);
        btnBar.add(styledButton("➕ Add Book",    new Color(46, 139, 87),  e -> showAddBookDialog()));
        btnBar.add(styledButton("✏ Update",       new Color(70, 130, 180), e -> showEditBookDialog(table)));
        btnBar.add(styledButton("🗑 Delete",      new Color(178, 34, 34),  e -> deleteSelectedBook(table)));
        btnBar.add(styledButton("🔄 Refresh",     new Color(100, 100, 100),e -> refreshBooks()));
        panel.add(btnBar, BorderLayout.SOUTH);

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
        JTextField titleF = new JTextField(20), authorF = new JTextField(20), categoryF = new JTextField(20);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available","Borrowed","Reserved"});
        JPanel form = buildFormPanel("Title:", titleF, "Author:", authorF, "Category:", categoryF, "Status:", statusBox);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Book", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (!Validator.isNotEmpty(titleF.getText()) || !Validator.isNotEmpty(authorF.getText()) || !Validator.isNotEmpty(categoryF.getText())) {
                showError("All fields are required."); return;
            }
            if (bookDAO.addBook(new Book(titleF.getText().trim(), authorF.getText().trim(), categoryF.getText().trim(), (String)statusBox.getSelectedItem()))) {
                showSuccess("Book added successfully!"); refreshBooks();
            } else showError("Failed to add book.");
        }
    }

    private void showEditBookDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a book to update."); return; }
        Book book = bookDAO.getBookById((int) table.getValueAt(row, 0));
        if (book == null) return;

        JTextField titleF = new JTextField(book.getTitle(), 20);
        JTextField authorF = new JTextField(book.getAuthor(), 20);
        JTextField categoryF = new JTextField(book.getCategory(), 20);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available","Borrowed","Reserved"});
        statusBox.setSelectedItem(book.getAvailabilityStatus());
        JPanel form = buildFormPanel("Title:", titleF, "Author:", authorF, "Category:", categoryF, "Status:", statusBox);

        if (JOptionPane.showConfirmDialog(this, form, "Update Book #" + book.getBookId(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            book.setTitle(titleF.getText().trim());
            book.setAuthor(authorF.getText().trim());
            book.setCategory(categoryF.getText().trim());
            book.setAvailabilityStatus((String) statusBox.getSelectedItem());
            if (bookDAO.updateBook(book)) { showSuccess("Book updated successfully!"); refreshBooks(); }
            else showError("Failed to update book.");
        }
    }

    private void deleteSelectedBook(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a book to delete."); return; }
        int id = (int) table.getValueAt(row, 0);
        String title = (String) table.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Delete \"" + title + "\"?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(id)) { showSuccess("Book deleted successfully!"); refreshBooks(); }
            else showError("Failed to delete.");
        }
    }

    //  MEMBERS PANEL 

    private JPanel buildMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("👤  Member Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 140));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"ID", "Name", "Email", "Membership Type"};
        memberTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(memberTableModel);
        styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnBar.setBackground(Color.WHITE);
        btnBar.add(styledButton("➕ Add Member",  new Color(46, 139, 87),  e -> showAddMemberDialog()));
        btnBar.add(styledButton("✏ Update",       new Color(70, 130, 180), e -> showEditMemberDialog(table)));
        btnBar.add(styledButton("🗑 Delete",      new Color(178, 34, 34),  e -> deleteSelectedMember(table)));
        btnBar.add(styledButton("🔄 Refresh",     new Color(100, 100, 100),e -> refreshMembers()));
        panel.add(btnBar, BorderLayout.SOUTH);

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
                setStatus("Members loaded: " + members.size() + " record(s).");
            });
        });
    }

    private void showAddMemberDialog() {
        JTextField nameF = new JTextField(20), emailF = new JTextField(20);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Student","Staff"});
        JPanel form = buildFormPanel("Name:", nameF, "Email:", emailF, "Membership Type:", typeBox);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Member", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (!Validator.isNotEmpty(nameF.getText())) { showError("Name cannot be empty."); return; }
            if (!Validator.isValidEmail(emailF.getText().trim())) { showError("Invalid email format."); return; }
            if (memberDAO.addMember(new Member(nameF.getText().trim(), emailF.getText().trim(), (String)typeBox.getSelectedItem()))) {
                showSuccess("Member registered successfully!"); refreshMembers();
            } else showError("Failed to register member.");
        }
    }

    private void showEditMemberDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a member to update."); return; }
        Member member = memberDAO.getMemberById((int) table.getValueAt(row, 0));
        if (member == null) return;

        JTextField nameF = new JTextField(member.getMemberName(), 20);
        JTextField emailF = new JTextField(member.getEmail(), 20);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Student","Staff"});
        typeBox.setSelectedItem(member.getMembershipType());
        JPanel form = buildFormPanel("Name:", nameF, "Email:", emailF, "Membership Type:", typeBox);

        if (JOptionPane.showConfirmDialog(this, form, "Update Member #" + member.getMemberId(), JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (!Validator.isValidEmail(emailF.getText().trim())) { showError("Invalid email."); return; }
            member.setMemberName(nameF.getText().trim());
            member.setEmail(emailF.getText().trim());
            member.setMembershipType((String) typeBox.getSelectedItem());
            if (memberDAO.updateMember(member)) { showSuccess("Member updated successfully!"); refreshMembers(); }
            else showError("Failed to update member.");
        }
    }

    private void deleteSelectedMember(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a member to delete."); return; }
        int id = (int) table.getValueAt(row, 0);
        String name = (String) table.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Delete \"" + name + "\"?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (memberDAO.deleteMember(id)) { showSuccess("Member deleted successfully!"); refreshMembers(); }
            else showError("Failed to delete.");
        }
    }

    // BORROW PANEL 

    private JPanel buildBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        panel.setBackground(Color.WHITE);

        JLabel title = new JLabel("📋  Borrow Records Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(new Color(30, 80, 140));
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        String[] cols = {"Record ID", "Book ID", "Member ID", "Borrow Date", "Due Date", "Status"};
        borrowTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(borrowTableModel);
        styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        btnBar.setBackground(Color.WHITE);
        btnBar.add(styledButton("➕ Add Record",   new Color(46, 139, 87),  e -> showAddBorrowDialog()));
        btnBar.add(styledButton("✏ Update Status", new Color(70, 130, 180), e -> showUpdateStatusDialog(table)));
        btnBar.add(styledButton("🗑 Delete",       new Color(178, 34, 34),  e -> deleteSelectedRecord(table)));
        btnBar.add(styledButton("⚠ Overdue",      new Color(200, 120, 0),  e -> showOverdueRecords()));
        btnBar.add(styledButton("🔄 Refresh",      new Color(100, 100, 100),e -> refreshBorrows()));
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
                        r.getRecordId(), r.getBookId(), r.getMemberId(),
                        r.getBorrowDate(), r.getDueDate(), r.getReturnStatus()});
                setStatus("Records loaded: " + records.size() + " record(s).");
            });
        });
    }

    private void showAddBorrowDialog() {
        JTextField bookIdF = new JTextField(10), memberIdF = new JTextField(10);
        JTextField borrowF = new JTextField("2026-01-01", 10), dueF = new JTextField("2026-01-15", 10);
        JPanel form = buildFormPanel(
            "Book ID:", bookIdF, "Member ID:", memberIdF,
            "Borrow Date (yyyy-MM-dd):", borrowF, "Due Date (yyyy-MM-dd):", dueF);

        if (JOptionPane.showConfirmDialog(this, form, "Add Borrow Record", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String bid = bookIdF.getText().trim(), mid = memberIdF.getText().trim();
            String borrow = borrowF.getText().trim(), due = dueF.getText().trim();

            if (!Validator.isPositiveInteger(bid))            { showError("Invalid Book ID."); return; }
            if (!Validator.isPositiveInteger(mid))            { showError("Invalid Member ID."); return; }
            if (!Validator.isValidDate(borrow))               { showError("Invalid borrow date."); return; }
            if (!Validator.isValidDate(due))                  { showError("Invalid due date."); return; }
            if (!Validator.isDueDateAfterBorrow(borrow, due)) { showError("Due date must be after borrow date."); return; }
            if (bookDAO.getBookById(Integer.parseInt(bid)) == null)     { showError("Book ID not found."); return; }
            if (memberDAO.getMemberById(Integer.parseInt(mid)) == null) { showError("Member ID not found."); return; }

            if (borrowDAO.addRecord(new BorrowRecord(Integer.parseInt(bid), Integer.parseInt(mid), borrow, due, "Borrowed"))) {
                showSuccess("Borrow record created successfully!"); refreshBorrows(); refreshBooks();
            } else showError("Failed to create record.");
        }
    }

    private void showUpdateStatusDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a record to update."); return; }
        int recordId = (int) table.getValueAt(row, 0);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Borrowed","Returned","Overdue"});

        if (JOptionPane.showConfirmDialog(this, new Object[]{"New Status:", statusBox},
            "Update Record #" + recordId, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (borrowDAO.updateStatus(recordId, (String) statusBox.getSelectedItem())) {
                showSuccess("Borrow record updated successfully!"); refreshBorrows(); refreshBooks();
            } else showError("Failed to update.");
        }
    }

    private void deleteSelectedRecord(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a record to delete."); return; }
        int id = (int) table.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete record #" + id + "?", "Confirm",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (borrowDAO.deleteRecord(id)) { showSuccess("Record deleted successfully!"); refreshBorrows(); }
            else showError("Failed to delete.");
        }
    }

    private void showOverdueRecords() {
        List<BorrowRecord> overdue = borrowDAO.getOverdueRecords();
        if (overdue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No overdue books!", "Overdue", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        String[] cols = {"Record ID","Book ID","Member ID","Borrow Date","Due Date","Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (BorrowRecord r : overdue)
            model.addRow(new Object[]{r.getRecordId(), r.getBookId(), r.getMemberId(), r.getBorrowDate(), r.getDueDate(), r.getReturnStatus()});
        JTable t = new JTable(model);
        styleTable(t);
        JOptionPane.showMessageDialog(this, new JScrollPane(t), "Overdue Records (" + overdue.size() + ")", JOptionPane.WARNING_MESSAGE);
    }

    // SEARCH PANEL 

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
        JComboBox<String> typeBox = new JComboBox<>(new String[]{
            "Books (title/author/ID)", "Books by Category",
            "Members (name/ID)", "Records by Member ID", "Records by Book ID"
        });
        JButton searchBtn = styledButton("🔍 Search", new Color(30, 80, 140), null);
        topBar.add(new JLabel("Search: ")); topBar.add(searchField);
        topBar.add(new JLabel("  In: ")); topBar.add(typeBox); topBar.add(searchBtn);

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
            resultModel.setRowCount(0); resultModel.setColumnCount(0);
            int idx = typeBox.getSelectedIndex();
            if (idx == 0) {
                resultModel.setColumnIdentifiers(new Object[]{"ID","Title","Author","Category","Status"});
                for (Book b : bookDAO.searchBooks(kw))
                    resultModel.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getAvailabilityStatus()});
            } else if (idx == 1) {
                resultModel.setColumnIdentifiers(new Object[]{"ID","Title","Author","Category","Status"});
                for (Book b : bookDAO.filterByCategory(kw))
                    resultModel.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getAvailabilityStatus()});
            } else if (idx == 2) {
                resultModel.setColumnIdentifiers(new Object[]{"ID","Name","Email","Type"});
                for (Member m : memberDAO.searchMembers(kw))
                    resultModel.addRow(new Object[]{m.getMemberId(), m.getMemberName(), m.getEmail(), m.getMembershipType()});
            } else if (idx == 3) {
                resultModel.setColumnIdentifiers(new Object[]{"Record ID","Book ID","Member ID","Borrow Date","Due Date","Status"});
                try { for (BorrowRecord r : borrowDAO.getRecordsByMember(Integer.parseInt(kw)))
                    resultModel.addRow(new Object[]{r.getRecordId(), r.getBookId(), r.getMemberId(), r.getBorrowDate(), r.getDueDate(), r.getReturnStatus()});
                } catch (NumberFormatException ex) { showError("Enter a numeric ID."); }
            } else {
                resultModel.setColumnIdentifiers(new Object[]{"Record ID","Book ID","Member ID","Borrow Date","Due Date","Status"});
                try { for (BorrowRecord r : borrowDAO.getRecordsByBook(Integer.parseInt(kw)))
                    resultModel.addRow(new Object[]{r.getRecordId(), r.getBookId(), r.getMemberId(), r.getBorrowDate(), r.getDueDate(), r.getReturnStatus()});
                } catch (NumberFormatException ex) { showError("Enter a numeric ID."); }
            }
            countLabel.setText("  Results: " + resultModel.getRowCount());
        };

        searchBtn.addActionListener(e -> doSearch.run());
        searchField.addActionListener(e -> doSearch.run());
        return panel;
    }

    // HELPERS 

    private void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setAutoCreateRowSorter(true);
        table.setSelectionBackground(new Color(184, 207, 229));
        table.setGridColor(new Color(220, 220, 220));
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(30, 80, 140));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private JPanel buildFormPanel(Object... items) {
        JPanel p = new JPanel(new GridLayout(items.length / 2, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        for (Object item : items) {
            if (item instanceof String s) { JLabel l = new JLabel(s); l.setFont(new Font("SansSerif", Font.PLAIN, 13)); p.add(l); }
            else p.add((Component) item);
        }
        return p;
    }

    // Overloaded styledButton with ActionListener
    private JButton styledButton(String text, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(bg.darker()); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(bg); }
        });
        if (action != null) btn.addActionListener(action);
        return btn;
    }

    private void showSuccess(String msg) {
        setStatus("✓ " + msg);
        JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String msg) {
        setStatus("✗ " + msg);
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
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