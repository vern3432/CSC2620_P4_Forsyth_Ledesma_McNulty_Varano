package com.gutenberg.cloud;

import com.kennycason.kumo.WordFrequency;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * A class to store word frequencies for generating word clouds.
 * Words are stored in a ConcurrentHashMap to allow concurrent access and modification.
 *
 * @author Maddie
 */
public class WordCloudStorage {
    private final ConcurrentMap<String, Integer> wordFrequencyMap;

    /**
     * Constructs a new WordCloudStorage object with an empty word frequency map.
     */
    public WordCloudStorage() {
        this.wordFrequencyMap = new ConcurrentHashMap<>();
    }

    /**
     * Adds new words and their frequencies to the word frequency map.
     * If a word already exists in the map, its frequency is updated by adding the new frequency to the existing one.
     *
     *  @param newWords A map containing new words and their frequencies.
     */
    public void addWords(ConcurrentMap<String, Integer> newWords) {
        for(Map.Entry<String, Integer> entry : newWords.entrySet()) {
            wordFrequencyMap.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    /**
     * Retrieves all words and their frequencies stored in the word frequency map.
     * Converts the word frequency map to a list of WordFrequency objects.
     *
     * @return A list of WordFrequency objects representing words and their frequencies.
     */
    public List<WordFrequency> getWords() {
        return wordFrequencyMap.entrySet().parallelStream()
                .map(entry -> new WordFrequency(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
