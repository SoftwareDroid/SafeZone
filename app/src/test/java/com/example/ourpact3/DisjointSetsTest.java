package com.example.ourpact3;

import com.example.ourpact3.learn_mode.DisjointSets;

import org.junit.Test;

import static org.junit.Assert.*;


import java.util.*;


public class DisjointSetsTest
{

    @Test
    public void testMyCalculateDisjointSets()
    {
        // Prepare input data
        List<Set<Integer>> A = new ArrayList<>();
        A.add(new HashSet<>(Arrays.asList(5, 1, 7, 3, 0))); // Set 1
        A.add(new HashSet<>(Arrays.asList(3, 5, 4)));      // Set 2
        A.add(new HashSet<>(Arrays.asList(1, 8, 9, 42, 100))); // Set 3

        List<Set<Integer>> B = new ArrayList<>();
        B.add(new HashSet<>(Arrays.asList(11, 9, 8, 3))); // Set 1
        B.add(new HashSet<>(Arrays.asList(1, 0, 4 ,2)));     // Set 2
        B.add(new HashSet<>(Arrays.asList(2, 10,11)));        // Set 3

        Set<Integer>[] result = DisjointSets.calculateDisjointAggregatedFrequencySets(A, B);
        assertTrue(result[0].contains(1));
        assertTrue(result[0].contains(3) || result[0].contains(5)  );
        assertTrue(result[1].contains(11));
        assertTrue(result[1].contains(2));

        assertEquals(result[0].size(), 2);
        assertEquals(result[1].size(), 2);

    }

}