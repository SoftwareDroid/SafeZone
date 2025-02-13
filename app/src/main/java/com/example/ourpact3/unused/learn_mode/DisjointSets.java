package com.example.ourpact3.unused.learn_mode;

import java.util.*;

public class DisjointSets<T> {

    /**
     * Calculates the disjoint sets from two lists of sets of type T.
     * This method takes two lists of sets, counts the frequency of each element in both lists,
     * removes elements from the copies of the sets that are present in the other list, and collects
     * the removed elements into separate result sets for each input list.
     *
     * <p>
     * Note: Do not add duplicate elements to the sets, as this may lead to unexpected behavior
     * and errors during the calculation.
     * </p>
     *
     * @param A a list of sets of type T from which elements will be removed
     * @param B a list of sets of type T from which elements will be removed
     * @return an array of two sets, where the first set contains the elements removed from list A,
     *         and the second set contains the elements removed from list B
     */
    public Set<T>[] calculateDisjointAggregatedFrequencySets(List<Set<T>> A, List<Set<T>> B) {
        Map<T, Integer> frequencyMapA = calculateFrequencies(A);
        Map<T, Integer> frequencyMapB = calculateFrequencies(B);

        List<Set<T>> copyOfA = deepCopySets(A);
        List<Set<T>> copyOfB = deepCopySets(B);

        removeElements(copyOfA, frequencyMapB);
        removeElements(copyOfB, frequencyMapA);
        Set<T> resultA = removeAndCollect(A, frequencyMapA);
        Set<T> resultB = removeAndCollect(B, frequencyMapB);

        return new Set[]{resultA, resultB};
    }

    private void checkForDuplicates(List<Set<T>> sets) {
        for (Set<T> set : sets) {
            if (set.size() != new HashSet<>(set).size()) {
                throw new IllegalArgumentException("Duplicate elements found in set: " + set);
            }
        }
    }

    private Map<T, Integer> calculateFrequencies(List<Set<T>> sets) {
        Map<T, Integer> frequencyMap = new HashMap<>();
        for (Set<T> set : sets) {
            for (T element : set) {
                frequencyMap.put(element, frequencyMap.getOrDefault(element, 0) + 1);
            }
        }
        return frequencyMap;
    }

    private List<Set<T>> deepCopySets(List<Set<T>> original) {
        List<Set<T>> copy = new ArrayList<>();
        for (Set<T> set : original) {
            copy.add(new HashSet<>(set)); // Deep copy of each set
        }
        return copy;
    }

    private void removeElements(List<Set<T>> sets, Map<T, Integer> frequencyMap) {
        for (Set<T> set : sets) {
            set.removeIf(frequencyMap::containsKey); // Remove elements present in frequencyMap
        }
    }

    private Set<T> removeAndCollect(List<Set<T>> sets, Map<T, Integer> frequencyMap) {
        Set<T> result = new HashSet<>();
        List<Map.Entry<T, Integer>> sortedEntries = new ArrayList<>(frequencyMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        for (Map.Entry<T, Integer> entry : sortedEntries) {
            if (sets.removeIf(set -> set.contains(entry.getKey()))) {
                result.add(entry.getKey());
            }
        }
        return result;
    }
}
