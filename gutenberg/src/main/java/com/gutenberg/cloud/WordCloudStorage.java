package com.gutenberg.cloud;

import com.kennycason.kumo.WordFrequency;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class WordCloudStorage {
    private final ConcurrentMap<String, Integer> wordFrequencyMap;

    public WordCloudStorage() {
        this.wordFrequencyMap = new ConcurrentHashMap<>();
    }

    public void addWords(ConcurrentMap<String, Integer> newWords) {
        for(Map.Entry<String, Integer> entry : newWords.entrySet()) {
            wordFrequencyMap.merge(entry.getKey(), entry.getValue(), Integer::sum);
        }
    }

    public List<WordFrequency> getWords() {
        return wordFrequencyMap.entrySet().parallelStream()
                .map(entry -> new WordFrequency(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

}
