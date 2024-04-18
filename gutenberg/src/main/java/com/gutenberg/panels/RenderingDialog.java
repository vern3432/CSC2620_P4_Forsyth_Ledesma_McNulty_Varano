package com.gutenberg.panels;

import javax.swing.*;
import java.awt.*;


/**
 * The RenderingDialog class represents a dialog for displaying a rendering progress.
 * It contains a progress bar to indicate the progress of rendering.
 */
public class RenderingDialog {

    private final JDialog dialog;
    private final JProgressBar progressBar;

    /**
     * Constructs a RenderingDialog with the specified parent frame.
     *
     * @param parent The parent frame to which the dialog is attached.
     */
    public RenderingDialog(JFrame parent) {
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

    /**
     * Shows the rendering dialog.
     */
    public void show() {
        this.progressBar.setIndeterminate(true);
        this.dialog.setVisible(true);
    }

    /**
     * Hides the rendering dialog.
     */
    public void hide() {
        this.progressBar.setIndeterminate(false);
        this.dialog.dispose();
    }

}
