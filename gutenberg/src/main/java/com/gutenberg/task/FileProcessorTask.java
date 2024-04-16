package com.gutenberg.task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class FileProcessorTask implements Runnable {
    private final String fileName;
    private final Map<String, String> dataMap;
    private final ClassLoader classLoader;
    private final String folderPath;

    public FileProcessorTask(String fileName, Map<String, String> dataMap, ClassLoader classLoader, String folderPath) {
        this.fileName = fileName;
        this.dataMap = dataMap;
        this.classLoader = classLoader;
        this.folderPath = folderPath;
    }

    @Override
    public void run() {
        try {
            // Get the input stream for the current file
            InputStream fileStream = classLoader.getResourceAsStream(folderPath + fileName + "/pg" + fileName + ".txt");
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