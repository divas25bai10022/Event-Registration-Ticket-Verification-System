package com.vityarthi.eventapp.model;

import java.time.LocalDate;

// Simple POJO to hold event details
public class Event {

    private int eventId;
    private String title;
    private String venue;
    private String eventDate;
    private int capacity;

    public Event() {
    }

    // Used when we already know the id (reading from DB)
    public Event(int eventId, String title, String venue, String eventDate, int capacity) {
        this.eventId = eventId;
        this.title = title;
        this.venue = venue;
        this.eventDate = eventDate;
        this.capacity = capacity;
    }

    // Used when creating a new event (id not assigned yet, DB will auto-generate it)
    public Event(String title, String venue, String eventDate, int capacity) {
        this.title = title;
        this.venue = venue;
        this.eventDate = eventDate;
        this.capacity = capacity;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    // Figure out if the event is today, upcoming, or already completed
    public String getStatus() {
        if (eventDate == null || eventDate.trim().isEmpty()) {
            return "Upcoming";
        }
        try {
            LocalDate today = LocalDate.now();
            LocalDate d = LocalDate.parse(eventDate.trim());
            if (d.isEqual(today)) {
                return "Ongoing";
            } else if (d.isAfter(today)) {
                return "Upcoming";
            } else {
                return "Completed";
            }
        } catch (Exception e) {
            return "Upcoming";
        }
    }

    // Handy for showing this in the event drop-down on the ticket tab
    @Override
    public String toString() {
        return eventId + " - " + title + " [" + getStatus() + "] (" + venue + ")";
    }
}
