package com.gutenberg.loading;

import com.gutenberg.cloud.WordCloudStorage;
import com.gutenberg.panels.StatusPanel;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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

    // Define the regex patterns for the filters
    private static final Pattern patternIng = Pattern.compile("\\b\\w+ing\\b");
    private static final Pattern patternOugh = Pattern.compile("\\b\\w+ough\\b");
    private static final Pattern patternIsm = Pattern.compile("\\b\\w+ism\\b");
    private static final Pattern patternKn = Pattern.compile("\\bkn\\w+");
    private static final Pattern patternAugh = Pattern.compile("\\b\\w+augh\\b");

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
                    // Check each filter pattern and add to frequency map
                    if (patternIng.matcher(word).find() ||
                            patternOugh.matcher(word).find() ||
                            patternIsm.matcher(word).find() ||
                            patternKn.matcher(word).find() ||
                            patternAugh.matcher(word).find()) {
                        // Increment the word frequency
                        wordFrequencyMap.merge(word, wordFrequencyMap.getOrDefault(word, 1), Integer::sum);
                    }

                }
            }
            this.wordCloudStorage.addWords(wordFrequencyMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
