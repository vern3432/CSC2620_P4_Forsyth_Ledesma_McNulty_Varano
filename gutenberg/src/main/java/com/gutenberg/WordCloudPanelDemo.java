package com.gutenberg;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import com.kennycason.kumo.WordFrequency;

public class WordCloudPanelDemo {

    public static void main(String[] args) {
        // Read data from gutenberg-data folder using DataProcessor
        Map<String, String> dataMap = DataProcessor.readFromGutenbergData();

        // Initialize an executor service with a fixed thread pool size
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

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

        // Create an instance of WordCloudPanel using the list of word frequencies
        WordCloudPanel wordCloudPanel = new WordCloudPanel(wordFrequenciesList);

        // Create a JFrame to hold the WordCloudPanel
        JFrame frame = new JFrame("Word Cloud Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.add(wordCloudPanel);
        frame.setVisible(true);
    }
}
