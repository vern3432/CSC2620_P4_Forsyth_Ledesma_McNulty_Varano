package com.gutenberg;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;

import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.bg.RectangleBackground;
import com.kennycason.kumo.CollisionMode;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.palette.ColorPalette;

public class WordCloudPanel extends JPanel {
    private WordCloud wordCloud;
    private BufferedImage wordCloudImage;

    public WordCloudPanel(List<WordFrequency> wordFrequencies) {
        // Define the size of the word cloud
        Dimension dimension = new Dimension(400, 300);

        // Create a word cloud object with specified dimension and collision mode
        wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

        // Set the background to a circle with radius of 100
        wordCloud.setBackground(new CircleBackground(100));

        // Set the font scalar for font size scaling
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));

        // Set the color palette for word colors
        wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.BLUE, Color.GREEN));

        // Build the word cloud using the list of word frequencies
        wordCloud.build(wordFrequencies);

        // Store the generated word cloud image
        wordCloudImage = wordCloud.getBufferedImage();
    }
    public WordCloudPanel() {
        // Define the size of the word cloud
        Dimension dimension = new Dimension(400, 300);

        // Create a word cloud object with specified dimension and collision mode
        wordCloud = new WordCloud(dimension, CollisionMode.PIXEL_PERFECT);

        // Set the background to a circle with radius of 100
        wordCloud.setBackground(new CircleBackground(100));

        // Set the font scalar for font size scaling
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));

        // Set the color palette for word colors
        wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.BLUE, Color.GREEN));

        // Define a list of word frequencies (example words with frequencies)
        List<WordFrequency> wordFrequencies = new ArrayList<>();
        wordFrequencies.add(new WordFrequency("Java", 50));
        wordFrequencies.add(new WordFrequency("Programming", 40));
        wordFrequencies.add(new WordFrequency("Threading", 30));
        wordFrequencies.add(new WordFrequency("Cloud", 20));
        wordFrequencies.add(new WordFrequency("Regular", 15));
        wordFrequencies.add(new WordFrequency("Expression", 10));

        // Build the word cloud using the list of word frequencies
        wordCloud.build(wordFrequencies);

        // Store the generated word cloud image
        wordCloudImage = wordCloud.getBufferedImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw the word cloud image onto the panel
        if (wordCloudImage != null) {
            g2d.drawImage(wordCloudImage, 0, 0, this);
        }
    }

    
}
