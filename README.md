# Event Registration & Ticket Verification System
# 🎫 Event Registration System

A straightforward desktop application for managing events, issuing tickets, and checking in attendees at the door. Built using Java Swing for the frontend and SQLite for local data persistence.

I created this project to keep event management simple and portable—no heavy database servers to configure, just run and go!

---

## ✨ Features

- **Event Management**: Set up events with title, date, and maximum capacity limits.
- **Ticket Issuance**: Register attendees and auto-generate tickets (with built-in capacity checks).
- **Fast Check-in**: Verify ticket IDs during venue entry to prevent duplicate check-ins.
- **Zero-Setup Database**: Uses an embedded SQLite database (`events.db`) right out of the box.

---

## 🛠️ Tech Stack

- **Language & GUI**: Java 8+, Java Swing
- **Database**: SQLite (via `sqlite-jdbc`)
- **Logging**: SLF4J (Simple Logger)
- **Build Tool**: Apache Maven

---

## 📁 Project Structure

The project follows a standard multi-tier design:

```text
com.vityarthi.eventapp
 ├── dao/         # Database operations (EventDAO, TicketDAO, DBConnection)
 ├── model/       # Data models (Event, Ticket)
 ├── service/     # Business logic & validations (TicketService)
 └── ui/          # Swing interface (MainUI, MainApp)
