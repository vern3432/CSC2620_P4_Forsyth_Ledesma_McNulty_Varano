package com.gutenberg;

import com.gutenberg.cloud.WordCloudStorage;
import com.gutenberg.loading.CollectionProcessor;
import com.gutenberg.panels.StatusPanel;
import com.gutenberg.panels.WordCloudPanel;

import javax.swing.*;
import java.awt.*;

public class WordCloudGui {

    public static void main(String[] args) {
        // Create an instance of WordCloudGui and display the GUI
        SwingUtilities.invokeLater(() -> {
            new WordCloudGui().createAndShowGUI();
        });
    }

    // Method to create and show the GUI
    void createAndShowGUI() {
        // Create a JFrame
        JFrame frame = new JFrame("Word Cloud Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(850, 750);

        // Create a JTabbedPane for multiple word cloud tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        var wordCloud = new WordCloudStorage();
        var statusPanel = new StatusPanel();
        var collectionProcessor = new CollectionProcessor(wordCloud, statusPanel);
        collectionProcessor.process();

        // Create a WordCloudPanel with the word frequencies list
        WordCloudPanel wordCloudPanel = new WordCloudPanel(wordCloud, frame);

        // Add the WordCloudPanel as a tab in the tabbedPane
        tabbedPane.addTab("Word Cloud", wordCloudPanel);

        // Add the tabbedPane to the frame
        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.add(statusPanel, BorderLayout.SOUTH);

        // Make the frame visible
        frame.setVisible(true);
    }

    // Method to create a JMenuBar with menus and menu items
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Create a "File" menu with a "New" menu item
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(e -> {
            // Handle "New" menu item click
            // Add functionality for creating a new word cloud, loading new data, etc.
            System.out.println("New menu item clicked");
        });
        fileMenu.add(newMenuItem);
        menuBar.add(fileMenu);

        // Add more menus as needed, such as Options, Help, etc.

        return menuBar;
    }
}
