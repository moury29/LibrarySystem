package library;

import java.sql.SQLException;
import javax.swing.SwingUtilities;
import library.dao.BorrowRecordDAO;
import library.dao.DatabaseConnection;
import library.ui.LoginGUI;

public class Main {
    public static void main(String[] args) {
        try {
            // Connect to database first
            DatabaseConnection.getConnection();
            System.out.println("[Main] Database ready.");

            // Auto update overdue after connection is established
            SwingUtilities.invokeLater(() -> {
                try {
                    new BorrowRecordDAO().autoUpdateOverdue();
                } catch (Exception e) {
                    System.err.println("[Main] Overdue update error: "
                        + e.getMessage());
                }
                new LoginGUI().setVisible(true);
            });

        } catch (SQLException e) {
            System.err.println("[Main] Cannot connect: " + e.getMessage());
            System.exit(1);
        }
    }
}