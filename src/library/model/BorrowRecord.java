package library.model;

public class BorrowRecord {
    private int recordId;
    private int bookId;
    private int memberId;
    private String borrowDate;
    private String dueDate;
    private String returnStatus;
    private String returnDate;
    private double fine;

    public BorrowRecord(int bookId, int memberId, String borrowDate,
                        String dueDate, String returnStatus) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnStatus = returnStatus;
        this.returnDate = "";
        this.fine = 0.0;
    }

    public BorrowRecord(int recordId, int bookId, int memberId, String borrowDate,
                        String dueDate, String returnStatus, String returnDate, double fine) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnStatus = returnStatus;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    public int getRecordId() { return recordId; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }
    public String getBorrowDate() { return borrowDate; }
    public String getDueDate() { return dueDate; }
    public String getReturnStatus() { return returnStatus; }
    public String getReturnDate() { return returnDate; }
    public double getFine() { return fine; }

    public void setRecordId(int recordId) { this.recordId = recordId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setReturnStatus(String returnStatus) { this.returnStatus = returnStatus; }
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public void setFine(double fine) { this.fine = fine; }

    @Override
    public String toString() {
        return String.format("%-5d | Book:%-4d | Member:%-4d | %s | Due:%s | %s | Fine:£%.2f",
                recordId, bookId, memberId, borrowDate, dueDate, returnStatus, fine);
    }
}