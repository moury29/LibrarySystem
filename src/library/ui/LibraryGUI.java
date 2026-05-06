package library.ui;

import library.dao.*;
import library.model.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LibraryGUI extends JFrame {

    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowRecordDAO borrowDAO = new BorrowRecordDAO();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private DefaultTableModel bookTableModel;
    private DefaultTableModel memberTableModel;
    private DefaultTableModel borrowTableModel;
    private JLabel statusBar;

    public LibraryGUI() {
        setTitle("St Mary's Digital Library System");
        setSize(1100, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel header = new JLabel("  St Mary's Digital Library System", SwingConstants.LEFT);
        header.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.setOpaque(true);
        header.setBackground(new Color(30, 80, 140));
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 50));
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tabs.addTab("Books", buildBooksPanel());
        tabs.addTab("Members", buildMembersPanel());
        tabs.addTab("Borrow Records", buildBorrowPanel());
        tabs.addTab("Search", buildSearchPanel());
        add(tabs, BorderLayout.CENTER);

        statusBar = new JLabel("  Ready");
        statusBar.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setPreferredSize(new Dimension(0, 25));
        add(statusBar, BorderLayout.SOUTH);

        refreshBooks();
        refreshMembers();
        refreshBorrows();
    }

    // ── BOOKS ──────────────────────────────────────────────────────────────

    private JPanel buildBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Title", "Author", "Category", "Status"};
        bookTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(bookTableModel);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JButton addBtn    = styledButton("Add Book",    new Color(46, 139, 87));
        JButton editBtn   = styledButton("Update Book", new Color(70, 130, 180));
        JButton deleteBtn = styledButton("Delete Book", new Color(178, 34, 34));
        JButton refreshBtn= styledButton("Refresh",     new Color(100, 100, 100));
        btnBar.add(addBtn); btnBar.add(editBtn); btnBar.add(deleteBtn); btnBar.add(refreshBtn);
        panel.add(btnBar, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> showAddBookDialog());
        editBtn.addActionListener(e -> showEditBookDialog(table));
        deleteBtn.addActionListener(e -> deleteSelectedBook(table));
        refreshBtn.addActionListener(e -> refreshBooks());
        return panel;
    }

    private void refreshBooks() {
        setStatus("Loading books...");
        executor.submit(() -> {
            List<Book> books = bookDAO.getAllBooks();
            SwingUtilities.invokeLater(() -> {
                bookTableModel.setRowCount(0);
                for (Book b : books)
                    bookTableModel.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(), b.getAvailabilityStatus()});
                setStatus("Books loaded: " + books.size());
            });
        });
    }

    private void showAddBookDialog() {
        JTextField titleF = new JTextField(20), authorF = new JTextField(20), categoryF = new JTextField(20);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available", "Borrowed", "Reserved"});
        JPanel form = buildFormPanel("Title:", titleF, "Author:", authorF, "Category:", categoryF, "Status:", statusBox);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Book", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String title = titleF.getText().trim(), author = authorF.getText().trim(), cat = categoryF.getText().trim();
            if (!Validator.isNotEmpty(title) || !Validator.isNotEmpty(author) || !Validator.isNotEmpty(cat)) {
                showError("Title, Author and Category cannot be empty."); return;
            }
            if (bookDAO.addBook(new Book(title, author, cat, (String) statusBox.getSelectedItem()))) {
                showSuccess("Book added successfully!"); refreshBooks();
            } else showError("Failed to add book.");
        }
    }

    private void showEditBookDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a book to update."); return; }
        int bookId = (int) table.getValueAt(row, 0);
        Book book = bookDAO.getBookById(bookId);
        if (book == null) return;

        JTextField titleF = new JTextField(book.getTitle(), 20);
        JTextField authorF = new JTextField(book.getAuthor(), 20);
        JTextField categoryF = new JTextField(book.getCategory(), 20);
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Available", "Borrowed", "Reserved"});
        statusBox.setSelectedItem(book.getAvailabilityStatus());
        JPanel form = buildFormPanel("Title:", titleF, "Author:", authorF, "Category:", categoryF, "Status:", statusBox);

        if (JOptionPane.showConfirmDialog(this, form, "Update Book #" + bookId, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
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
        int bookId = (int) table.getValueAt(row, 0);
        String title = (String) table.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Delete \"" + title + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (bookDAO.deleteBook(bookId)) { showSuccess("Book deleted successfully!"); refreshBooks(); }
            else showError("Failed to delete book.");
        }
    }

    // ── MEMBERS ────────────────────────────────────────────────────────────

    private JPanel buildMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Name", "Email", "Membership Type"};
        memberTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(memberTableModel);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JButton addBtn    = styledButton("Add Member",    new Color(46, 139, 87));
        JButton editBtn   = styledButton("Update Member", new Color(70, 130, 180));
        JButton deleteBtn = styledButton("Delete Member", new Color(178, 34, 34));
        JButton refreshBtn= styledButton("Refresh",       new Color(100, 100, 100));
        btnBar.add(addBtn); btnBar.add(editBtn); btnBar.add(deleteBtn); btnBar.add(refreshBtn);
        panel.add(btnBar, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> showAddMemberDialog());
        editBtn.addActionListener(e -> showEditMemberDialog(table));
        deleteBtn.addActionListener(e -> deleteSelectedMember(table));
        refreshBtn.addActionListener(e -> refreshMembers());
        return panel;
    }

    private void refreshMembers() {
        setStatus("Loading members...");
        executor.submit(() -> {
            List<Member> members = memberDAO.getAllMembers();
            SwingUtilities.invokeLater(() -> {
                memberTableModel.setRowCount(0);
                for (Member m : members)
                    memberTableModel.addRow(new Object[]{m.getMemberId(), m.getMemberName(), m.getEmail(), m.getMembershipType()});
                setStatus("Members loaded: " + members.size());
            });
        });
    }

    private void showAddMemberDialog() {
        JTextField nameF = new JTextField(20), emailF = new JTextField(20);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Student", "Staff"});
        JPanel form = buildFormPanel("Name:", nameF, "Email:", emailF, "Type:", typeBox);

        if (JOptionPane.showConfirmDialog(this, form, "Add New Member", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String name = nameF.getText().trim(), email = emailF.getText().trim();
            if (!Validator.isNotEmpty(name)) { showError("Name cannot be empty."); return; }
            if (!Validator.isValidEmail(email)) { showError("Invalid email format."); return; }
            if (memberDAO.addMember(new Member(name, email, (String) typeBox.getSelectedItem()))) {
                showSuccess("Member registered successfully!"); refreshMembers();
            } else showError("Failed to register member.");
        }
    }

    private void showEditMemberDialog(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a member to update."); return; }
        int memberId = (int) table.getValueAt(row, 0);
        Member member = memberDAO.getMemberById(memberId);
        if (member == null) return;

        JTextField nameF = new JTextField(member.getMemberName(), 20);
        JTextField emailF = new JTextField(member.getEmail(), 20);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{"Student", "Staff"});
        typeBox.setSelectedItem(member.getMembershipType());
        JPanel form = buildFormPanel("Name:", nameF, "Email:", emailF, "Type:", typeBox);

        if (JOptionPane.showConfirmDialog(this, form, "Update Member #" + memberId, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String email = emailF.getText().trim();
            if (!Validator.isValidEmail(email)) { showError("Invalid email."); return; }
            member.setMemberName(nameF.getText().trim());
            member.setEmail(email);
            member.setMembershipType((String) typeBox.getSelectedItem());
            if (memberDAO.updateMember(member)) { showSuccess("Member updated successfully!"); refreshMembers(); }
            else showError("Failed to update member.");
        }
    }

    private void deleteSelectedMember(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a member to delete."); return; }
        int memberId = (int) table.getValueAt(row, 0);
        String name = (String) table.getValueAt(row, 1);
        if (JOptionPane.showConfirmDialog(this, "Delete \"" + name + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (memberDAO.deleteMember(memberId)) { showSuccess("Member deleted successfully!"); refreshMembers(); }
            else showError("Failed to delete member.");
        }
    }

    // ── BORROW RECORDS ─────────────────────────────────────────────────────

    private JPanel buildBorrowPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Record ID", "Book ID", "Member ID", "Borrow Date", "Due Date", "Status"};
        borrowTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(borrowTableModel);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 13));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        JButton addBtn     = styledButton("Add Record",    new Color(46, 139, 87));
        JButton updateBtn  = styledButton("Update Status", new Color(70, 130, 180));
        JButton deleteBtn  = styledButton("Delete Record", new Color(178, 34, 34));
        JButton overdueBtn = styledButton("Show Overdue",  new Color(200, 120, 0));
        JButton refreshBtn = styledButton("Refresh",       new Color(100, 100, 100));
        btnBar.add(addBtn); btnBar.add(updateBtn); btnBar.add(deleteBtn); btnBar.add(overdueBtn); btnBar.add(refreshBtn);
        panel.add(btnBar, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> showAddBorrowDialog());
        updateBtn.addActionListener(e -> showUpdateStatusDialog(table));
        deleteBtn.addActionListener(e -> deleteSelectedRecord(table));
        overdueBtn.addActionListener(e -> showOverdueRecords());
        refreshBtn.addActionListener(e -> refreshBorrows());
        return panel;
    }

    private void refreshBorrows() {
        setStatus("Loading borrow records...");
        executor.submit(() -> {
            List<BorrowRecord> records = borrowDAO.getAllRecords();
            SwingUtilities.invokeLater(() -> {
                borrowTableModel.setRowCount(0);
                for (BorrowRecord r : records)
                    borrowTableModel.addRow(new Object[]{r.getRecordId(), r.getBookId(), r.getMemberId(), r.getBorrowDate(), r.getDueDate(), r.getReturnStatus()});
                setStatus("Records loaded: " + records.size());
            });
        });
    }

    private void showAddBorrowDialog() {
        JTextField bookIdF = new JTextField(10), memberIdF = new JTextField(10);
        JTextField borrowF = new JTextField("2026-01-01", 10), dueF = new JTextField("2026-01-15", 10);
        JPanel form = buildFormPanel("Book ID:", bookIdF, "Member ID:", memberIdF, "Borrow Date (yyyy-MM-dd):", borrowF, "Due Date (yyyy-MM-dd):", dueF);

        if (JOptionPane.showConfirmDialog(this, form, "Add Borrow Record", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            String bid = bookIdF.getText().trim(), mid = memberIdF.getText().trim();
            String borrow = borrowF.getText().trim(), due = dueF.getText().trim();
            if (!Validator.isPositiveInteger(bid)) { showError("Invalid Book ID."); return; }
            if (!Validator.isPositiveInteger(mid)) { showError("Invalid Member ID."); return; }
            if (!Validator.isValidDate(borrow))    { showError("Invalid borrow date."); return; }
            if (!Validator.isValidDate(due) || !Validator.isDueDateAfterBorrow(borrow, due)) { showError("Invalid due date."); return; }
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
        JComboBox<String> statusBox = new JComboBox<>(new String[]{"Borrowed", "Returned", "Overdue"});

        if (JOptionPane.showConfirmDialog(this, new Object[]{"New Status:", statusBox}, "Update Record #" + recordId, JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if (borrowDAO.updateStatus(recordId, (String) statusBox.getSelectedItem())) {
                showSuccess("Borrow record updated successfully!"); refreshBorrows(); refreshBooks();
            } else showError("Failed to update.");
        }
    }

    private void deleteSelectedRecord(JTable table) {
        int row = table.getSelectedRow();
        if (row < 0) { showError("Please select a record to delete."); return; }
        int recordId = (int) table.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete record #" + recordId + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            if (borrowDAO.deleteRecord(recordId)) { showSuccess("Record deleted successfully!"); refreshBorrows(); }
            else showError("Failed to delete record.");
        }
    }

    private void showOverdueRecords() {
        List<BorrowRecord> overdue = borrowDAO.getOverdueRecords();
        if (overdue.isEmpty()) { JOptionPane.showMessageDialog(this, "No overdue books!", "Overdue", JOptionPane.INFORMATION_MESSAGE); return; }
        String[] cols = {"Record ID", "Book ID", "Member ID", "Borrow Date", "Due Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (BorrowRecord r : overdue)
            model.addRow(new Object[]{r.getRecordId(), r.getBookId(), r.getMemberId(), r.getBorrowDate(), r.getDueDate(), r.getReturnStatus()});
        JTable t = new JTable(model);
        t.setRowHeight(24);
        JOptionPane.showMessageDialog(this, new JScrollPane(t), "Overdue Records (" + overdue.size() + ")", JOptionPane.WARNING_MESSAGE);
    }

    // ── SEARCH ─────────────────────────────────────────────────────────────

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JComboBox<String> typeBox = new JComboBox<>(new String[]{
            "Books (title/author/ID)", "Books by Category",
            "Members (name/ID)", "Records by Member ID", "Records by Book ID"
        });
        JButton searchBtn = styledButton("Search", new Color(30, 80, 140));
        topBar.add(new JLabel("Search: ")); topBar.add(searchField);
        topBar.add(new JLabel("  In: ")); topBar.add(typeBox); topBar.add(searchBtn);
        panel.add(topBar, BorderLayout.NORTH);

        DefaultTableModel resultModel = new DefaultTableModel();
        JTable resultTable = new JTable(resultModel);
        resultTable.setRowHeight(24);
        resultTable.setAutoCreateRowSorter(true);
        panel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        JLabel countLabel = new JLabel("  Results: 0");
        panel.add(countLabel, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> {
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
                } catch (NumberFormatException ex) { showError("Enter a numeric Member ID."); }
            } else {
                resultModel.setColumnIdentifiers(new Object[]{"Record ID","Book ID","Member ID","Borrow Date","Due Date","Status"});
                try { for (BorrowRecord r : borrowDAO.getRecordsByBook(Integer.parseInt(kw)))
                    resultModel.addRow(new Object[]{r.getRecordId(), r.getBookId(), r.getMemberId(), r.getBorrowDate(), r.getDueDate(), r.getReturnStatus()});
                } catch (NumberFormatException ex) { showError("Enter a numeric Book ID."); }
            }
            countLabel.setText("  Results: " + resultModel.getRowCount());
        });
        return panel;
    }

    // ── HELPERS ────────────────────────────────────────────────────────────

    private JPanel buildFormPanel(Object... items) {
        JPanel p = new JPanel(new GridLayout(items.length / 2, 2, 6, 6));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        for (Object item : items) {
            if (item instanceof String s) p.add(new JLabel(s));
            else p.add((Component) item);
        }
        return p;
    }

    private JButton styledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        return btn;
    }

    private void showSuccess(String msg) { setStatus("✓ " + msg); JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE); }
    private void showError(String msg)   { setStatus("✗ " + msg); JOptionPane.showMessageDialog(this, msg, "Error",   JOptionPane.ERROR_MESSAGE); }
    private void setStatus(String msg)   { SwingUtilities.invokeLater(() -> statusBar.setText("  " + msg)); }

    @Override
    public void dispose() { executor.shutdown(); DatabaseConnection.closeConnection(); super.dispose(); }
}