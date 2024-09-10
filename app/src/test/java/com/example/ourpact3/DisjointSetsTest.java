package com.example.ourpact3;
import com.example.ourpact3.learn_mode.DisjointSets;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


import java.util.*;


public class DisjointSetsTest {

    @Test
    public void testCalculateDisjointSets() {
        // Setup larger and more complex input sets A and B
        List<Set<Integer>> A = new ArrayList<>();
        A.add(new HashSet<>(Arrays.asList(1, 2, 3, 10, 20)));
        A.add(new HashSet<>(Arrays.asList(2, 4, 5, 30)));
        A.add(new HashSet<>(Arrays.asList(3, 5, 6, 40)));
        A.add(new HashSet<>(Arrays.asList(7, 8, 9, 50)));
        A.add(new HashSet<>(Arrays.asList(10, 11, 12, 60)));
        A.add(new HashSet<>(Arrays.asList(13, 14, 15, 70)));

        List<Set<Integer>> B = new ArrayList<>();
        B.add(new HashSet<>(Arrays.asList(3, 6, 9, 80)));
        B.add(new HashSet<>(Arrays.asList(4, 7, 10, 90)));
        B.add(new HashSet<>(Arrays.asList(5, 8, 11, 100)));
        B.add(new HashSet<>(Arrays.asList(12, 13, 14, 110)));
        B.add(new HashSet<>(Arrays.asList(15, 16, 17, 120)));
        B.add(new HashSet<>(Arrays.asList(18, 19, 20, 130)));

        // Calculate disjoint sets A' and B'
        DisjointSets.Result result = DisjointSets.calculateDisjointSets(A, B);

        // Check the expected output
        assertNotNull(result);
        assertTrue(result.APrime.contains(1) || result.APrime.contains(2) || result.APrime.contains(10)); // A' should contain at least one of these
        assertTrue(result.BPrime.contains(6) || result.BPrime.contains(9) || result.BPrime.contains(80)); // B' should contain at least one of these
        assertFalse(result.APrime.stream().anyMatch(result.BPrime::contains)); // A' and B' should be disjoint
    }

    @Test
    public void testIsInAPrime() {
        // Setup larger and more complex input sets A and B
        List<Set<Integer>> A = new ArrayList<>();
        A.add(new HashSet<>(Arrays.asList(1, 2, 3, 10, 20)));
        A.add(new HashSet<>(Arrays.asList(2, 4, 5, 30)));
        A.add(new HashSet<>(Arrays.asList(3, 5, 6, 40)));
        A.add(new HashSet<>(Arrays.asList(7, 8, 9, 50)));
        A.add(new HashSet<>(Arrays.asList(10, 11, 12, 60)));
        A.add(new HashSet<>(Arrays.asList(13, 14, 15, 70)));

        List<Set<Integer>> B = new ArrayList<>();
        B.add(new HashSet<>(Arrays.asList(3, 6, 9, 80)));
        B.add(new HashSet<>(Arrays.asList(4, 7, 10, 90)));
        B.add(new HashSet<>(Arrays.asList(5, 8, 11, 100)));
        B.add(new HashSet<>(Arrays.asList(12, 13, 14, 110)));
        B.add(new HashSet<>(Arrays.asList(15, 16, 17, 120)));
        B.add(new HashSet<>(Arrays.asList(18, 19, 20, 130)));

        // Calculate disjoint sets A' and B'
        DisjointSets.Result result = DisjointSets.calculateDisjointSets(A, B);

        // Check if specific elements are in A'
        Set<Integer> elementsToCheck = new HashSet<>(Arrays.asList(2, 10, 50));
        assertTrue(result.isInAPrime(elementsToCheck)); // 2, 10, or 50 should be in A'

        elementsToCheck = new HashSet<>(Arrays.asList(100, 110));
        assertFalse(result.isInAPrime(elementsToCheck)); // No elements should be in A'
    }

    @Test
    public void testIsInBPrime() {
        // Setup larger and more complex input sets A and B
        List<Set<Integer>> A = new ArrayList<>();
        A.add(new HashSet<>(Arrays.asList(1, 2, 3, 10, 20)));
        A.add(new HashSet<>(Arrays.asList(2, 4, 5, 30)));
        A.add(new HashSet<>(Arrays.asList(3, 5, 6, 40)));
        A.add(new HashSet<>(Arrays.asList(7, 8, 9, 50)));
        A.add(new HashSet<>(Arrays.asList(10, 11, 12, 60)));
        A.add(new HashSet<>(Arrays.asList(13, 14, 15, 70)));

        List<Set<Integer>> B = new ArrayList<>();
        B.add(new HashSet<>(Arrays.asList(3, 6, 9, 80)));
        B.add(new HashSet<>(Arrays.asList(4, 7, 10, 90)));
        B.add(new HashSet<>(Arrays.asList(5, 8, 11, 100)));
        B.add(new HashSet<>(Arrays.asList(12, 13, 14, 110)));
        B.add(new HashSet<>(Arrays.asList(15, 16, 17, 120)));
        B.add(new HashSet<>(Arrays.asList(18, 19, 20, 130)));

        // Calculate disjoint sets A' and B'
        DisjointSets.Result result = DisjointSets.calculateDisjointSets(A, B);

        // Check if specific elements are in B'
        Set<Integer> elementsToCheck = new HashSet<>(Arrays.asList(6, 80, 130));
        assertTrue(result.isInBPrime(elementsToCheck)); // 6, 80, or 130 should be in B'

        elementsToCheck = new HashSet<>(Arrays.asList(1, 2, 3));
        assertFalse(result.isInBPrime(elementsToCheck)); // No elements should be in B'
    }
}