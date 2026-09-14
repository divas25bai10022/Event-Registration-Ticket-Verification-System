package com.vityarthi.eventapp.dao;

import com.vityarthi.eventapp.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

// Handles all the DB operations for tickets
public class TicketDAO {

    // Insert a new ticket row
    public boolean saveTicket(Ticket ticket) {
        String sql = "INSERT INTO tickets (ticket_code, event_id, attendee_name, attendee_email, status) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticket.getTicketCode());
            ps.setInt(2, ticket.getEventId());
            ps.setString(3, ticket.getAttendeeName());
            ps.setString(4, ticket.getAttendeeEmail());
            ps.setString(5, ticket.getStatus());
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.out.println("Error saving ticket: " + e.getMessage());
            return false;
        }
    }

    // Look up a ticket by its code (case-insensitive)
    public Ticket getTicket(String ticketCode) {
        if (ticketCode == null) return null;
        String sql = "SELECT * FROM tickets WHERE UPPER(ticket_code) = UPPER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticketCode.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Ticket(
                            rs.getString("ticket_code"),
                            rs.getInt("event_id"),
                            rs.getString("attendee_name"),
                            rs.getString("attendee_email"),
                            rs.getString("status")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Error fetching ticket: " + e.getMessage());
        }

        return null;
    }

    // Flip ticket status to USED if it is currently VALID
    public boolean markTicketAsUsed(String ticketCode) {
        if (ticketCode == null) return false;
        String sql = "UPDATE tickets SET status = 'USED' WHERE UPPER(ticket_code) = UPPER(?) AND status = 'VALID'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ticketCode.trim());
            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;

        } catch (SQLException e) {
            System.out.println("Error updating ticket status: " + e.getMessage());
            return false;
        }
    }

    // Count how many tickets have been registered for a given event
    public int getTicketCountForEvent(int eventId) {
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
            System.out.println("Error counting tickets for event: " + e.getMessage());
        }

        return 0;
    }

    // Check if an email is already registered for this event
    public boolean isEmailRegistered(int eventId, String email) {
        if (email == null) return false;
        String sql = "SELECT COUNT(*) FROM tickets WHERE event_id = ? AND LOWER(attendee_email) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, eventId);
            ps.setString(2, email.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error checking attendee email: " + e.getMessage());
        }

        return false;
    }

    // Return all tickets, newest first
    public List<Ticket> getAllTickets() {
        List<Ticket> tickets = new ArrayList<>();
        String sql = "SELECT * FROM tickets ORDER BY rowid DESC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                tickets.add(new Ticket(
                        rs.getString("ticket_code"),
                        rs.getInt("event_id"),
                        rs.getString("attendee_name"),
                        rs.getString("attendee_email"),
                        rs.getString("status")
                ));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching tickets: " + e.getMessage());
        }

        return tickets;
    }
}
