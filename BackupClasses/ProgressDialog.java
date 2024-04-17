package com.gutenberg;

import javax.swing.*;

public class ProgressDialog extends JDialog {
    private JProgressBar progressBar;

    public ProgressDialog(Frame parent, String title) {
        super(parent, title, true); // Create a modal dialog
        setLayout(new BorderLayout());
        progressBar = new JProgressBar(0, 100);
        add(progressBar, BorderLayout.CENTER);
        pack(); // Adjust size based on components
    }

    
    /** 
     * @param value
     */
    public void setProgress(int value) {
        progressBar.setValue(value);
    }
}
