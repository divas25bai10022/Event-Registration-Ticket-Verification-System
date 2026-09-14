package com.vityarthi.eventapp.service;

import com.vityarthi.eventapp.dao.EventDAO;
import com.vityarthi.eventapp.dao.TicketDAO;
import com.vityarthi.eventapp.model.Event;
import com.vityarthi.eventapp.model.Ticket;

import java.util.Random;

// Small service class that sits between the UI and the DAO layer.
// Handles ticket code generation, registration rules, and check-in logic.
public class TicketService {

    private TicketDAO ticketDAO = new TicketDAO();
    private EventDAO eventDAO = new EventDAO();
    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private Random rand = new Random();

    // Generate a code like "EVT-4F7K2A" and make sure it's not already used
    public String generateTicketCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder("EVT-");
            for (int i = 0; i < 6; i++) {
                sb.append(CHARS.charAt(rand.nextInt(CHARS.length())));
            }
            code = sb.toString();
        } while (ticketDAO.getTicket(code) != null); // just in case of a collision

        return code;
    }

    // Simple result holder for registration
    public static class RegistrationResult {
        public boolean success;
        public String message;
        public Ticket ticket;

        public RegistrationResult(boolean success, String message, Ticket ticket) {
            this.success = success;
            this.message = message;
            this.ticket = ticket;
        }
    }

    // Registers a new attendee if capacity allows
    public RegistrationResult registerTicket(int eventId, String attendeeName, String attendeeEmail) {
        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            return new RegistrationResult(false, "Selected event was not found.", null);
        }

        // Don't allow registering for an event that has already ended
        if ("Completed".equals(event.getStatus())) {
            return new RegistrationResult(false, "Cannot register for an event that has already ended.", null);
        }

        // Check if this attendee is already registered
        if (ticketDAO.isEmailRegistered(eventId, attendeeEmail)) {
            return new RegistrationResult(false,
                    "This email (" + attendeeEmail.trim() + ") is already registered for this event.", null);
        }

        // Check if event has reached maximum capacity
        int currentCount = ticketDAO.getTicketCountForEvent(eventId);
        if (currentCount >= event.getCapacity()) {
            return new RegistrationResult(false,
                    "Event is at full capacity (" + event.getCapacity() + " tickets). No more spots available.", null);
        }

        String code = generateTicketCode();
        Ticket ticket = new Ticket(code, eventId, attendeeName.trim(), attendeeEmail.trim(), "VALID");

        boolean saved = ticketDAO.saveTicket(ticket);
        if (!saved) {
            return new RegistrationResult(false, "Database error: could not save ticket.", null);
        }

        return new RegistrationResult(true, "Registration successful!", ticket);
    }

    // Result object so the UI can tell exactly what happened at the gate
    public static class CheckInResult {
        public boolean success;
        public String message;

        public CheckInResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    // Core check-in logic used by the Gate Verification tab
    public CheckInResult checkInTicket(String ticketCode) {
        if (ticketCode == null || ticketCode.trim().isEmpty()) {
            return new CheckInResult(false, "Please enter a ticket code.");
        }

        String code = ticketCode.trim().toUpperCase();
        Ticket ticket = ticketDAO.getTicket(code);

        if (ticket == null) {
            return new CheckInResult(false, "Ticket not found. Please check the code and try again.");
        }

        if ("USED".equalsIgnoreCase(ticket.getStatus())) {
            return new CheckInResult(false, "This ticket has already been used for check-in! Attendee: " + ticket.getAttendeeName());
        }

        // Try to mark it as used
        boolean marked = ticketDAO.markTicketAsUsed(ticket.getTicketCode());
        if (!marked) {
            return new CheckInResult(false, "This ticket has already been checked in!");
        }

        Event event = eventDAO.getEventById(ticket.getEventId());
        String eventTitle = (event != null) ? event.getTitle() : "Event #" + ticket.getEventId();

        return new CheckInResult(true, "Check-in successful! Welcome, " + ticket.getAttendeeName() + " (" + eventTitle + ").");
    }
}
