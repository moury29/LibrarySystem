package library.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import library.model.Book;

public class BookDAO {

    public boolean addBook(Book book) {
        String sql = "INSERT INTO books (title, author, category, availability_status) VALUES (?, ?, ?, ?)";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setString(4, book.getAvailabilityStatus());
            int rows = ps.executeUpdate();
            ps.close();
            System.out.println("[BookDAO] Rows inserted: " + rows);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] Error adding book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books ORDER BY book_id";
        try {
            Connection conn = DatabaseConnection.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) books.add(mapRow(rs));
            stmt.close();
        } catch (SQLException e) {
            System.err.println("[BookDAO] Error fetching books: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Book b = mapRow(rs);
                ps.close();
                return b;
            }
            ps.close();
        } catch (SQLException e) {
            System.err.println("[BookDAO] Error finding book: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Book> searchBooks(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(title) LIKE ? OR LOWER(author) LIKE ? OR LOWER(category) LIKE ? OR CAST(book_id AS TEXT) = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            String kw = "%" + keyword.toLowerCase() + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            ps.setString(3, kw);
            ps.setString(4, keyword);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
            ps.close();
        } catch (SQLException e) {
            System.err.println("[BookDAO] Error searching books: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> filterByCategory(String category) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE LOWER(category) LIKE ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + category.toLowerCase() + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) books.add(mapRow(rs));
            ps.close();
        } catch (SQLException e) {
            System.err.println("[BookDAO] Error filtering: " + e.getMessage());
            e.printStackTrace();
        }
        return books;
    }

    public boolean updateBook(Book book) {
        String sql = "UPDATE books SET title=?, author=?, category=?, availability_status=? WHERE book_id=?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCategory());
            ps.setString(4, book.getAvailabilityStatus());
            ps.setInt(5, book.getBookId());
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] Error updating book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBook(int bookId) {
        String sql = "DELETE FROM books WHERE book_id = ?";
        try {
            Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, bookId);
            int rows = ps.executeUpdate();
            ps.close();
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("[BookDAO] Error deleting book: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
            rs.getInt("book_id"),
            rs.getString("title"),
            rs.getString("author"),
            rs.getString("category"),
            rs.getString("availability_status")
        );
    }
}