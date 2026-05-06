package library.dao;

import java.sql.*;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:database/library.db";
    private static Connection connection = null;

    private DatabaseConnection() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);
                initializeDatabase(connection);
            } catch (ClassNotFoundException e) {
                throw new SQLException("SQLite JDBC driver not found.", e);
            }
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS books (
                book_id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                author TEXT NOT NULL,
                category TEXT NOT NULL,
                availability_status TEXT NOT NULL
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS members (
                member_id INTEGER PRIMARY KEY AUTOINCREMENT,
                member_name TEXT NOT NULL,
                email TEXT NOT NULL,
                membership_type TEXT NOT NULL
            )
        """);

        stmt.execute("""
            CREATE TABLE IF NOT EXISTS borrow_records (
                record_id INTEGER PRIMARY KEY AUTOINCREMENT,
                book_id INTEGER NOT NULL,
                member_id INTEGER NOT NULL,
                borrow_date DATE NOT NULL,
                due_date DATE NOT NULL,
                return_status TEXT NOT NULL
            )
        """);

        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) AS cnt FROM books");
        if (rs.next() && rs.getInt("cnt") == 0) {
            stmt.execute("""
                INSERT INTO books (title, author, category, availability_status) VALUES
                ('Introduction to Java', 'John Smith', 'Programming', 'Available'),
                ('Database Systems', 'Maria Garcia', 'Computer Science', 'Borrowed'),
                ('Software Engineering Principles', 'Alan Brown', 'Engineering', 'Available')
            """);
            stmt.execute("""
                INSERT INTO members (member_name, email, membership_type) VALUES
                ('Alice Johnson', 'alice.johnson@stmarys.ac.uk', 'Student'),
                ('Michael Lee', 'michael.lee@stmarys.ac.uk', 'Staff'),
                ('Sara Ahmed', 'sara.ahmed@stmarys.ac.uk', 'Student')
            """);
            stmt.execute("""
                INSERT INTO borrow_records (book_id, member_id, borrow_date, due_date, return_status) VALUES
                (2, 1, '2025-03-01', '2025-03-15', 'Borrowed'),
                (1, 2, '2025-03-02', '2025-03-16', 'Returned'),
                (3, 3, '2025-03-05', '2025-03-19', 'Borrowed')
            """);
        }
        stmt.close();
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}