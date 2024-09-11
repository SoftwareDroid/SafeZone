package com.example.ourpact3.learn_mode;

import java.util.*;

public class DisjointSets {



    public static Set<Integer>[] myCalculateDisjointSets(List<Set<Integer>> A, List<Set<Integer>> B) {
        Map<Integer, Integer> frequencyMapA = calculateFrequencies(A);
        Map<Integer, Integer> frequencyMapB = calculateFrequencies(B);

        List<Set<Integer>> copyOfA = deepCopySets(A);
        List<Set<Integer>> copyOfB = deepCopySets(B);

        removeElements(copyOfA, frequencyMapB);
        removeElements(copyOfB, frequencyMapA);

        Set<Integer> resultA = removeAndCollect(A, frequencyMapA);
        Set<Integer> resultB = removeAndCollect(B, frequencyMapB);

        return new Set[]{resultA, resultB};
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
