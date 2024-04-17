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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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


    private int wordLimit = 100; // Default limit of top 100 word frequencies


    // Cache to store filtered and rendered word cloud images
    private Map<String, BufferedImage> wordCloudCache = new HashMap<>();
    ArrayList<String> authors=new ArrayList<>();
    private Map<String, Integer> authorFrequencyMap;

    public WordCloudPanel(List<WordFrequency> wordFrequencies,ArrayList<String> authors) {
        this.authors=authors;
        this.wordFrequencies = wordFrequencies;
        this.filteredWordFrequencies = new ArrayList<>(wordFrequencies);
        authorFrequencyMap = generateAuthorFrequencyMap(authors);

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
    
    /** 
     * @param authors
     * @return Map<String, Integer>
     */
    private Map<String, Integer> generateAuthorFrequencyMap(ArrayList<String> authors) {
        Map<String, Integer> frequencyMap = new HashMap<>();
        
        for (String author : authors) {
            frequencyMap.put(author, frequencyMap.getOrDefault(author, 0) + 1);
        }
        
        return frequencyMap;
    }

    


    
    /** 
     * @return JPanel
     */
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
    
        // Add a button to change the limit value
        JButton changeLimitButton = new JButton("Change Limit (" + wordLimit + ")");
        sidePanel.add(changeLimitButton);
    
        // Add action listener for the button
        changeLimitButton.addActionListener(e -> {
            // Open a popup dialog to change the limit value
            String newLimitStr = JOptionPane.showInputDialog(
                sidePanel,
                "Enter new limit value:",
                "Change Limit",
                JOptionPane.PLAIN_MESSAGE
            );
            if (newLimitStr != null && !newLimitStr.isEmpty()) {
                try {
                    int newLimit = Integer.parseInt(newLimitStr);
                    if (newLimit > 0) {
                        wordLimit = newLimit;
                        changeLimitButton.setText("Change Limit (" + wordLimit + ")");
                        clearCacheAndRerender();
                    } else {
                        JOptionPane.showMessageDialog(
                            sidePanel,
                            "Please enter a positive integer value.",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE
                        );
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(
                        sidePanel,
                        "Please enter a valid integer value.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });
    
        return sidePanel;
    }

    private void clearCacheAndRerender() {
        // Clear the cache
        wordCloudCache.clear();
        
        // Update filters and generate a new word cloud
        updateFiltersAndWordCloud();
    }
    

    

    

    
    /** 
     * @param checkBox
     * @param action
     */
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
        if (useAuthorFilter) {
            // If author filter is selected, use the author frequency map
            filteredWordFrequencies.addAll(authorFrequencyMap.entrySet()
                    .stream()
                    .map(entry -> new WordFrequency(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList()));
        } else {
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
    }

    private boolean isAuthorName(String word) {
        // Determine if a word is an author's name based on whether the first letter is uppercase
        return Character.isUpperCase(word.charAt(0));
    }

    private void setupWordCloud() {
        // Define the size of the word cloud
        Dimension dimension = new Dimension(DEFAULT_WIDTH - SIDEBAR_WIDTH + 500, DEFAULT_HEIGHT + 500);
    
        // Create a new word cloud object with the specified dimension and collision mode
        wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);
    
        // Set background, font scalar, and color palette
        wordCloud.setBackground(new CircleBackground(400));
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));
        wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.BLUE, Color.GREEN));
    
        // Create a loading dialog with a spinner
        JDialog loadingDialog = new JDialog((JFrame) null, "Loading...", true);
        loadingDialog.setLayout(new BorderLayout());
        loadingDialog.setSize(300, 100);
        loadingDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    
        JLabel messageLabel = new JLabel("Rendering new Cloud, Please Wait...", SwingConstants.CENTER);
        loadingDialog.add(messageLabel, BorderLayout.NORTH);
    
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        loadingDialog.add(progressBar, BorderLayout.CENTER);
    
        // Show the loading dialog on the EDT
        SwingUtilities.invokeLater(() -> loadingDialog.setVisible(true));
    
        // Create an executor to run the build process in a background thread
        ExecutorService executor = Executors.newSingleThreadExecutor();
    
        // Submit the build process to the executor
        executor.submit(() -> {
            try {
                long startTime = System.currentTimeMillis();
                
                // Build the word cloud using the filtered word frequencies
                List<WordFrequency> top100Frequencies = new TopWordFrequenciesReducer().reduce(filteredWordFrequencies,this.wordLimit);

                wordCloud.build(top100Frequencies);
                
                // Store the generated word cloud image
                wordCloudImage = wordCloud.getBufferedImage();
    
                // Calculate the duration of the word cloud build process
                long endTime = System.currentTimeMillis();
                System.out.println("Cloud Built: Took: " + (endTime - startTime) + " ms");
                System.out.println("Cloud Rendered");
    
                // Generate the filter key for caching
                String filterKey = generateFilterKey();
                
                // Add the rendered word cloud image to the cache
                wordCloudCache.put(filterKey, wordCloudImage);
            } catch (Exception e) {
                System.err.println("Error during word cloud setup: " + e.getMessage());
                e.printStackTrace();
            } finally {
                // Close the loading dialog on the EDT
                SwingUtilities.invokeLater(() -> loadingDialog.dispose());
    
                // Repaint the panel to reflect the updated word cloud on the EDT
                SwingUtilities.invokeLater(() -> repaint());
    
                // Shut down the executor
                executor.shutdown();
            }
        });
    }
    
    public void reloadData(String folderPath) {

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