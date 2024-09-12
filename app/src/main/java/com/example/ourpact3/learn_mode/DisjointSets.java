package com.example.ourpact3.learn_mode;

import java.util.*;

public class DisjointSets {

    /**
     * Do not add double elements as set this results on errors
     * Calculates the disjoint sets from two lists of sets of integers.
     *  with n integer approximately O(NlogN) in the worst case, and the space complexity is O(N).
     * This method takes two lists of sets, counts the frequency of each integer in both lists,
     * removes elements from the copies of the sets that are present in the other list, and collects
     * the removed elements into separate result sets for each input list.
     *
     * <p>
     * Note: Do not add duplicate elements to the sets, as this may lead to unexpected behavior
     * and errors during the calculation.
     * </p>
     *
     * @param A a list of sets of integers from which elements will be removed
     * @param B a list of sets of integers from which elements will be removed
     * @return an array of two sets, where the first set contains the elements removed from list A,
     *         and the second set contains the elements removed from list B
     */
    public static Set<Integer>[] calculateDisjointAggregatedFrequencySets(List<Set<Integer>> A, List<Set<Integer>> B) {
        Map<Integer, Integer> frequencyMapA = calculateFrequencies(A);
        Map<Integer, Integer> frequencyMapB = calculateFrequencies(B);

        List<Set<Integer>> copyOfA = deepCopySets(A);
        List<Set<Integer>> copyOfB = deepCopySets(B);

        removeElements(copyOfA, frequencyMapB);
        removeElements(copyOfB, frequencyMapA);sc

        Set<Integer> resultA = removeAndCollect(A, frequencyMapA);
        Set<Integer> resultB = removeAndCollect(B, frequencyMapB);

        return new Set[]{resultA, resultB};
    }

    private static void checkForDuplicates(List<Set<Integer>> sets) {
        for (Set<Integer> set : sets) {
            if (set.size() != new HashSet<>(set).size()) {
                throw new IllegalArgumentException("Duplicate elements found in set: " + set);
            }
        }
    }

    private static Map<Integer, Integer> calculateFrequencies(List<Set<Integer>> sets) {
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (Set<Integer> set : sets) {
            for (Integer num : set) {
                frequencyMap.put(num, frequencyMap.getOrDefault(num, 0) + 1);
            }
        }
        return frequencyMap;
    }

    private static List<Set<Integer>> deepCopySets(List<Set<Integer>> original) {
        List<Set<Integer>> copy = new ArrayList<>();
        for (Set<Integer> set : original) {
            copy.add(new HashSet<>(set)); // Deep copy of each set
        }
        return copy;
    }

    private static void removeElements(List<Set<Integer>> sets, Map<Integer, Integer> frequencyMap) {
        for (Set<Integer> set : sets) {
            set.removeIf(frequencyMap::containsKey); // Remove elements present in frequencyMap
        }
    }

    private static Set<Integer> removeAndCollect(List<Set<Integer>> sets, Map<Integer, Integer> frequencyMap) {
        Set<Integer> result = new HashSet<>();
        List<Map.Entry<Integer, Integer>> sortedEntries = new ArrayList<>(frequencyMap.entrySet());
        sortedEntries.sort((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()));

        for (Map.Entry<Integer, Integer> entry : sortedEntries) {
            if (sets.removeIf(set -> set.contains(entry.getKey()))) {
                result.add(entry.getKey());
            }
        }
        return result;
    }



}
