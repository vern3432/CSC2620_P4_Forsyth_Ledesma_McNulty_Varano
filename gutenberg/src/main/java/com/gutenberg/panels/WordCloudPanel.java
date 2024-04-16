package com.gutenberg.panels;

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

import com.gutenberg.cloud.WordCloudStorage;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.font.scale.SqrtFontScalar;
import com.kennycason.kumo.palette.ColorPalette;
import com.kennycason.kumo.palette.LinearGradientColorPalette;

import static com.gutenberg.Filters.*;

public class WordCloudPanel extends JPanel {
    private WordCloud wordCloud;
    private BufferedImage wordCloudImage;
    private WordCloudStorage wordCloudStorage;
    private List<WordFrequency> filteredWordFrequencies;

    private static final int DEFAULT_WIDTH = 600;
    private static final int DEFAULT_HEIGHT = 600;
    private static final int SIDEBAR_WIDTH = 200;

    // Checkboxes for filtering options
    private JCheckBox cbIngFilter;
    private JCheckBox cbOughFilter;
    private JCheckBox cbIsmFilter;
    private JCheckBox cbKnFilter;
    private JCheckBox cbAughFilter;
    private JCheckBox cbAuthorFilter;


    public WordCloudPanel(WordCloudStorage wordCloudStorage) {
        this.wordCloudStorage = wordCloudStorage;
        this.filteredWordFrequencies = new ArrayList<>();

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
        addFilterActionListener(cbIngFilter);
        addFilterActionListener(cbOughFilter);
        addFilterActionListener(cbIsmFilter);
        addFilterActionListener(cbKnFilter);
        addFilterActionListener(cbAughFilter);
        addFilterActionListener(cbAuthorFilter);

        // Add checkboxes to the side panel
        sidePanel.add(cbIngFilter);
        sidePanel.add(cbOughFilter);
        sidePanel.add(cbIsmFilter);
        sidePanel.add(cbKnFilter);
        sidePanel.add(cbAughFilter);
        sidePanel.add(cbAuthorFilter);

        return sidePanel;
    }

    private void addFilterActionListener(JCheckBox checkBox) {
        checkBox.addActionListener(e -> {
            updateFiltersAndWordCloud();
        });
    }

    private void updateFiltersAndWordCloud() {
        applyFilters();

        // Generate a unique key for the current filter state
        String filterKey = generateFilterKey();

        // Build and cache a new word cloud image for the current filter state
        setupWordCloud();

        // Repaint the panel to reflect the updated word cloud
        repaint();
    }

    private void setupInitialWordCloud() {
        // Use the cached default word cloud image
        String filterKey = generateFilterKey();
        // wordCloudImage = wordCloudCache.get(filterKey);
    }

    private void cacheDefaultWordCloud() {
        // Generate the default filter key
        String filterKey = generateFilterKey();

        setupWordCloud();
    }

    private String generateFilterKey() {
        return cbIngFilter.isSelected() + "-" + cbOughFilter.isSelected() + "-"
                + cbIsmFilter.isSelected() + "-" + cbKnFilter.isSelected() + "-"
                + cbAughFilter.isSelected() + "-" + cbAuthorFilter.isSelected();
    }

    private void applyFilters() {

        // Clear the filtered word frequencies list
        filteredWordFrequencies.clear();

        // Apply the selected filters to the word frequencies list
        wordCloudStorage.getWords().parallelStream().forEach(wordFrequency -> {
            var word = wordFrequency.getWord();
            if (cbIngFilter.isSelected() && !ingPattern.matcher(word).matches()) {
                filteredWordFrequencies.add(wordFrequency);
            } else if (cbOughFilter.isSelected() && !oughPattern.matcher(word).matches()) {
                filteredWordFrequencies.add(wordFrequency);
            } else if (cbIsmFilter.isSelected() && !ismPattern.matcher(word).matches()) {
                filteredWordFrequencies.add(wordFrequency);
            } else if (cbKnFilter.isSelected() && !knPattern.matcher(word).matches()) {
                filteredWordFrequencies.add(wordFrequency);
            } else if (cbAughFilter.isSelected() && !aughPattern.matcher(word).matches()) {
                filteredWordFrequencies.add(wordFrequency);
            } else if (cbAuthorFilter.isSelected() && !isAuthorName(word)) {
                filteredWordFrequencies.add(wordFrequency);
            }
        });
    }

    private boolean isAuthorName(String word) {
        // Determine if a word is an author's name based on whether the first letter is uppercase
        return Character.isUpperCase(word.charAt(0));
    }

    private void setupWordCloud() {
        // Create a new word cloud object with the specified dimension and collision mode
        wordCloud = new WordCloud(getPreferredSize(), CollisionMode.PIXEL_PERFECT);

        wordCloud.setPadding(2);
        wordCloud.setBackground(new CircleBackground(300));
        wordCloud.setColorPalette(new LinearGradientColorPalette(Color.RED, Color.BLUE, Color.GREEN, 30, 30));
        wordCloud.setFontScalar(new SqrtFontScalar(10, 40));

        // Build the word cloud using the filtered word frequencies
        System.out.println("********* " + this.filteredWordFrequencies.size());
        wordCloud.build(this.filteredWordFrequencies);

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