package com.gutenberg.dialogs;

import javax.swing.*;
import java.awt.*;

public class ProgressDialog {
    private final JDialog processingDialog;

    public ProgressDialog(JFrame parent) {
        processingDialog = new JDialog(parent, "Processing", Dialog.ModalityType.APPLICATION_MODAL);
        processingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); // Disable close button
        processingDialog.setSize(200, 100);
        processingDialog.setLocationRelativeTo(parent);

        // Create a transparent glass pane that covers the parent window
        JPanel glassPane = new JPanel();
        glassPane.setOpaque(false); // Make the glass pane transparent
        glassPane.addMouseListener(null); // Disable mouse events on the glass pane
        processingDialog.setGlassPane(glassPane); // Set the glass pane of the dialog
        glassPane.setVisible(true); // Make the glass pane visible to block interaction with the parent window

        JLabel messageLabel = new JLabel("Processing...");
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center align the message
        processingDialog.add(messageLabel, BorderLayout.CENTER); // Add label to the center

        processingDialog.setVisible(true); // Show the dialog
    }

    public void hide() {
        if (processingDialog != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    processingDialog.dispose();
                }
            });
        }
    }
}
