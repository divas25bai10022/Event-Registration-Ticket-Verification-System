package com.vityarthi.eventapp.model;

// Simple POJO to hold ticket details
public class Ticket {

    private String ticketCode;
    private int eventId;
    private String attendeeName;
    private String attendeeEmail;
    private String status; // "VALID" or "USED"

    public Ticket() {
    }

    public Ticket(String ticketCode, int eventId, String attendeeName, String attendeeEmail, String status) {
        this.ticketCode = ticketCode;
        this.eventId = eventId;
        this.attendeeName = attendeeName;
        this.attendeeEmail = attendeeEmail;
        this.status = status;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getAttendeeName() {
        return attendeeName;
    }

    public void setAttendeeName(String attendeeName) {
        this.attendeeName = attendeeName;
    }

    public String getAttendeeEmail() {
        return attendeeEmail;
    }

    public void setAttendeeEmail(String attendeeEmail) {
        this.attendeeEmail = attendeeEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
