package library.ui;

import java.util.List;
import java.util.Scanner;
import library.dao.*;
import library.model.*;

public class ConsoleUI {

    private final Scanner scanner = new Scanner(System.in);
    private final BookDAO bookDAO = new BookDAO();
    private final MemberDAO memberDAO = new MemberDAO();
    private final BorrowRecordDAO borrowDAO = new BorrowRecordDAO();

    public void start() {
        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║   St Mary's Digital Library Management System    ║");
        System.out.println("╚══════════════════════════════════════════════════╝");

        boolean running = true;
        while (running) {
            System.out.println("\n┌─── MAIN MENU ──────────────────────────────────┐");
            System.out.println("│  1. Manage Books                                │");
            System.out.println("│  2. Manage Members                              │");
            System.out.println("│  3. Manage Borrowing Records                    │");
            System.out.println("│  4. Search Records                              │");
            System.out.println("│  5. Exit System                                 │");
            System.out.println("└─────────────────────────────────────────────────┘");
            System.out.print("Enter choice: ");

            switch (scanner.nextLine().trim()) {
                case "1" -> booksMenu();
                case "2" -> membersMenu();
                case "3" -> borrowMenu();
                case "4" -> searchMenu();
                case "5" -> { running = false; System.out.println("Goodbye!"); }
                default  -> System.out.println("[!] Invalid option. Enter 1-5.");
            }
        }
    }

    //  BOOKS

    private void booksMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── BOOK MANAGEMENT ───────────────────────────────");
            System.out.println("  1. View All Books");
            System.out.println("  2. Add New Book");
            System.out.println("  3. Update Book");
            System.out.println("  4. Delete Book");
            System.out.println("  5. Back");
            System.out.print("Enter choice: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> listBooks(bookDAO.getAllBooks());
                case "2" -> addBook();
                case "3" -> updateBook();
                case "4" -> deleteBook();
                case "5" -> back = true;
                default  -> System.out.println("[!] Invalid option.");
            }
        }
    }

    private void listBooks(List<Book> books) {
        if (books.isEmpty()) { System.out.println("[i] No books found."); return; }
        System.out.println("\nID    | Title                               | Author               | Category             | Status");
        System.out.println("─".repeat(100));
        books.forEach(System.out::println);
        System.out.println("Total: " + books.size() + " book(s).");
    }

    private void addBook() {
        System.out.println("\n── Add New Book ──");
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        if (!Validator.isNotEmpty(title)) { System.out.println("[!] Title cannot be empty."); return; }

        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        if (!Validator.isNotEmpty(author)) { System.out.println("[!] Author cannot be empty."); return; }

        System.out.print("Category: ");
        String category = scanner.nextLine().trim();
        if (!Validator.isNotEmpty(category)) { System.out.println("[!] Category cannot be empty."); return; }

        System.out.print("Status (Available/Borrowed/Reserved): ");
        String status = scanner.nextLine().trim();
        if (!Validator.isValidAvailabilityStatus(status)) { System.out.println("[!] Invalid status."); return; }

        if (bookDAO.addBook(new Book(title, author, category, status)))
            System.out.println("[✓] Book added successfully!");
        else
            System.out.println("[✗] Failed to add book.");
    }

    private void updateBook() {
        System.out.println("\n── Update Book ──");
        System.out.print("Enter Book ID: ");
        String idStr = scanner.nextLine().trim();
        if (!Validator.isPositiveInteger(idStr)) { System.out.println("[!] Invalid ID."); return; }
        int id = Integer.parseInt(idStr);
        Book b = bookDAO.getBookById(id);
        if (b == null) { System.out.println("[!] Book not found."); return; }

        System.out.println("Current: " + b);
        System.out.print("New Title (Enter to keep): "); String t = scanner.nextLine().trim();
        System.out.print("New Author (Enter to keep): "); String a = scanner.nextLine().trim();
        System.out.print("New Category (Enter to keep): "); String c = scanner.nextLine().trim();
        System.out.print("New Status (Enter to keep): "); String s = scanner.nextLine().trim();

        if (!t.isEmpty()) b.setTitle(t);
        if (!a.isEmpty()) b.setAuthor(a);
        if (!c.isEmpty()) b.setCategory(c);
        if (!s.isEmpty() && Validator.isValidAvailabilityStatus(s)) b.setAvailabilityStatus(s);

        if (bookDAO.updateBook(b)) System.out.println("[✓] Book updated successfully!");
        else System.out.println("[✗] Failed to update.");
    }

    private void deleteBook() {
        System.out.println("\n── Delete Book ──");
        System.out.print("Enter Book ID: ");
        String idStr = scanner.nextLine().trim();
        if (!Validator.isPositiveInteger(idStr)) { System.out.println("[!] Invalid ID."); return; }
        int id = Integer.parseInt(idStr);
        Book b = bookDAO.getBookById(id);
        if (b == null) { System.out.println("[!] Book not found."); return; }
        System.out.println("Book: " + b);
        System.out.print("Confirm delete? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) { System.out.println("[i] Cancelled."); return; }
        if (bookDAO.deleteBook(id)) System.out.println("[✓] Book deleted successfully!");
        else System.out.println("[✗] Failed to delete.");
    }

    // MEMBERS 

    private void membersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── MEMBER MANAGEMENT ─────────────────────────────");
            System.out.println("  1. View All Members");
            System.out.println("  2. Add New Member");
            System.out.println("  3. Update Member");
            System.out.println("  4. Delete Member");
            System.out.println("  5. Back");
            System.out.print("Enter choice: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> listMembers(memberDAO.getAllMembers());
                case "2" -> addMember();
                case "3" -> updateMember();
                case "4" -> deleteMember();
                case "5" -> back = true;
                default  -> System.out.println("[!] Invalid option.");
            }
        }
    }

    private void listMembers(List<Member> members) {
        if (members.isEmpty()) { System.out.println("[i] No members found."); return; }
        System.out.println("\nID    | Name                      | Email                               | Type");
        System.out.println("─".repeat(90));
        members.forEach(System.out::println);
        System.out.println("Total: " + members.size() + " member(s).");
    }

    private void addMember() {
        System.out.println("\n── Add New Member ──");
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        if (!Validator.isNotEmpty(name)) { System.out.println("[!] Name cannot be empty."); return; }

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (!Validator.isValidEmail(email)) { System.out.println("[!] Invalid email."); return; }

        System.out.print("Membership Type (Student/Staff): ");
        String type = scanner.nextLine().trim();
        if (!Validator.isValidMembershipType(type)) { System.out.println("[!] Must be Student or Staff."); return; }

        if (memberDAO.addMember(new Member(name, email, type)))
            System.out.println("[✓] Member registered successfully!");
        else
            System.out.println("[✗] Failed to register.");
    }

    private void updateMember() {
        System.out.println("\n── Update Member ──");
        System.out.print("Enter Member ID: ");
        String idStr = scanner.nextLine().trim();
        if (!Validator.isPositiveInteger(idStr)) { System.out.println("[!] Invalid ID."); return; }
        int id = Integer.parseInt(idStr);
        Member m = memberDAO.getMemberById(id);
        if (m == null) { System.out.println("[!] Member not found."); return; }

        System.out.println("Current: " + m);
        System.out.print("New Name (Enter to keep): "); String n = scanner.nextLine().trim();
        System.out.print("New Email (Enter to keep): "); String e = scanner.nextLine().trim();
        System.out.print("New Type (Enter to keep): "); String t = scanner.nextLine().trim();

        if (!n.isEmpty()) m.setMemberName(n);
        if (!e.isEmpty() && Validator.isValidEmail(e)) m.setEmail(e);
        if (!t.isEmpty() && Validator.isValidMembershipType(t)) m.setMembershipType(t);

        if (memberDAO.updateMember(m)) System.out.println("[✓] Member updated successfully!");
        else System.out.println("[✗] Failed to update.");
    }

    private void deleteMember() {
        System.out.println("\n── Delete Member ──");
        System.out.print("Enter Member ID: ");
        String idStr = scanner.nextLine().trim();
        if (!Validator.isPositiveInteger(idStr)) { System.out.println("[!] Invalid ID."); return; }
        int id = Integer.parseInt(idStr);
        Member m = memberDAO.getMemberById(id);
        if (m == null) { System.out.println("[!] Member not found."); return; }
        System.out.println("Member: " + m);
        System.out.print("Confirm delete? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) { System.out.println("[i] Cancelled."); return; }
        if (memberDAO.deleteMember(id)) System.out.println("[✓] Member deleted successfully!");
        else System.out.println("[✗] Failed to delete.");
    }

    // BORROWING RECORDS 

    private void borrowMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── BORROWING MANAGEMENT ──────────────────────────");
            System.out.println("  1. View All Records");
            System.out.println("  2. Add Borrow Record");
            System.out.println("  3. Update Return Status");
            System.out.println("  4. Delete Record");
            System.out.println("  5. Show Overdue Books");
            System.out.println("  6. Back");
            System.out.print("Enter choice: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> listRecords(borrowDAO.getAllRecords());
                case "2" -> addBorrowRecord();
                case "3" -> updateBorrowStatus();
                case "4" -> deleteBorrowRecord();
                case "5" -> showOverdue();
                case "6" -> back = true;
                default  -> System.out.println("[!] Invalid option.");
            }
        }
    }

    private void listRecords(List<BorrowRecord> records) {
        if (records.isEmpty()) { System.out.println("[i] No records found."); return; }
        System.out.println("\nID    | Book ID  | Member ID | Borrowed       | Due            | Status");
        System.out.println("─".repeat(80));
        records.forEach(System.out::println);
        System.out.println("Total: " + records.size() + " record(s).");
    }

    private void addBorrowRecord() {
        System.out.println("\n── Add Borrow Record ──");
        System.out.print("Book ID: ");
        String bid = scanner.nextLine().trim();
        if (!Validator.isPositiveInteger(bid) || bookDAO.getBookById(Integer.parseInt(bid)) == null) {
            System.out.println("[!] Invalid or non-existent Book ID."); return;
        }
        System.out.print("Member ID: ");
        String mid = scanner.nextLine().trim();
        if (!Validator.isPositiveInteger(mid) || memberDAO.getMemberById(Integer.parseInt(mid)) == null) {
            System.out.println("[!] Invalid or non-existent Member ID."); return;
        }
        System.out.print("Borrow Date (yyyy-MM-dd): ");
        String borrow = scanner.nextLine().trim();
        if (!Validator.isValidDate(borrow)) { System.out.println("[!] Invalid date."); return; }
        System.out.print("Due Date (yyyy-MM-dd): ");
        String due = scanner.nextLine().trim();
        if (!Validator.isValidDate(due) || !Validator.isDueDateAfterBorrow(borrow, due)) {
            System.out.println("[!] Invalid due date."); return;
        }
        if (borrowDAO.addRecord(new BorrowRecord(Integer.parseInt(bid), Integer.parseInt(mid), borrow, due, "Borrowed")))
            System.out.println("[✓] Borrow record created successfully!");
        else
            System.out.println("[✗] Failed to create record.");
    }

    private void updateBorrowStatus() {
        System.out.println("\n── Update Borrow Status ──");
        System.out.print("Record ID: ");
        String idStr = scanner.nextLine().trim();
        if (!Validator.isPositiveInteger(idStr) || borrowDAO.getRecordById(Integer.parseInt(idStr)) == null) {
            System.out.println("[!] Record not found."); return;
        }
        System.out.print("New Status (Borrowed/Returned/Overdue): ");
        String status = scanner.nextLine().trim();
        if (!Validator.isValidReturnStatus(status)) { System.out.println("[!] Invalid status."); return; }
        if (borrowDAO.updateStatus(Integer.parseInt(idStr), status))
            System.out.println("[✓] Borrow record updated successfully!");
        else
            System.out.println("[✗] Failed to update.");
    }

    private void deleteBorrowRecord() {
        System.out.println("\n── Delete Borrow Record ──");
        System.out.print("Record ID: ");
        String idStr = scanner.nextLine().trim();
        if (!Validator.isPositiveInteger(idStr)) { System.out.println("[!] Invalid ID."); return; }
        int id = Integer.parseInt(idStr);
        BorrowRecord r = borrowDAO.getRecordById(id);
        if (r == null) { System.out.println("[!] Record not found."); return; }
        System.out.println("Record: " + r);
        System.out.print("Confirm delete? (yes/no): ");
        if (!scanner.nextLine().trim().equalsIgnoreCase("yes")) { System.out.println("[i] Cancelled."); return; }
        if (borrowDAO.deleteRecord(id)) System.out.println("[✓] Record deleted successfully!");
        else System.out.println("[✗] Failed to delete.");
    }

    private void showOverdue() {
        List<BorrowRecord> overdue = borrowDAO.getOverdueRecords();
        if (overdue.isEmpty()) System.out.println("[✓] No overdue books.");
        else { System.out.println("[!] " + overdue.size() + " overdue record(s):"); listRecords(overdue); }
    }

    //  SEARCH 

    private void searchMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n── SEARCH ─────────────────────────────────────────");
            System.out.println("  1. Search Books (title/author/ID)");
            System.out.println("  2. Filter Books by Category");
            System.out.println("  3. Search Members (name/ID)");
            System.out.println("  4. Records by Member ID");
            System.out.println("  5. Records by Book ID");
            System.out.println("  6. Records by Date Range");
            System.out.println("  7. Back");
            System.out.print("Enter choice: ");
            switch (scanner.nextLine().trim()) {
                case "1" -> { System.out.print("Keyword: "); listBooks(bookDAO.searchBooks(scanner.nextLine().trim())); }
                case "2" -> { System.out.print("Category: "); listBooks(bookDAO.filterByCategory(scanner.nextLine().trim())); }
                case "3" -> { System.out.print("Keyword: "); listMembers(memberDAO.searchMembers(scanner.nextLine().trim())); }
                case "4" -> { System.out.print("Member ID: "); String id = scanner.nextLine().trim(); if (Validator.isPositiveInteger(id)) listRecords(borrowDAO.getRecordsByMember(Integer.parseInt(id))); }
                case "5" -> { System.out.print("Book ID: "); String id = scanner.nextLine().trim(); if (Validator.isPositiveInteger(id)) listRecords(borrowDAO.getRecordsByBook(Integer.parseInt(id))); }
                case "6" -> { System.out.print("From (yyyy-MM-dd): "); String from = scanner.nextLine().trim(); System.out.print("To (yyyy-MM-dd): "); String to = scanner.nextLine().trim(); if (Validator.isValidDate(from) && Validator.isValidDate(to)) listRecords(borrowDAO.filterByDateRange(from, to)); }
                case "7" -> back = true;
                default  -> System.out.println("[!] Invalid option.");
            }
        }
    }
}