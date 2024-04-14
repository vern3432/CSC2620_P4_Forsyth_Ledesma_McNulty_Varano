package com.gutenberg;import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataProcessor {

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
    public static Map<String, String> readFromGutenbergData() {
        // Create a map to store file data
        Map<String, String> dataMap = new HashMap<>();

        // Specify the folder path in the resources directory
        String folderPath = "gutenberg-data/";

        // Get the class loader
        ClassLoader classLoader = DataProcessor.class.getClassLoader();

        try {
            // Get the names of the files in the folder
            InputStream folderStream = classLoader.getResourceAsStream(folderPath);
            Objects.requireNonNull(folderStream, "Folder stream is null.");

            // Read each file name from the folder stream
            BufferedReader folderReader = new BufferedReader(new InputStreamReader(folderStream, StandardCharsets.UTF_8));
            String fileName;

            // Loop through each file name in the folder
            while ((fileName = folderReader.readLine()) != null) {
                // Get the input stream for the current file
                InputStream fileStream = classLoader.getResourceAsStream(folderPath + fileName.trim());
                if (fileStream != null) {
                    // Read file content using BufferedReader
                    BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileStream, StandardCharsets.UTF_8));
                    StringBuilder fileContent = new StringBuilder();
                    String line;

                    // Read file line by line
                    while ((line = fileReader.readLine()) != null) {
                        fileContent.append(line).append("\n");
                    }

                    // Add file content to the map with the file name as the key
                    dataMap.put(fileName.trim(), fileContent.toString());

                    // Close the file reader
                    fileReader.close();
                }
            }

            // Close the folder reader
            folderReader.close();

        } catch (IOException e) {
            System.err.println("Error reading files from gutenberg-data folder.");
            e.fillInStackTrace();
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
    public static ArrayList<String> extractAuthors(String content) {
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

    public static void main(String[] args) {
        // Read data from gutenberg-data folder
        Map<String, String> dataMap = readFromGutenbergData();

        // Iterate over each file and apply filters and extract authors
        for (Map.Entry<String, String> entry : dataMap.entrySet()) {
            String fileName = entry.getKey();
            String fileContent = entry.getValue();

            System.out.println("File Name: " + fileName);

            // Apply filters to the content
            Map<String, Integer> filteredWords = applyFilters(fileContent);
            System.out.println("Filtered Words and Frequencies: " + filteredWords);

            // Extract authors' names from the content
            ArrayList<String> authors = extractAuthors(fileContent);
            System.out.println("Authors: " + authors);

            System.out.println("-----");
        }
    }
}
