package library.dao;

import library.model.BorrowRecord;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BorrowRecordDAO {

    public boolean addRecord(BorrowRecord record) {
        String sql = "INSERT INTO borrow_records (book_id, member_id, borrow_date, due_date, return_status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, record.getBookId());
            ps.setInt(2, record.getMemberId());
            ps.setString(3, record.getBorrowDate());
            ps.setString(4, record.getDueDate());
            ps.setString(5, record.getReturnStatus());
            ps.executeUpdate();
            String updateBook = "UPDATE books SET availability_status='Borrowed' WHERE book_id=?";
            try (PreparedStatement ps2 = DatabaseConnection.getConnection().prepareStatement(updateBook)) {
                ps2.setInt(1, record.getBookId());
                ps2.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding record: " + e.getMessage());
            return false;
        }
    }

    public List<BorrowRecord> getAllRecords() {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records ORDER BY record_id";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) records.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error fetching records: " + e.getMessage());
        }
        return records;
    }

    public BorrowRecord getRecordById(int recordId) {
        String sql = "SELECT * FROM borrow_records WHERE record_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Error finding record: " + e.getMessage());
        }
        return null;
    }

    public List<BorrowRecord> getRecordsByMember(int memberId) {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE member_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) records.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error fetching by member: " + e.getMessage());
        }
        return records;
    }

    public List<BorrowRecord> getRecordsByBook(int bookId) {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE book_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) records.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error fetching by book: " + e.getMessage());
        }
        return records;
    }

    public List<BorrowRecord> getOverdueRecords() {
        List<BorrowRecord> records = new ArrayList<>();
        String today = LocalDate.now().toString();
        String sql = "SELECT * FROM borrow_records WHERE due_date < ? AND return_status != 'Returned'";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, today);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) records.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error fetching overdue: " + e.getMessage());
        }
        return records;
    }

    public List<BorrowRecord> filterByDateRange(String fromDate, String toDate) {
        List<BorrowRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrow_records WHERE borrow_date >= ? AND borrow_date <= ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, fromDate);
            ps.setString(2, toDate);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) records.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error filtering by date: " + e.getMessage());
        }
        return records;
    }

    public boolean updateStatus(int recordId, String newStatus) {
        String sql = "UPDATE borrow_records SET return_status=? WHERE record_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, recordId);
            boolean updated = ps.executeUpdate() > 0;
            if (updated && newStatus.equals("Returned")) {
                BorrowRecord r = getRecordById(recordId);
                if (r != null) {
                    String updateBook = "UPDATE books SET availability_status='Available' WHERE book_id=?";
                    try (PreparedStatement ps2 = DatabaseConnection.getConnection().prepareStatement(updateBook)) {
                        ps2.setInt(1, r.getBookId());
                        ps2.executeUpdate();
                    }
                }
            }
            return updated;
        } catch (SQLException e) {
            System.err.println("Error updating status: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteRecord(int recordId) {
        String sql = "DELETE FROM borrow_records WHERE record_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, recordId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting record: " + e.getMessage());
            return false;
        }
    }

    private BorrowRecord mapRow(ResultSet rs) throws SQLException {
        return new BorrowRecord(
            rs.getInt("record_id"),
            rs.getInt("book_id"),
            rs.getInt("member_id"),
            rs.getString("borrow_date"),
            rs.getString("due_date"),
            rs.getString("return_status")
        );
    }
}