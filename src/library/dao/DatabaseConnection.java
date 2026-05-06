package library.dao;

import java.sql.*;

public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:database/library.db";
    private static Connection connection = null;

    private DatabaseConnection() {}

    public static synchronized Connection getConnection() throws SQLException {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("org.sqlite.JDBC");
                connection = DriverManager.getConnection(DB_URL);
                // These settings prevent database locked errors
                connection.setAutoCommit(true);
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("PRAGMA journal_mode=WAL");
                    stmt.execute("PRAGMA busy_timeout=10000");
                    stmt.execute("PRAGMA synchronous=NORMAL");
                }
                initializeDatabase(connection);
                System.out.println("[DB] Connected successfully.");
            }
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite JDBC driver not found.", e);
        }
        return connection;
    }

    private static void initializeDatabase(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {

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
                    email TEXT NOT NULL UNIQUE,
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
                    return_status TEXT NOT NULL,
                    return_date DATE,
                    fine REAL DEFAULT 0.0
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL
                )
            """);

            // Default admin user
            ResultSet rsU = stmt.executeQuery(
                "SELECT COUNT(*) AS cnt FROM users");
            if (rsU.next() && rsU.getInt("cnt") == 0) {
                stmt.execute(
                    "INSERT INTO users (username, password) " +
                    "VALUES ('admin', 'library26')");
            }

            // Sample books
            ResultSet rs = stmt.executeQuery(
                "SELECT COUNT(*) AS cnt FROM books");
            if (rs.next() && rs.getInt("cnt") == 0) {
                stmt.execute("""
                    INSERT INTO books
                        (title, author, category, availability_status)
                    VALUES
                    ('Introduction to Java','John Smith','Programming','Available'),
                    ('Database Systems','Maria Garcia','Computer Science','Available'),
                    ('Software Engineering Principles','Alan Brown','Engineering','Available'),
                    ('Clean Code','Robert C. Martin','Programming','Available'),
                    ('The Pragmatic Programmer','David Thomas','Programming','Available'),
                    ('Design Patterns','Gang of Four','Software Engineering','Available'),
                    ('Artificial Intelligence','Stuart Russell','Computer Science','Available'),
                    ('Computer Networks','Andrew Tanenbaum','Networking','Available')
                """);
            }

            // Sample members
            ResultSet rs2 = stmt.executeQuery(
                "SELECT COUNT(*) AS cnt FROM members");
            if (rs2.next() && rs2.getInt("cnt") == 0) {
                stmt.execute("""
                    INSERT INTO members
                        (member_name, email, membership_type)
                    VALUES
                    ('Alice Johnson','alice.johnson@stmarys.ac.uk','Student'),
                    ('Michael Lee','michael.lee@stmarys.ac.uk','Staff'),
                    ('Sara Ahmed','sara.ahmed@stmarys.ac.uk','Student'),
                    ('James Wilson','james.wilson@stmarys.ac.uk','Student'),
                    ('Emma Davis','emma.davis@stmarys.ac.uk','Staff'),
                    ('Oliver Brown','oliver.brown@stmarys.ac.uk','Student')
                """);
            }

            // Sample borrow records
            ResultSet rs3 = stmt.executeQuery(
                "SELECT COUNT(*) AS cnt FROM borrow_records");
            if (rs3.next() && rs3.getInt("cnt") == 0) {
                stmt.execute("""
                    INSERT INTO borrow_records
                        (book_id, member_id, borrow_date, due_date,
                         return_status, return_date, fine)
                    VALUES
                    (1, 1, '2025-03-01', '2025-03-15', 'Borrowed', '', 0.0),
                    (2, 2, '2025-03-02', '2025-03-16', 'Returned', '2025-03-14', 0.0),
                    (3, 3, '2025-03-05', '2025-03-19', 'Borrowed', '', 0.0)
                """);
            }
        }
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing: " + e.getMessage());
        }
    }
}