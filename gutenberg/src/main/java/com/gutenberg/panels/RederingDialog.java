package com.gutenberg.panels;

import javax.swing.*;
import java.awt.*;

public class RederingDialog {

    private final JDialog dialog;
    private final JProgressBar progressBar;

    public RederingDialog(JFrame parent) {
        this.dialog = new JDialog(parent, "Processing", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Disable close button
        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(parent);
        dialog.setUndecorated(true);

        JLabel messageLabel = new JLabel("Rendering...");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align the message

        progressBar = new JProgressBar();
        progressBar.setValue(0);
        progressBar.setStringPainted(false);
        progressBar.setForeground(new Color(29, 161, 242)); // Blue color
        progressBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        dialog.add(messageLabel, BorderLayout.CENTER);
    }

    public void show() {
        this.progressBar.setIndeterminate(true);
        this.dialog.setVisible(true);
    }

    public void hide() {
        this.progressBar.setIndeterminate(false);
        this.dialog.dispose();
    }

}
