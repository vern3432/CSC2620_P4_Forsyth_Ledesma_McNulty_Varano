package com.gutenberg;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.kennycason.kumo.WordFrequency;

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
        frame.setSize(800, 600);

        // Create a JTabbedPane for multiple word cloud tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Initialize an executor service with a fixed thread pool size
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Read data from gutenberg-data folder using DataProcessor
        Map<String, String> dataMap = DataProcessor.readFromGutenbergData();

        // Create a concurrent map to hold the word frequencies without duplicates
        ConcurrentMap<String, Integer> wordFrequenciesMap = new ConcurrentHashMap<>();

        // Submit tasks to process data and calculate word frequencies concurrently
        List<Callable<Void>> tasks = dataMap.entrySet().stream()
            .map(entry -> (Callable<Void>) () -> {
                String fileContent = entry.getValue();
                Map<String, Integer> frequencies = DataProcessor.applyFilters(fileContent);
                
                // Merge the frequencies into the concurrent map
                frequencies.forEach((word, frequency) -> 
                    wordFrequenciesMap.merge(word, frequency, Integer::sum)
                );
                
                return null;
            }).collect(Collectors.toList());

        // Submit all tasks to the executor service and wait for completion
        try {
            executorService.invokeAll(tasks);
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Convert the map to a list of WordFrequency objects
        List<WordFrequency> wordFrequenciesList = wordFrequenciesMap.entrySet().stream()
            .map(entry -> new WordFrequency(entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());

        // Create a WordCloudPanel with the word frequencies list
        WordCloudPanel wordCloudPanel = new WordCloudPanel(wordFrequenciesList);

        // Add the WordCloudPanel as a tab in the tabbedPane
        tabbedPane.addTab("Word Cloud", wordCloudPanel);

        // Add the tabbedPane to the frame
        frame.add(tabbedPane, BorderLayout.CENTER);

        // Create a JMenuBar and menus for the frame
        JMenuBar menuBar = createMenuBar();
        frame.setJMenuBar(menuBar);

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
