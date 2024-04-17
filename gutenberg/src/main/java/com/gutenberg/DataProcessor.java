package com.gutenberg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.crypto.Data;

public class DataProcessor {

    Map<String, String> dataMap;
    private static final int THREAD_POOL_SIZE = 5; // Number of threads in the thread pool
    ArrayList<String> extractedAuthors = new ArrayList<>();

    // Constructor
    public DataProcessor() {
        // Initialization code if necessary
    }

    /**
     * Reads data files from the "resources/gutenberg-data" folder and returns a map
     * with file names as keys and file contents as values.
     *
     * @return A map where the keys are file names and the values are file contents.
     */
    public Map<String, String> readFromGutenbergData() {
        // Create a map to store file data
        Map<String, String> dataMap = new HashMap<>();

        // Specify the folder path in the resources directory
        String folderPath = "gutenberg-data/";

        // Get the class loader
        ClassLoader classLoader = DataProcessor.class.getClassLoader();

        // Create a thread pool with a fixed size
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        try {
            // Get the names of the files in the folder
            InputStream folderStream = classLoader.getResourceAsStream(folderPath);
            Objects.requireNonNull(folderStream, "Folder stream is null.");

            // Read each file name from the folder stream
            BufferedReader folderReader = new BufferedReader(
                    new InputStreamReader(folderStream, StandardCharsets.UTF_8));
            String fileName;

            // Loop through each file name in the folder
            while ((fileName = folderReader.readLine()) != null) {
                // Submit the file processing task to the executor service
                executorService.submit(new FileProcessorTask(fileName.trim(), dataMap, classLoader, folderPath));
            }

            // Close the folder reader
            folderReader.close();

        } catch (IOException e) {
            System.err.println("Error reading files from gutenberg-data folder.");
            e.printStackTrace();
        }

        // Shutdown the executor service and wait for all tasks to complete
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.dataMap = dataMap;

        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            String fileName = entry.getKey();
            String fileContent = entry.getValue();
            // Extract authors' names from the content
            ArrayList<String> authors = extractAuthors(fileContent);
            this.extractedAuthors.addAll(authors);
        }

        return dataMap;
    }    
    /** 
     * @param directoryPath
     * @return Map<String, String>
     */
    public Map<String, String> readFromGutenbergData(String directoryPath) {
        System.out.println("Start Fresh From:"+directoryPath);
        // Create a map to store file data
        Map<String, String> dataMap = new HashMap<>();
    
        // Create a thread pool with a fixed size
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    
        // Get the folder as a File object
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            System.err.println("Provided path is not a valid directory: " + directoryPath);
            return dataMap;
        }
    
        // Get the list of files in the directory
        File[] files = directory.listFiles();
        
        if (files != null) {
            // Iterate through each file in the directory
            for (File file : files) {
                if (file.isDirectory()) {
                    // If the file is a directory, process the subdirectory recursively
                    Map<String, String> subdirectoryDataMap = readFromGutenbergData(file.getAbsolutePath());
                    dataMap.putAll(subdirectoryDataMap);
                } else {
                    // Process the file if it is not a directory
                    executorService.submit(new FileProcessorTask2(file.getName(), dataMap, file.getAbsolutePath()));
                }
            }
        }
    
        // Shutdown the executor service and wait for all tasks to complete
        executorService.shutdown();
        try {
            executorService.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    
        // Extract authors' names from file content
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            String fileName = entry.getKey();
            String fileContent = entry.getValue();
            ArrayList<String> authors = extractAuthors(fileContent);
            this.extractedAuthors.addAll(authors);
        }
    
        return dataMap;
    }

    /**
     * Applies the specified filters using regular expressions to the content.
     *
     * @param content The text content to filter.
     * @return A map containing filtered words and their frequencies.
     */
    public static Map<String, Integer> applyFilters(String content) {
        Map<String, Integer> wordFrequencies = new HashMap<>();

        // Define the regex patterns for the filters
        Pattern patternIng = Pattern.compile("\\b\\w+ing\\b");
        Pattern patternOugh = Pattern.compile("\\b\\w+ough\\b");
        Pattern patternIsm = Pattern.compile("\\b\\w+ism\\b");
        Pattern patternKn = Pattern.compile("\\bkn\\w+");
        Pattern patternAugh = Pattern.compile("\\b\\w+augh\\b");

        // Define a pattern to split content into words
        Pattern wordPattern = Pattern.compile("\\b\\w+\\b");

        // Use a matcher to find and count words according to the filters
        Matcher matcher = wordPattern.matcher(content);
        while (matcher.find()) {
            String word = matcher.group().toLowerCase();

            // Check each filter pattern and add to frequency map
            if (patternIng.matcher(word).find() ||
                    patternOugh.matcher(word).find() ||
                    patternIsm.matcher(word).find() ||
                    patternKn.matcher(word).find() ||
                    patternAugh.matcher(word).find()) {
                // Increment the word frequency
                wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
            }
        }

        return wordFrequencies;
    }

    /**
     * Extracts authors' names from the content using regular expressions.
     *
     * @param content The text content to extract authors' names from.
     * @return A list of authors' names found in the content.
     */
    public ArrayList<String> extractAuthors(String content) {
        ArrayList<String> authors = new ArrayList<>();

        // Define the regex pattern for extracting authors' names
        Pattern authorPattern = Pattern.compile("(?i)Author:\\s*([^\\n]+)");

        // Use a matcher to find authors' names
        Matcher matcher = authorPattern.matcher(content);
        while (matcher.find()) {
            String author = matcher.group(1).trim();
            authors.add(author);
        }

        return authors;
    }

    /**
     * Main method for demonstration.
     */
    public static void main(String[] args) {
        DataProcessor proccProcessor = new DataProcessor();
        Map<String, String> dataMap = proccProcessor.readFromGutenbergData();
        System.out.println(proccProcessor.getExtractedAuthors());

        // // Read data from gutenberg-data folder
        // Map<String, String> dataMap = readFromGutenbergData();

        // // Iterate over each file and apply filters and extract authors
        // for (Map.Entry<String, String> entry : dataMap.entrySet()) {
        // String fileName = entry.getKey();
        // String fileContent = entry.getValue();

        // System.out.println("File Name: " + fileName + " Length: " +
        // fileContent.length());

        // // Apply filters to the content
        // Map<String, Integer> filteredWords = applyFilters(fileContent);
        // System.out.println("Filtered Words and Frequencies: " + filteredWords);

        // // Extract authors' names from the content
        // ArrayList<String> authors = extractAuthors(fileContent);
        // System.out.println("Authors: " + authors);

        // System.out.println("-----");
        // }
    }

    
    /** 
     * @return ArrayList<String>
     */
    public ArrayList<String> getExtractedAuthors() {
        return extractedAuthors;
    }

    public void setExtractedAuthors(ArrayList<String> extractedAuthors) {
        this.extractedAuthors = extractedAuthors;
    }

    private static class FileProcessorTask2 implements Runnable {
        private final String fileName;
        private final Map<String, String> dataMap;
        private final String filePath;

        public FileProcessorTask2(String fileName, Map<String, String> dataMap, String filePath) {
            this.fileName = fileName;
            this.dataMap = dataMap;
            this.filePath = filePath;
        }

        @Override
        public void run() {
            try {
                // Read file content using BufferedReader
                BufferedReader fileReader = new BufferedReader(new FileReader(filePath, StandardCharsets.UTF_8));
                StringBuilder fileContent = new StringBuilder();
                String line;

                // Read file line by line
                while ((line = fileReader.readLine()) != null) {
                    fileContent.append(line).append("\n");
                }

                // Add file content to the map with the file name as the key
                synchronized (dataMap) {
                    dataMap.put(fileName, fileContent.toString());
                }

                // Close the file reader
                fileReader.close();
            } catch (IOException e) {
                System.err.println("Error reading file: " + fileName);
                e.printStackTrace();
            }
        }
    }

    /**
     * Runnable class that processes file data.
     */
    private static class FileProcessorTask implements Runnable {
        private final String fileName;
        private final Map<String, String> dataMap;
        private final ClassLoader classLoader;
        private final String folderPath;

        public FileProcessorTask(String fileName, Map<String, String> dataMap, ClassLoader classLoader,
                String folderPath) {
            this.fileName = fileName;
            this.dataMap = dataMap;
            this.classLoader = classLoader;
            this.folderPath = folderPath;
        }

        @Override
        public void run() {
            try {
                // Get the input stream for the current file
                InputStream fileStream = classLoader
                        .getResourceAsStream(folderPath + fileName + "/pg" + fileName + ".txt");
                if (fileStream != null) {
                    // Read file content using BufferedReader
                    BufferedReader fileReader = new BufferedReader(
                            new InputStreamReader(fileStream, StandardCharsets.UTF_8));
                    StringBuilder fileContent = new StringBuilder();
                    String line;

                    // Read file line by line
                    while ((line = fileReader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }

                    // Add file content to the map with the file name as the key
                    synchronized (dataMap) {
                        dataMap.put(fileName, fileContent.toString());
                    }

                    // Close the file reader
                    fileReader.close();
                } else {
                    System.out.println("Null file stream for file: " + folderPath + fileName);
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + fileName);
                e.printStackTrace();
            }
        }
    }
}
