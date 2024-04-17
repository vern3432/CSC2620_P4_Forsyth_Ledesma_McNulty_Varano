package com.gutenberg.panels;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatusPanel extends JPanel {

    private JProgressBar progressBar;
    private Timer timer;
    private JLabel statusLabel;

    public StatusPanel() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(getWidth(), 30));
        setBackground(new Color(240, 240, 240)); // Light gray background

        progressBar = new JProgressBar();
        progressBar.setValue(100);
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(29, 161, 242)); // Blue color
        progressBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 20));
        add(progressBar, BorderLayout.CENTER);

        statusLabel = new JLabel("Scanning files (0) ...");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 12)); // Bold font
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 5)); // Add padding
        add(statusLabel, BorderLayout.WEST);
    }

    public void startAnimation() {
        this.progressBar.setIndeterminate(true);
    }
    public void stopAnimation() {
        this.progressBar.setIndeterminate(false);
    }

    public void setStatus(String status) {
        statusLabel.setText(status);
    }
}
