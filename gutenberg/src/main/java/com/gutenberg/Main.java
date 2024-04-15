package com.gutenberg;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Create an instance of WordCloudGui and display the GUI
        SwingUtilities.invokeLater(() -> {
            new WordCloudGui().createAndShowGUI();
        });   
    
    
    }
}