package com.vityarthi.eventapp;

import com.vityarthi.eventapp.dao.DBConnection;
import com.vityarthi.eventapp.ui.MainUI;

import javax.swing.*;

// Entry point for the app
public class MainApp {

    public static void main(String[] args) {
        // Make sure the DB and tables are ready before we open the GUI
        DBConnection.createTables();

        // Try to use the system look and feel so it doesn't look like default Swing gray
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set look and feel, using default.");
        }

        // Swing GUIs should be started on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            MainUI ui = new MainUI();
            ui.setVisible(true);
        });
    }
}
