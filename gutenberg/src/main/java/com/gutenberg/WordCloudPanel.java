package com.gutenberg;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JPanel;
import com.kennycason.kumo.WordCloud;
import com.kennycason.kumo.WordFrequency;
import com.kennycason.kumo.bg.CircleBackground;
import com.kennycason.kumo.font.scale.LinearFontScalar;
import com.kennycason.kumo.palette.ColorPalette;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class WordCloudPanel extends JPanel {
    private WordCloud wordCloud;
    private BufferedImage wordCloudImage;

    public WordCloudPanel() {
        // Define the size of the word cloud
        int width = 400;
        int height = 300;

        // Create a word cloud object
        wordCloud = new WordCloud(width, height, new CircleBackground(100));

        // Set the font scaler
        wordCloud.setFontScalar(new LinearFontScalar(10, 40));

        // Set the color palette
        wordCloud.setColorPalette(new ColorPalette(Color.RED, Color.BLUE, Color.GREEN));

        // Define some example words with their frequencies
        List<WordFrequency> wordFrequencies = new ArrayList<>();
        wordFrequencies.add(new WordFrequency("Java", 50));
        wordFrequencies.add(new WordFrequency("Programming", 40));
        wordFrequencies.add(new WordFrequency("Threading", 30));
        wordFrequencies.add(new WordFrequency("Cloud", 20));
        wordFrequencies.add(new WordFrequency("Regular", 15));
        wordFrequencies.add(new WordFrequency("Expression", 10));

        // Build the word cloud
        wordCloud.build(wordFrequencies);

        // Get the word cloud image
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
