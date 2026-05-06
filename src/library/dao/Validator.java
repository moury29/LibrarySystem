package library.dao;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

    private static final DateTimeFormatter DATE_FORMAT =
        DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    public static boolean isValidDate(String date) {
        if (!isNotEmpty(date)) return false;
        try {
            LocalDate.parse(date, DATE_FORMAT);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isDueDateAfterBorrow(String borrowDate, String dueDate) {
        try {
            LocalDate borrow = LocalDate.parse(borrowDate, DATE_FORMAT);
            LocalDate due = LocalDate.parse(dueDate, DATE_FORMAT);
            return due.isAfter(borrow);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isPositiveInteger(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isValidMembershipType(String type) {
        return type != null &&
               (type.equalsIgnoreCase("Student") ||
                type.equalsIgnoreCase("Staff"));
    }

    public static boolean isValidAvailabilityStatus(String status) {
        return status != null && (
            status.equalsIgnoreCase("Available") ||
            status.equalsIgnoreCase("Borrowed")  ||
            status.equalsIgnoreCase("Reserved")
        );
    }

    public static boolean isValidReturnStatus(String status) {
        return status != null && (
            status.equalsIgnoreCase("Borrowed") ||
            status.equalsIgnoreCase("Returned") ||
            status.equalsIgnoreCase("Overdue")
        );
    }

    // Calculate fine — £0.50 per day overdue
    public static double calculateFine(String dueDate) {
        try {
            LocalDate due = LocalDate.parse(dueDate, DATE_FORMAT);
            LocalDate today = LocalDate.now();
            if (today.isAfter(due)) {
                long daysOverdue = java.time.temporal.ChronoUnit.DAYS.between(due, today);
                return daysOverdue * 0.50;
            }
        } catch (DateTimeParseException e) {
            return 0.0;
        }
        return 0.0;
    }
}