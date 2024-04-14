package com.gutenberg;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WordCloudGui {

    // Main method to run the application
    public static void main(String[] args) {
        // Create an instance of the WordCloudGui
        WordCloudGui app = new WordCloudGui();
        app.createAndShowGUI();
    }

    // Create and show the GUI
    public void createAndShowGUI() {
        // Create a JFrame
        JFrame frame = new JFrame("Word Cloud Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);

        // Create a JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();

        // Add a tab with the WordCloudPanel
        WordCloudPanel wordCloudPanel = new WordCloudPanel();
        tabbedPane.addTab("Word Cloud", wordCloudPanel);

        // Add other tabs as needed
        // You can add more tabs for other functionalities of your program

        // Create a JMenuBar for the top options menu
        JMenuBar menuBar = new JMenuBar();

        // Create a "File" menu
        JMenu fileMenu = new JMenu("File");
        JMenu optionsMenu = new JMenu("Options");

        // Create a "New" menu item under "File"
        JMenuItem newMenuItem = new JMenuItem("New");
        JMenuItem testOption = new JMenuItem("Test Option");

        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Handle "New" menu item click
                // You can add functionality for creating a new word cloud, loading new data, etc.
                System.out.println("New menu item clicked");
            }
        });
        fileMenu.add(newMenuItem);
        optionsMenu.add(testOption);

        // Create other menu items as needed under "File"
        // For example: Open, Save, Exit, etc.
        
        // Add the "File" menu to the menu bar
        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);


        // Add the menu bar to the frame
        frame.setJMenuBar(menuBar);

        // Add the tabbed pane to the frame
        frame.add(tabbedPane);

        // Set the frame to be visible
        frame.setVisible(true);
    }
}
