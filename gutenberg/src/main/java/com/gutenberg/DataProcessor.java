package com.gutenberg;import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
            e.printStackTrace();
        }

        return dataMap;
    }

    public static void main(String[] args) {
        // Example usage: Read data from the gutenberg-data folder
        Map<String, String> data = readFromGutenbergData();

        // Print the data (file name and content)
        for (Map.Entry<String, String> entry : data.entrySet()) {
            System.out.println("File Name: " + entry.getKey());
            System.out.println("File Content: " + entry.getValue().substring(0, Math.min(100, entry.getValue().length())) + "...");
            System.out.println("-----");
        }
    }
}
