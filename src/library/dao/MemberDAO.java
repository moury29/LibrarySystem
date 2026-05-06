package library.dao;

import library.model.Member;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO {

    public boolean addMember(Member member) {
        String sql = "INSERT INTO members (member_name, email, membership_type) VALUES (?, ?, ?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, member.getMemberName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getMembershipType());
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error adding member: " + e.getMessage());
            return false;
        }
    }

    public List<Member> getAllMembers() {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members ORDER BY member_id";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) members.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error fetching members: " + e.getMessage());
        }
        return members;
    }

    public Member getMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE member_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) {
            System.err.println("Error finding member: " + e.getMessage());
        }
        return null;
    }

    public List<Member> searchMembers(String keyword) {
        List<Member> members = new ArrayList<>();
        String sql = "SELECT * FROM members WHERE LOWER(member_name) LIKE ? OR CAST(member_id AS TEXT) = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ps.setString(2, keyword);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) members.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("Error searching members: " + e.getMessage());
        }
        return members;
    }

    public boolean updateMember(Member member) {
        String sql = "UPDATE members SET member_name=?, email=?, membership_type=? WHERE member_id=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, member.getMemberName());
            ps.setString(2, member.getEmail());
            ps.setString(3, member.getMembershipType());
            ps.setInt(4, member.getMemberId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteMember(int memberId) {
        String sql = "DELETE FROM members WHERE member_id = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, memberId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
            return false;
        }
    }

    private Member mapRow(ResultSet rs) throws SQLException {
        return new Member(
            rs.getInt("member_id"),
            rs.getString("member_name"),
            rs.getString("email"),
            rs.getString("membership_type")
        );
    }
}