package com.vityarthi.eventapp.ui;

import com.vityarthi.eventapp.dao.EventDAO;
import com.vityarthi.eventapp.dao.TicketDAO;
import com.vityarthi.eventapp.model.Event;
import com.vityarthi.eventapp.model.Ticket;
import com.vityarthi.eventapp.service.TicketService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

// Main window for the app - holds the 3 tabs (Events, Register Ticket, Gate Check-In)
public class MainUI extends JFrame {

    private EventDAO eventDAO = new EventDAO();
    private TicketDAO ticketDAO = new TicketDAO();
    private TicketService ticketService = new TicketService();

    // Tab 1 components
    private JTextField titleField, venueField, dateField, capacityField;
    private JComboBox<String> statusFilter;
    private DefaultTableModel eventTableModel;
    private JTable eventTable;

    // Tab 2 components
    private JTextField attendeeNameField, attendeeEmailField;
    private JComboBox<Event> eventDropdown;
    private DefaultTableModel ticketTableModel;
    private JTable ticketTable;

    // Tab 3 components
    private JTextField ticketCodeField;
    private JLabel checkInResultLabel;

    public MainUI() {
        setTitle("Event Registration & Ticket Verification System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(820, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Manage Events", buildEventTab());
        tabs.addTab("Register Ticket", buildTicketTab());
        tabs.addTab("Gate Check-In", buildCheckInTab());

        // Keep tables and dropdowns fresh when switching tabs
        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            if (selectedIndex == 0) {
                refreshEventTable();
            } else if (selectedIndex == 1) {
                refreshEventDropdown();
                refreshTicketTable();
            }
        });

        add(tabs);

        refreshEventTable();
        refreshTicketTable();
    }

    // ---------- TAB 1: Manage Events ----------
    private JPanel buildEventTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form for adding a new event
        JPanel formPanel = new JPanel(new GridLayout(5, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Create New Event"));

        titleField = new JTextField();
        venueField = new JTextField();
        dateField = new JTextField();
        capacityField = new JTextField();

        formPanel.add(new JLabel("Event Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Venue:"));
        formPanel.add(venueField);
        formPanel.add(new JLabel("Date (e.g. 2026-10-05):"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Total Capacity:"));
        formPanel.add(capacityField);

        JButton addEventBtn = new JButton("Add Event");
        addEventBtn.addActionListener(e -> addEvent());
        formPanel.add(new JLabel()); // empty cell for spacing
        formPanel.add(addEventBtn);

        // Events table
        eventTableModel = new DefaultTableModel(
                new String[]{"ID", "Title", "Venue", "Date", "Status", "Capacity", "Registered", "Available"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // read only table
            }
        };
        eventTable = new JTable(eventTableModel);
        eventTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(eventTable);

        // Filter and delete controls
        JPanel tablePanel = new JPanel(new BorderLayout(5, 5));
        tablePanel.setBorder(BorderFactory.createTitledBorder("Events"));

        JPanel tableToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        tableToolbar.add(new JLabel("Filter Status:"));
        statusFilter = new JComboBox<>(new String[]{"All Events", "Ongoing", "Upcoming", "Completed"});
        statusFilter.addActionListener(e -> refreshEventTable());
        tableToolbar.add(statusFilter);

        JButton deleteBtn = new JButton("Delete Selected Event");
        deleteBtn.addActionListener(e -> deleteSelectedEvent());
        tableToolbar.add(deleteBtn);

        tablePanel.add(tableToolbar, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);

        return panel;
    }

    private void addEvent() {
        String title = titleField.getText().trim();
        String venue = venueField.getText().trim();
        String date = dateField.getText().trim();
        String capacityText = capacityField.getText().trim();

        if (title.isEmpty() || venue.isEmpty() || date.isEmpty() || capacityText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int capacity;
        try {
            capacity = Integer.parseInt(capacityText);
            if (capacity <= 0) {
                JOptionPane.showMessageDialog(this, "Capacity must be greater than zero.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Capacity must be a valid number.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Simple check for YYYY-MM-DD format
        if (!date.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            JOptionPane.showMessageDialog(this, "Please enter date in YYYY-MM-DD format (e.g. 2026-10-05).", "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Event event = new Event(title, venue, date, capacity);
        boolean saved = eventDAO.saveEvent(event);

        if (!saved) {
            JOptionPane.showMessageDialog(this, "Could not save event to database. Please check logs.", "Database Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // clear the form
        titleField.setText("");
        venueField.setText("");
        dateField.setText("");
        capacityField.setText("");

        refreshEventTable();
        refreshEventDropdown();

        JOptionPane.showMessageDialog(this, "Event added successfully!");
    }

    private void deleteSelectedEvent() {
        int selectedRow = eventTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an event from the table first.", "No Event Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int eventId = (int) eventTableModel.getValueAt(selectedRow, 0);
        String title = (String) eventTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + title + "'?\nThis will also delete any tickets registered for this event.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean deleted = eventDAO.deleteEvent(eventId);
            if (deleted) {
                refreshEventTable();
                refreshEventDropdown();
                refreshTicketTable();
                JOptionPane.showMessageDialog(this, "Event '" + title + "' was deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete event from database.", "Delete Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshEventTable() {
        if (eventTableModel == null) return;
        eventTableModel.setRowCount(0); // clear old rows
        String filter = (statusFilter != null && statusFilter.getSelectedItem() != null)
                ? (String) statusFilter.getSelectedItem() : "All Events";

        List<Event> events = eventDAO.getAllEvents();
        for (Event e : events) {
            String status = e.getStatus();
            if (!"All Events".equals(filter) && !status.equalsIgnoreCase(filter)) {
                continue;
            }
            int registered = eventDAO.getRegisteredCount(e.getEventId());
            int available = Math.max(0, e.getCapacity() - registered);
            eventTableModel.addRow(new Object[]{
                    e.getEventId(), e.getTitle(), e.getVenue(), e.getEventDate(),
                    status, e.getCapacity(), registered, available
            });
        }
    }

    // ---------- TAB 2: Register Ticket ----------
    private JPanel buildTicketTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Register Attendee"));

        attendeeNameField = new JTextField();
        attendeeEmailField = new JTextField();
        eventDropdown = new JComboBox<>();
        refreshEventDropdown();

        formPanel.add(new JLabel("Attendee Name:"));
        formPanel.add(attendeeNameField);
        formPanel.add(new JLabel("Attendee Email:"));
        formPanel.add(attendeeEmailField);
        formPanel.add(new JLabel("Select Event:"));
        formPanel.add(eventDropdown);

        JButton registerBtn = new JButton("Register & Generate Ticket");
        registerBtn.addActionListener(e -> registerTicket());
        formPanel.add(new JLabel());
        formPanel.add(registerBtn);

        // Tickets table
        ticketTableModel = new DefaultTableModel(
                new String[]{"Ticket Code", "Event ID", "Attendee Name", "Email", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        ticketTable = new JTable(ticketTableModel);
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Registered Tickets"));

        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void refreshEventDropdown() {
        if (eventDropdown == null) return;
        eventDropdown.removeAllItems();
        List<Event> events = eventDAO.getAllEvents();
        for (Event e : events) {
            eventDropdown.addItem(e);
        }
    }

    private void refreshTicketTable() {
        if (ticketTableModel == null) return;
        ticketTableModel.setRowCount(0);
        List<Ticket> tickets = ticketDAO.getAllTickets();
        for (Ticket t : tickets) {
            ticketTableModel.addRow(new Object[]{
                    t.getTicketCode(), t.getEventId(), t.getAttendeeName(), t.getAttendeeEmail(), t.getStatus()
            });
        }
    }

    private void registerTicket() {
        String name = attendeeNameField.getText().trim();
        String email = attendeeEmailField.getText().trim();
        Event selectedEvent = (Event) eventDropdown.getSelectedItem();

        if (selectedEvent == null) {
            JOptionPane.showMessageDialog(this, "No event selected. Please add an event first under 'Manage Events'.", "No Event", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (name.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in attendee name and email.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Quick check for email format
        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.", "Invalid Email", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TicketService.RegistrationResult result = ticketService.registerTicket(selectedEvent.getEventId(), name, email);

        if (!result.success) {
            JOptionPane.showMessageDialog(this, result.message, "Registration Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        attendeeNameField.setText("");
        attendeeEmailField.setText("");

        refreshTicketTable();
        refreshEventTable();

        JOptionPane.showMessageDialog(this,
                "Ticket registered!\n\nTicket Code: " + result.ticket.getTicketCode() +
                        "\nAttendee: " + result.ticket.getAttendeeName() +
                        "\nEvent: " + selectedEvent.getTitle(),
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ---------- TAB 3: Gate Check-In ----------
    private JPanel buildCheckInTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Ticket Verification"));

        ticketCodeField = new JTextField(20);
        JButton verifyBtn = new JButton("Verify & Check In");

        // Pressing Enter in the input field also triggers verification
        verifyBtn.addActionListener(e -> verifyTicket());
        ticketCodeField.addActionListener(e -> verifyTicket());

        inputPanel.add(new JLabel("Ticket Code:"));
        inputPanel.add(ticketCodeField);
        inputPanel.add(verifyBtn);

        // Feedback label showing check-in status and attendee name
        checkInResultLabel = new JLabel("Enter a ticket code and click Verify.", SwingConstants.CENTER);
        checkInResultLabel.setFont(new Font("SansSerif", Font.BOLD, 17));
        checkInResultLabel.setOpaque(true);
        checkInResultLabel.setBackground(Color.LIGHT_GRAY);
        checkInResultLabel.setForeground(Color.DARK_GRAY);
        checkInResultLabel.setPreferredSize(new Dimension(400, 150));

        panel.add(inputPanel, BorderLayout.NORTH);
        panel.add(checkInResultLabel, BorderLayout.CENTER);

        return panel;
    }

    private void verifyTicket() {
        String code = ticketCodeField.getText().trim();

        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a ticket code.", "Missing Info", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TicketService.CheckInResult result = ticketService.checkInTicket(code);

        checkInResultLabel.setText("<html><center style='padding: 10px;'>" + result.message + "</center></html>");
        if (result.success) {
            checkInResultLabel.setBackground(new Color(220, 245, 220));
            checkInResultLabel.setForeground(new Color(25, 90, 25));
        } else {
            checkInResultLabel.setBackground(new Color(255, 225, 225));
            checkInResultLabel.setForeground(new Color(140, 25, 25));
        }

        refreshTicketTable();
        refreshEventTable();

        ticketCodeField.setText("");
    }
}
