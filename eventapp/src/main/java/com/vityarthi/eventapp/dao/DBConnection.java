package com.vityarthi.eventapp.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

// Small helper class that handles the SQLite connection
// and makes sure the tables exist before the app starts using them
public class DBConnection {

    private static final String DB_URL = "jdbc:sqlite:events.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC driver not found: " + e.getMessage());
        }
    }

    // Open a new SQLite connection with foreign keys enabled
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(DB_URL);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        }
        return conn;
    }

    // Creates the events and tickets tables if they don't already exist
    // Called once when the app starts up
    public static void createTables() {
        String eventsTable = "CREATE TABLE IF NOT EXISTS events ("
                + "event_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "title TEXT NOT NULL,"
                + "venue TEXT,"
                + "event_date TEXT,"
                + "capacity INTEGER NOT NULL"
                + ")";

        String ticketsTable = "CREATE TABLE IF NOT EXISTS tickets ("
                + "ticket_code TEXT PRIMARY KEY,"
                + "event_id INTEGER NOT NULL,"
                + "attendee_name TEXT NOT NULL,"
                + "attendee_email TEXT NOT NULL,"
                + "status TEXT NOT NULL,"
                + "FOREIGN KEY(event_id) REFERENCES events(event_id)"
                + ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(eventsTable);
            stmt.execute(ticketsTable);

        } catch (SQLException e) {
            System.out.println("Error setting up database tables: " + e.getMessage());
        }
    }
}
