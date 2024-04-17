package com.gutenberg.loading;

import com.gutenberg.RegexFilters;
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

import static com.gutenberg.RegexFilters.authorPattern;

public class CollectionProcessor {
    private final ClassLoader classLoader;
    String folderPath = "/Users/maddie/JavaProjects/CSC2620_P4_Forsyth_Ledesma_McNulty_Varano/gutenberg/src/main/resources/gutenberg-data";
    private WordCloudStorage wordCloudStorage;
    private StatusPanel statusPanel;

    private int fileCount = 0;

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
                statusPanel.setStatus(fileCount + " files discovered");
            }
        });

        // Start the background thread
        backgroundThread.start();
    }

    private CompletableFuture<Void> processFolder(String folderPath, Executor executor) throws IOException, URISyntaxException {
        CompletableFuture<Void> allTasks = CompletableFuture.completedFuture(null);
        allTasks = Files.walk(Paths.get(folderPath))
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
            final var authorFrequencyMap = new ConcurrentHashMap<String, Integer>();
            String line;
            while ((line = fileReader.readLine()) != null) {
                Matcher matcher = wordPattern.matcher(line);
                while(matcher.find()) {
                    String word = matcher.group().toLowerCase();
                    if (word.length()  < 5) {
                        continue;
                    }
                    // Check each filter pattern and add to frequency map
                    if (RegexFilters.ingPattern.matcher(word).find() ||
                            RegexFilters.oughPattern.matcher(word).find() ||
                            RegexFilters.ismPattern.matcher(word).find() ||
                            RegexFilters.knPattern.matcher(word).find() ||
                            RegexFilters.aughPattern.matcher(word).find()) {
                        // Increment the word frequency
                        var currentValue = wordFrequencyMap.getOrDefault(word, 0);
                        wordFrequencyMap.put(word, currentValue + 1);
                    }
                }

                // Use a matcher to find authors' names
                matcher = authorPattern.matcher(line);
                while (matcher.find()) {
                    String author = matcher.group(1).trim();
                    // Check if multiple auth
                    var currentValue = wordFrequencyMap.getOrDefault(author, 0);
                    authorFrequencyMap.put(author, currentValue + 1);
                }

            }
            this.wordCloudStorage.addWords(wordFrequencyMap);
            this.wordCloudStorage.addAuthors(authorFrequencyMap);
            fileCount++;
            statusPanel.setStatus("Scanning files (" + fileCount + ") ...");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
