package com.gutenberg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.palette.ColorPalette;

public class WordCloudPanel extends JPanel {
    private WordCloud wordCloud;
    private BufferedImage wordCloudImage;
    private List<WordFrequency> wordFrequencies;
    private List<WordFrequency> filteredWordFrequencies;

    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 400;
    private static final int SIDEBAR_WIDTH = 200;

    // Checkboxes for filtering options
    private JCheckBox cbIngFilter;
    private JCheckBox cbOughFilter;
    private JCheckBox cbIsmFilter;
    private JCheckBox cbKnFilter;
    private JCheckBox cbAughFilter;
    private JCheckBox cbAuthorFilter;
    private JProgressBar progressBar;


    // Boolean options for filters
    private boolean useIngFilter = false;
    private boolean useOughFilter = false;
    private boolean useIsmFilter = false;
    private boolean useKnFilter = false;
    private boolean useAughFilter = false;
    private boolean useAuthorFilter = false;

    public WordCloudPanel(List<WordFrequency> wordFrequencies) {
        this.wordFrequencies = wordFrequencies;
        this.filteredWordFrequencies = new ArrayList<>(wordFrequencies);

        setLayout(new BorderLayout());

        // Create the side panel with filter options
        JPanel sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.WEST);

        // Set up the word cloud panel and generate the word cloud
        setupWordCloud();
        // add(new JLabel(new ImageIcon(wordCloudImage)), BorderLayout.CENTER);
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(SIDEBAR_WIDTH, DEFAULT_HEIGHT));
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));

        // Initialize checkboxes for filtering options
        cbIngFilter = new JCheckBox("Words ending in 'ing'");
        cbOughFilter = new JCheckBox("Words containing 'ough'");
        cbIsmFilter = new JCheckBox("Words ending in 'ism'");
        cbKnFilter = new JCheckBox("Words starting with 'kn'");
        cbAughFilter = new JCheckBox("Words containing 'augh'");
        cbAuthorFilter = new JCheckBox("Author's names");

        // Add action listeners to the checkboxes
        cbIngFilter.addActionListener(e -> {
            useIngFilter = cbIngFilter.isSelected();
            updateFiltersAndWordCloud();
        });

        cbOughFilter.addActionListener(e -> {
            useOughFilter = cbOughFilter.isSelected();
            updateFiltersAndWordCloud();
        });

        cbIsmFilter.addActionListener(e -> {
            useIsmFilter = cbIsmFilter.isSelected();
            updateFiltersAndWordCloud();
        });

        cbKnFilter.addActionListener(e -> {
            useKnFilter = cbKnFilter.isSelected();
            updateFiltersAndWordCloud();
        });

        cbAughFilter.addActionListener(e -> {
            useAughFilter = cbAughFilter.isSelected();
            updateFiltersAndWordCloud();
        });

        cbAuthorFilter.addActionListener(e -> {
            useAuthorFilter = cbAuthorFilter.isSelected();
            updateFiltersAndWordCloud();
        });

        // Add checkboxes to the side panel
        sidePanel.add(cbIngFilter);
        sidePanel.add(cbOughFilter);
        sidePanel.add(cbIsmFilter);
        sidePanel.add(cbKnFilter);
        sidePanel.add(cbAughFilter);
        sidePanel.add(cbAuthorFilter);

        return sidePanel;
    }

    private void updateFiltersAndWordCloud() {
        // Apply filters based on the selected checkboxes
        applyFilters();

        // Rebuild the word cloud with filtered word frequencies
        setupWordCloud();

        // Repaint the panel to reflect the updated word cloud
        repaint();
    }

    private void applyFilters() {
        // Apply filtering logic based on selected checkboxes
        filteredWordFrequencies.clear();

        for (WordFrequency wordFrequency : wordFrequencies) {
            String word = wordFrequency.getWord();
            boolean includeWord = true;

            if (useIngFilter && !word.endsWith("ing")) {
                includeWord = false;
            }

            if (useOughFilter && !word.contains("ough")) {
                includeWord = false;
            }

            if (useIsmFilter && !word.endsWith("ism")) {
                includeWord = false;
            }

            if (useKnFilter && !word.startsWith("kn")) {
                includeWord = false;
            }

            if (useAughFilter && !word.contains("augh")) {
                includeWord = false;
            }

            if (useAuthorFilter && !isAuthorName(word)) {
                includeWord = false;
            }

            if (includeWord) {
                filteredWordFrequencies.add(wordFrequency);
            }
        }
    }

    private boolean isAuthorName(String word) {
        // This method should implement logic to determine if a word is an author's name
        // You can customize this logic as needed, using regex or other approaches
        return Character.isUpperCase(word.charAt(0));
    }
    public void WordCloudProgressBar() {
        // Initialize the progress bar
        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
    }

    public void setupWordCloud() {
        // Define the size of the word cloud
        Dimension dimension = new Dimension(DEFAULT_WIDTH - SIDEBAR_WIDTH, DEFAULT_HEIGHT);

        // Create a new word cloud object with the updated dimension and collision mode
        wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
        
        // Set background, font scalar, and color palette as before
        wordCloud.setBackground(new CircleBackground(100));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.BLUE, Color.GREEN));

        // Run the long-running build task in a separate thread
        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() {
                // Build the word cloud using the filtered list of word frequencies
                for (int i = 0; i < filteredWordFrequencies.size(); i++) {
                    // Build a part of the word cloud here
                    // Use the loop index to simulate progress
                    int progress = (int) (((double) i / filteredWordFrequencies.size()) * 100);
                    publish(progress);
                }
                wordCloud.build(filteredWordFrequencies);
                return null;
            }

            @Override
            protected void process(List<Integer> chunks) {
                // Update the progress bar with the latest progress
                progressBar.setValue(chunks.get(chunks.size() - 1));
            }

            @Override
            protected void done() {
                // Once the task is done, retrieve the generated word cloud image
                wordCloudImage = wordCloud.getBufferedImage();
                progressBar.setValue(100);
                progressBar.setString("Complete");
            }
        };

        // Execute the worker
        worker.execute();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw the word cloud image onto the panel
        if (wordCloudImage != null) {
            g2d.drawImage(wordCloudImage, SIDEBAR_WIDTH, 0, this);
        }

        // Draw the sidebar
        drawSidebar(g2d);
    }

    private void drawSidebar(Graphics2D g2d) {
        // Draw the sidebar background
        g2d.setColor(Color.LIGHT_GRAY);
        g2d.fillRect(0, 0, SIDEBAR_WIDTH, getHeight());

        // Customize the sidebar as needed (labels, additional components, etc.)
        g2d.setColor(Color.BLACK);
        g2d.drawString("Filter Options:", 10, 20);

        // Add any additional customization here
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
