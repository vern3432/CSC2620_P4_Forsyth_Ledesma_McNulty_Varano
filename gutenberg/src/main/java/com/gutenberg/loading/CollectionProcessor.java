package com.gutenberg.loading;

import com.gutenberg.Filters;
import com.gutenberg.cloud.WordCloudStorage;
import com.gutenberg.panels.StatusPanel;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectionProcessor {
    private final ClassLoader classLoader;
    String folderPath = "gutenberg-data/";
    private WordCloudStorage wordCloudStorage;
    private StatusPanel statusPanel;

    // Define a pattern to split content into words
    private static final Pattern wordPattern = Pattern.compile("\\b\\w+\\b");

    public CollectionProcessor(WordCloudStorage wordCloudStorage, StatusPanel statusPanel) {
        this.wordCloudStorage = wordCloudStorage;
        this.statusPanel = statusPanel;
        this.classLoader = CollectionProcessor.class.getClassLoader();
    }

    public void process() {
        Thread backgroundThread = new Thread(() -> {
            ExecutorService executor = Executors.newFixedThreadPool(5);
            try {
                CompletableFuture<Void> allTasks = processFolder(folderPath, executor);
                // Wait for all tasks to complete
                allTasks.get();

            } catch (Exception ex) {
                // Show Error Dialog
            } finally {
                executor.shutdown();
                statusPanel.stopAnimation();
                statusPanel.setStatus("All files completed");
            }
        });

        // Start the background thread
        backgroundThread.start();
    }

    private CompletableFuture<Void> processFolder(String folderPath, Executor executor) throws IOException, URISyntaxException {
        CompletableFuture<Void> allTasks = CompletableFuture.completedFuture(null);
        allTasks = Files.walk(Paths.get(classLoader.getResource(folderPath).toURI()))
                .parallel() // Enable parallel traversal
                .filter(Files::isRegularFile) // Process only regular files
                .map(path -> CompletableFuture.runAsync(() -> processFile(path), executor))
                .reduce(CompletableFuture::allOf)
                .orElse(CompletableFuture.completedFuture(null));
        return allTasks;
    }

    private void processFile(Path path) {
        try(var fileReader = new BufferedReader(new FileReader(path.toFile()))) {
            final var wordFrequencyMap = new ConcurrentHashMap<String, Integer>();

            String line;
            while ((line = fileReader.readLine()) != null) {
                Matcher matcher = wordPattern.matcher(line);
                while(matcher.find()) {
                    String word = matcher.group().toLowerCase();
                    if (word.length()  < 5) {
                        continue;
                    }
                    // Check each filter pattern and add to frequency map
                    if (Filters.ingPattern.matcher(word).find() ||
                            Filters.oughPattern.matcher(word).find() ||
                            Filters.ismPattern.matcher(word).find() ||
                            Filters.knPattern.matcher(word).find() ||
                            Filters.aughPattern.matcher(word).find()) {
                        // Increment the word frequency
                        var currentValue = wordFrequencyMap.getOrDefault(word, 0);
                        wordFrequencyMap.put(word, currentValue + 1);
                    }

                }
            }
            this.wordCloudStorage.addWords(wordFrequencyMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
