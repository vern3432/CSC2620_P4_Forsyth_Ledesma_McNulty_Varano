package com.gutenberg;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

import com.kennycason.kumo.WordFrequency;

public class TopWordFrequenciesReducer {
    private static final int MAX_THREADS = Runtime.getRuntime().availableProcessors();

    
    /** 
     * @param filteredWordFrequencies
     * @return List<WordFrequency>
     */
    public List<WordFrequency> reduce(List<WordFrequency> filteredWordFrequencies) {
        // If the list has 100 or fewer elements, return it as is
        if (filteredWordFrequencies.size() <= 100) {
            return new ArrayList<>(filteredWordFrequencies);
        }

        // Create a ForkJoinPool with the maximum number of threads
        ForkJoinPool pool = new ForkJoinPool(MAX_THREADS);

        // Sort the list using a multi-threaded merge sort and retrieve the top 100 results
        List<WordFrequency> sortedList = pool.invoke(new MergeSortTask(filteredWordFrequencies));
        
        // Close the pool
        pool.shutdown();

        // Return the top 100 results
        return sortedList.subList(0, 100);
    }

    // RecursiveTask for the multi-threaded merge sort
    private static class MergeSortTask extends RecursiveTask<List<WordFrequency>> {
        private static final long serialVersionUID = 1L;
        private final List<WordFrequency> list;

        public MergeSortTask(List<WordFrequency> list) {
            this.list = list;
        }

        @Override
        protected List<WordFrequency> compute() {
            // If the list size is 1 or less, return it as is
            if (list.size() <= 1) {
                return list;
            }

            // Divide the list into two halves
            int mid = list.size() / 2;
            MergeSortTask leftTask = new MergeSortTask(list.subList(0, mid));
            MergeSortTask rightTask = new MergeSortTask(list.subList(mid, list.size()));

            // Fork the left task
            leftTask.fork();

            // Compute the right task
            List<WordFrequency> rightResult = rightTask.compute();

            // Join the left task and get the left result
            List<WordFrequency> leftResult = leftTask.join();

            // Merge the results of the left and right tasks
            return merge(leftResult, rightResult);
        }

        // Method to merge two sorted lists
        private List<WordFrequency> merge(List<WordFrequency> left, List<WordFrequency> right) {
            List<WordFrequency> merged = new ArrayList<>();
            int i = 0, j = 0;

            while (i < left.size() && j < right.size()) {
                if (left.get(i).getFrequency() >= right.get(j).getFrequency()) {
                    merged.add(left.get(i));
                    i++;
                } else {
                    merged.add(right.get(j));
                    j++;
                }
            }

            // Add remaining elements from left and right lists
            while (i < left.size()) {
                merged.add(left.get(i));
                i++;
            }
            while (j < right.size()) {
                merged.add(right.get(j));
                j++;
            }

            return merged;
        }
    }
}
