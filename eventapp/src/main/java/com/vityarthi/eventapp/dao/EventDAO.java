package com.vityarthi.eventapp.dao;

import com.vityarthi.eventapp.model.Event;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Handles all the DB operations for events
public class EventDAO {

    // Insert a new event into the DB, returns true if successful
    public boolean saveEvent(Event event) {
        String sql = "INSERT INTO events (title, venue, event_date, capacity) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, event.getTitle());
            ps.setString(2, event.getVenue());
            ps.setString(3, event.getEventDate());
            ps.setInt(4, event.getCapacity());
            ps.executeUpdate();

            // grab the auto-generated id so the caller has it too
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    event.setEventId(keys.getInt(1));
                }
            }
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving event: " + e.getMessage());
            return false;
        }
    }

    // Return every event currently in the table, sorted by status (Ongoing, Upcoming, Completed)
    public List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT * FROM events";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Event e = new Event(
                        rs.getInt("event_id"),
                        rs.getString("title"),
                        rs.getString("venue"),
                        rs.getString("event_date"),
                        rs.getInt("capacity")
                );
                events.add(e);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching events: " + e.getMessage());
        }

        // Sort: Ongoing (today) first, then Upcoming (future), then Completed (past)
        events.sort((a, b) -> {
            int rankA = a.getStatus().equals("Ongoing") ? 1 : a.getStatus().equals("Upcoming") ? 2 : 3;
            int rankB = b.getStatus().equals("Ongoing") ? 1 : b.getStatus().equals("Upcoming") ? 2 : 3;
            if (rankA != rankB) {
                return Integer.compare(rankA, rankB);
            }
            return a.getEventDate().compareTo(b.getEventDate());
        });

        return events;
    }

    // Look up one event by its id, returns null if nothing found
    public Event getEventById(int eventId) {
        String sql = "SELECT * FROM events WHERE event_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Event(
                            rs.getInt("event_id"),
                            rs.getString("title"),
                            rs.getString("venue"),
                            rs.getString("event_date"),
                            rs.getInt("capacity")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching event: " + e.getMessage());
        }

        return null;
    }

    // Returns how many tickets have been registered for this event
    public int getRegisteredCount(int eventId) {
        String sql = "SELECT COUNT(*) FROM tickets WHERE event_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error counting tickets: " + e.getMessage());
        }

        return 0;
    }

    // Delete an event and all tickets linked to it
    public boolean deleteEvent(int eventId) {
        String deleteTickets = "DELETE FROM tickets WHERE event_id = ?";
        String deleteEvent = "DELETE FROM events WHERE event_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psTickets = conn.prepareStatement(deleteTickets);
                 PreparedStatement psEvent = conn.prepareStatement(deleteEvent)) {

                psTickets.setInt(1, eventId);
                psTickets.executeUpdate();

                psEvent.setInt(1, eventId);
                int rows = psEvent.executeUpdate();

                conn.commit();
                return rows > 0;
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Error deleting event: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.out.println("Database error during event deletion: " + e.getMessage());
            return false;
        }
    }
}
