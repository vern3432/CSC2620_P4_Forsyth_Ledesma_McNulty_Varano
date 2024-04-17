package com.gutenberg;

import javax.swing.*;

public class WordCloudPanelDemo {

    
    /** 
     * @param args
     */
    public static void main(String[] args) {
        // Create a JFrame to hold the WordCloudPanel
        JFrame frame = new JFrame("Word Cloud Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(450, 350); // Set the size of the frame

        // Create an instance of the WordCloudPanel
        WordCloudPanel wordCloudPanel = new WordCloudPanel();

        // Add the WordCloudPanel to the frame
        frame.add(wordCloudPanel);

        // Set the frame to be visible
        frame.setVisible(true);
    }
}
