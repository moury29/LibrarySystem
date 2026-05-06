package library;

import java.sql.SQLException;
import javax.swing.SwingUtilities;
import library.dao.DatabaseConnection;
import library.ui.LoginGUI;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Cannot connect to database: " + e.getMessage());
            System.exit(1);
        }

        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}