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

```
com.vityarthi.eventapp
 ├── dao/         # Database operations (EventDAO, TicketDAO, DBConnection)
 ├── model/       # Data models (Event, Ticket)
 ├── service/     # Business logic & validations (TicketService)
 └── ui/          # Swing interface (MainUI, MainApp)
```

---

## 🚀 How to Run

### Option 1: Quick Launch (Windows)
If you're on Windows, just double-click or run the batch script:
```cmd
run.bat
```

### Option 2: Run via Maven
```bash
mvn compile exec:java -Dexec.mainClass="com.vityarthi.eventapp.MainApp"
```

### Option 3: Manual Execution
```bash
javac -cp "lib/*;" -d bin src/com/vityarthi/eventapp/**/*.java
java -cp "bin;lib/*" com.vityarthi.eventapp.MainApp
```

---

## 📝 Notes & Future Ideas

- Adding PDF ticket export functionality.
- Improving the UI theme with FlatLaf.
- Adding bulk export (CSV) for event attendee lists.

Feel free to star ⭐️ the repo or open an issue if you find any bugs!
