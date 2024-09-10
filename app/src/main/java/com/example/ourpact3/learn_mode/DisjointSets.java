import java.util.*;

public class DisjointSets {

    public static void main(String[] args) {
        // Beispiel-Daten
        List<Set<Integer>> A = new ArrayList<>();
        List<Set<Integer>> B = new ArrayList<>();

        // Füge einige Sets zu A und B hinzu
        A.add(new HashSet<>(Arrays.asList(1, 2, 3)));
        A.add(new HashSet<>(Arrays.asList(4, 5)));
        B.add(new HashSet<>(Arrays.asList(6, 7)));
        B.add(new HashSet<>(Arrays.asList(8, 9)));

        // Berechne A2 und B2
        Set<Integer>[] result = calculateDisjointSets(A, B);
        Set<Integer> A2 = result[0];
        Set<Integer> B2 = result[1];

        // Ausgabe der Ergebnisse
        System.out.println("A2: " + A2);
        System.out.println("B2: " + B2);
    }

    public static Set<Integer>[] calculateDisjointSets(List<Set<Integer>> A, List<Set<Integer>> B) {
        Set<Integer> A2 = new HashSet<>();
        Set<Integer> B2 = new HashSet<>();

        // Füge Elemente zu A2 hinzu
        for (Set<Integer> setA : A) {
            boolean found = false;
            for (Integer element : setA) {
                // Füge ein Element hinzu, das nicht in B enthalten ist
                if (!isElementInAnySet(element, B)) {
                    A2.add(element);
                    found = true;
                    break; // Breche ab, wenn ein Element hinzugefügt wurde
                }
            }
            // Wenn kein Element gefunden wurde, füge ein neues Element hinzu
            if (!found) {
                A2.add(getUniqueElement(A, B));
            }
        }

        // Füge Elemente zu B2 hinzu
        for (Set<Integer> setB : B) {
            boolean found = false;
            for (Integer element : setB) {
                // Füge ein Element hinzu, das nicht in A enthalten ist
                if (!isElementInAnySet(element, A)) {
                    B2.add(element);
                    found = true;
                    break; // Breche ab, wenn ein Element hinzugefügt wurde
                }
            }
            // Wenn kein Element gefunden wurde, füge ein neues Element hinzu
            if (!found) {
                B2.add(getUniqueElement(A, B));
            }
        }

        return new Set[]{A2, B2}; // Rückgabe als Array von Sets
    }

    // Überprüft, ob ein Element in irgendeinem Set einer Liste enthalten ist
    private static boolean isElementInAnySet(Integer element, List<Set<Integer>> sets) {
        for (Set<Integer> set : sets) {
            if (set.contains(element)) {
                return true;
            }
        }
        return false;
    }

    // Gibt ein einzigartiges Element zurück, das nicht in den Sets der Liste enthalten ist
    private static Integer getUniqueElement(List<Set<Integer>> A, List<Set<Integer>> B) {
        Set<Integer> allElements = new HashSet<>();
        for (Set<Integer> set : A) {
            allElements.addAll(set);
        }
        for (Set<Integer> set : B) {
            allElements.addAll(set);
        }

        // Suche nach einem einzigartigen Element, das nicht in allElements enthalten ist
        int uniqueElement = 0;
        while (allElements.contains(uniqueElement)) {
            uniqueElement++;
        }
        return uniqueElement;
    }
}
