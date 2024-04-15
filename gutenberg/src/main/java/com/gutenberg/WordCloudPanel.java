package com.gutenberg;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

    // Boolean options for filters
    private boolean useIngFilter = false;
    private boolean useOughFilter = false;
    private boolean useIsmFilter = false;
    private boolean useKnFilter = false;
    private boolean useAughFilter = false;
    private boolean useAuthorFilter = false;

    // Cache to store filtered and rendered word cloud images
    private Map<String, BufferedImage> wordCloudCache = new HashMap<>();

    public WordCloudPanel(List<WordFrequency> wordFrequencies) {
        this.wordFrequencies = wordFrequencies;
        this.filteredWordFrequencies = new ArrayList<>(wordFrequencies);

        setLayout(new BorderLayout());

        // Create the side panel with filter options
        JPanel sidePanel = createSidePanel();
        add(sidePanel, BorderLayout.WEST);

        // Setup the initial word cloud
        cacheDefaultWordCloud();
        // Setup the initial word cloud using the cached default image
        setupInitialWordCloud();
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
        addFilterActionListener(cbIngFilter, () -> useIngFilter = cbIngFilter.isSelected());
        addFilterActionListener(cbOughFilter, () -> useOughFilter = cbOughFilter.isSelected());
        addFilterActionListener(cbIsmFilter, () -> useIsmFilter = cbIsmFilter.isSelected());
        addFilterActionListener(cbKnFilter, () -> useKnFilter = cbKnFilter.isSelected());
        addFilterActionListener(cbAughFilter, () -> useAughFilter = cbAughFilter.isSelected());
        addFilterActionListener(cbAuthorFilter, () -> useAuthorFilter = cbAuthorFilter.isSelected());

        // Add checkboxes to the side panel
        sidePanel.add(cbIngFilter);
        sidePanel.add(cbOughFilter);
        sidePanel.add(cbIsmFilter);
        sidePanel.add(cbKnFilter);
        sidePanel.add(cbAughFilter);
        sidePanel.add(cbAuthorFilter);

        return sidePanel;
    }

    private void addFilterActionListener(JCheckBox checkBox, Runnable action) {
        checkBox.addActionListener(e -> {
            action.run();
            updateFiltersAndWordCloud();
        });
    }

    private void updateFiltersAndWordCloud() {
        applyFilters();

        // Generate a unique key for the current filter state
        String filterKey = generateFilterKey();

        // Check if the filtered and rendered word cloud image is in the cache
        if (wordCloudCache.containsKey(filterKey)) {
            // Use the cached word cloud image
            wordCloudImage = wordCloudCache.get(filterKey);
        } else {
            // Build and cache a new word cloud image for the current filter state
            setupWordCloud();
            wordCloudCache.put(filterKey, wordCloudImage);
        }

        // Repaint the panel to reflect the updated word cloud
        repaint();
    }

    private void setupInitialWordCloud() {
        // Use the cached default word cloud image
        String filterKey = generateFilterKey();
        wordCloudImage = wordCloudCache.get(filterKey);
    }
    private void cacheDefaultWordCloud() {
        // Generate the default filter key
        String filterKey = generateFilterKey();

        // Check if the default word cloud image is not already cached
        if (!wordCloudCache.containsKey(filterKey)) {
            // Build and cache the default word cloud image
            setupWordCloud();
            wordCloudCache.put(filterKey, wordCloudImage);
        }
    }

    private String generateFilterKey() {
        return useIngFilter + "-" + useOughFilter + "-" + useIsmFilter + "-" +
               useKnFilter + "-" + useAughFilter + "-" + useAuthorFilter;
    }

    private void applyFilters() {
        // Regular expression patterns for each filter
        Pattern ingPattern = Pattern.compile(".*ing$");
        Pattern oughPattern = Pattern.compile(".*ough.*");
        Pattern ismPattern = Pattern.compile(".*ism$");
        Pattern knPattern = Pattern.compile("^kn.*");
        Pattern aughPattern = Pattern.compile(".*augh.*");

        // Clear the filtered word frequencies list
        filteredWordFrequencies.clear();

        // Apply the selected filters to the word frequencies list
        for (WordFrequency wordFrequency : wordFrequencies) {
            String word = wordFrequency.getWord();
            boolean includeWord = true;

            // Apply the filters
            if (useIngFilter && !ingPattern.matcher(word).matches()) {
                includeWord = false;
            }

            if (useOughFilter && !oughPattern.matcher(word).matches()) {
                includeWord = false;
            }

            if (useIsmFilter && !ismPattern.matcher(word).matches()) {
                includeWord = false;
            }

            if (useKnFilter && !knPattern.matcher(word).matches()) {
                includeWord = false;
            }

            if (useAughFilter && !aughPattern.matcher(word).matches()) {
                includeWord = false;
            }

            if (useAuthorFilter && !isAuthorName(word)) {
                includeWord = false;
            }

            // Add the word frequency to the filtered list if it meets the filter criteria
            if (includeWord) {
                filteredWordFrequencies.add(wordFrequency);
            }
        }
    }

    private boolean isAuthorName(String word) {
        // Determine if a word is an author's name based on whether the first letter is uppercase
        return Character.isUpperCase(word.charAt(0));
    }

    private void setupWordCloud() {
        // Define the size of the word cloud
        Dimension dimension = new Dimension(DEFAULT_WIDTH - SIDEBAR_WIDTH, DEFAULT_HEIGHT);

        // Create a new word cloud object with the specified dimension and collision mode
        wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

        // Set background, font scalar, and color palette
        wordCloud.setBackground(new CircleBackground(100));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.BLUE, Color.GREEN));

        // Build the word cloud using the filtered word frequencies
        System.out.println("Rendering new Cloud, Please Wait");
        long startTime = System.currentTimeMillis();
        wordCloud.build(filteredWordFrequencies);
        long endTime = System.currentTimeMillis();

        System.out.println("Cloud Built"+":"+"Took:"+(endTime - startTime));

        // Store the generated word cloud image
        wordCloudImage = wordCloud.getBufferedImage();
        System.out.println("Cloud Rendered");

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
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}