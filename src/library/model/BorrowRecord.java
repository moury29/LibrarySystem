package library.model;

public class BorrowRecord {
    private int recordId;
    private int bookId;
    private int memberId;
    private String borrowDate;
    private String dueDate;
    private String returnStatus;

    public BorrowRecord(int bookId, int memberId, String borrowDate, String dueDate, String returnStatus) {
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnStatus = returnStatus;
    }

    public BorrowRecord(int recordId, int bookId, int memberId, String borrowDate, String dueDate, String returnStatus) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnStatus = returnStatus;
    }

    public int getRecordId() { return recordId; }
    public int getBookId() { return bookId; }
    public int getMemberId() { return memberId; }
    public String getBorrowDate() { return borrowDate; }
    public String getDueDate() { return dueDate; }
    public String getReturnStatus() { return returnStatus; }

    public void setRecordId(int recordId) { this.recordId = recordId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setMemberId(int memberId) { this.memberId = memberId; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public void setReturnStatus(String returnStatus) { this.returnStatus = returnStatus; }

    @Override
    public String toString() {
        return String.format("%-5d | Book:%-4d | Member:%-4d | Borrowed:%-12s | Due:%-12s | %s",
                recordId, bookId, memberId, borrowDate, dueDate, returnStatus);
    }
}