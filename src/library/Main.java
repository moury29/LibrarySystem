package library;

import library.ui.ConsoleUI;
import library.ui.LibraryGUI;
import library.dao.DatabaseConnection;
import javax.swing.SwingUtilities;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();
        } catch (SQLException e) {
            System.err.println("Cannot connect to database: " + e.getMessage());
            System.exit(1);
        }

        boolean useConsole = args.length > 0 && args[0].equalsIgnoreCase("console");

        if (useConsole) {
            new ConsoleUI().start();
            DatabaseConnection.closeConnection();
        } else {
            SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
        }
    }
}