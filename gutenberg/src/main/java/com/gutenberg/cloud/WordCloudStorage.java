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
    private final ConcurrentMap<String, Integer> authorsFrequencyMap;

    /**
     * Constructs a new WordCloudStorage object with an empty word frequency map.
     */
    public WordCloudStorage() {
        this.wordFrequencyMap = new ConcurrentHashMap<>();
        this.authorsFrequencyMap = new ConcurrentHashMap<>();
    }

    /**
     * Adds new words and their frequencies to the word frequency map.
     * If a word already exists in the map, its frequency is updated by adding the new frequency to the existing one.
     *
     *  @param source A map containing new words and their frequencies.
     */
    public void addWords(ConcurrentMap<String, Integer> source) {
        for(Map.Entry<String, Integer> entry : source.entrySet()) {
            wordFrequencyMap.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    /**
     * Adds new authors and their frequencies to the author frequency map.
     * If a author already exists in the map, its frequency is updated by adding the new frequency to the existing one.
     *
     *  @param source A map containing new authors and their frequencies.
     */
    public void addAuthors(ConcurrentMap<String, Integer> source) {
        for(Map.Entry<String, Integer> entry : source.entrySet()) {
            authorsFrequencyMap.merge(entry.getKey(), entry.getValue(), Integer::sum);
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

    /**
     * Retrieves all authors and their frequencies stored in the author frequency map.
     * Converts the word frequency map to a list of WordFrequency objects.
     *
     * @return A list of WordFrequency objects representing authors and their frequencies.
     */
    public List<WordFrequency> getAuthors() {
        return authorsFrequencyMap.entrySet().parallelStream()
                .map(entry -> new WordFrequency(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
