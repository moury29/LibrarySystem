package library.model;

public class Book {
    private int bookId;
    private String title;
    private String author;
    private String category;
    private String availabilityStatus;

    public Book(String title, String author, String category, String availabilityStatus) {
        this.title = title;
        this.author = author;
        this.category = category;
        this.availabilityStatus = availabilityStatus;
    }

    public Book(int bookId, String title, String author, String category, String availabilityStatus) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.availabilityStatus = availabilityStatus;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getAvailabilityStatus() { return availabilityStatus; }

    public void setBookId(int bookId) { this.bookId = bookId; }
    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setCategory(String category) { this.category = category; }
    public void setAvailabilityStatus(String status) { this.availabilityStatus = status; }

    @Override
    public String toString() {
        return String.format("%-5d | %-35s | %-20s | %-20s | %s",
                bookId, title, author, category, availabilityStatus);
    }
}