package com.gutenberg;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.kennycason.kumo.WordFrequency;

public class WordCloudGui {
    DataProcessor processor;
    JTabbedPane tabbedPane;

    
    /** 
     * @param args
     */
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
        tabbedPane = new JTabbedPane();

        // Initialize an executor service with a fixed thread pool size
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Read data from gutenberg-data folder using DataProcessor
        processor = new DataProcessor();
        Map<String, String> dataMap = processor.readFromGutenbergData();

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
        WordCloudPanel wordCloudPanel = new WordCloudPanel(wordFrequenciesList, processor.getExtractedAuthors());

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

    
    /** 
     * Method to create a JMenuBar with menus and menu items
     * 
     * @return JMenuBar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Create a "File" menu with a "New" menu item
        JMenu fileMenu = new JMenu("File");
        JMenuItem newMenuItem = new JMenuItem("Open New Directory");

        // Set accelerator key for new directory (Ctrl + o)
        int shortcutKey = InputEvent.CTRL_DOWN_MASK; // Ctrl key for shortcut
        newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, shortcutKey));
        
        newMenuItem.addActionListener(e -> {
            // Handle "New" menu item click
            // Select a directory using JFileChooser
            String newDir = selectDirectory();

            if (newDir != null) {
                // Create a new WordCloudPanel and refresh the panel
                createNewWordCloudPanel(newDir);
            }
        });

        fileMenu.add(newMenuItem);
        menuBar.add(fileMenu);

        // Add more menus as needed, such as Options, Help, etc.

        return menuBar;
    }

    
    /** 
     * @param folderPath
     */
    // Method to create a new WordCloudPanel based on the specified directory
    private void createNewWordCloudPanel(String folderPath) {
        // Read data from the specified folder path using DataProcessor
        processor = new DataProcessor();
        Map<String, String> dataMap = processor.readFromGutenbergData(folderPath);

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
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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

        // Create a new WordCloudPanel with the word frequencies list and authors
        WordCloudPanel newWordCloudPanel = new WordCloudPanel(wordFrequenciesList, processor.getExtractedAuthors());

        // Add the new WordCloudPanel as a new tab in the tabbedPane
        tabbedPane.addTab("Word Cloud", newWordCloudPanel);
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1); // Select the new tab
    }

    
    /** 
     * @return String
     */
    // Method to allow the user to select a directory using JFileChooser
    public String selectDirectory() {
        // Create a JFileChooser instance
        JFileChooser fileChooser = new JFileChooser();

        // Configure the file chooser to only allow directory selection
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the file chooser dialog
        int result = fileChooser.showOpenDialog(null);

        // Check if the user approved the selection
        if (result == JFileChooser.APPROVE_OPTION) {
            // Get the selected file (directory)
            File selectedDirectory = fileChooser.getSelectedFile();

            // Return the full path of the selected directory
            return selectedDirectory.getAbsolutePath();
        }

        // If the user canceled the dialog, return null
        return null;
    }
}
