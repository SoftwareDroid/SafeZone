package com.example.ourpact3.learn_mode;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Example data
        ArrayList<ArrayList<String>> a = new ArrayList<>();
        a.add(new ArrayList<>(Arrays.asList("id1", "id2", "id3")));
        a.add(new ArrayList<>(Arrays.asList("id2", "id4", "id5")));

        ArrayList<ArrayList<String>> b = new ArrayList<>();
        b.add(new ArrayList<>(Arrays.asList("id4", "id5", "id6")));
        b.add(new ArrayList<>(Arrays.asList("id6", "id7", "id8")));

        IdMapper idMapper = new IdMapper();
        ArrayList<ArrayList<Integer>> aInt = idMapper.mapToIds(a);
        ArrayList<ArrayList<Integer>> bInt = idMapper.mapToIds(b);

        ExpressionResult result = getMinimalExpressions(aInt, bInt);
        System.out.println("Minimal expression for A:");
        for (ArrayList<Integer> orClause : result.expressionA) {
            System.out.print("(");
            for (int i = 0; i < orClause.size(); i++) {
                System.out.print(idMapper.mapToId(orClause.get(i)));
                if (i < orClause.size() - 1) {
                    System.out.print(" and ");
                }
            }
            System.out.println(")");
        }
        System.out.println("Minimal expression for B:");
        for (ArrayList<Integer> orClause : result.expressionB) {
            System.out.print("(");
            for (int i = 0; i < orClause.size(); i++) {
                System.out.print(idMapper.mapToId(orClause.get(i)));
                if (i < orClause.size() - 1) {
                    System.out.print(" and ");
                }
            }
            System.out.println(")");
        }
    }

    /**
     * Returns the minimal expressions for classes A and B.
     *
     * The minimal expressions are constructed by finding the union of all IDs in each class,
     * then finding the difference between the two unions. The resulting IDs are used to
     * construct the minimal expressions.
     *
     * @param a the lists of IDs in class A
     * @param b the lists of IDs in class B
     * @return the minimal expressions for classes A and B
     */
    public static ExpressionResult getMinimalExpressions(ArrayList<ArrayList<Integer>> a, ArrayList<ArrayList<Integer>> b) {
        // Find the union of all IDs in class A and class B
        Set<Integer> unionA = new HashSet<>();
        Set<Integer> unionB = new HashSet<>();
        for (ArrayList<Integer> ids : a) {
            unionA.addAll(ids);
        }
        for (ArrayList<Integer> ids : b) {
            unionB.addAll(ids);
        }

        // Find the union of all IDs in class A minus the union of all IDs in class B (A - B)
        Set<Integer> diffA = new HashSet<>(unionA);
        diffA.removeAll(unionB);

        // Find the union of all IDs in class B minus the union of all IDs in class A (B - A)
        Set<Integer> diffB = new HashSet<>(unionB);
        diffB.removeAll(unionA);

        // Use the result from step 2 to construct the minimal expression for A
        ArrayList<ArrayList<Integer>> expressionA = new ArrayList<>();
        for (Integer id : diffA) {
            ArrayList<Integer> orClause = new ArrayList<>();
            for (ArrayList<Integer> ids : a) {
                if (ids.contains(id)) {
                    orClause.add(id);
                }
            }
            if (!orClause.isEmpty()) {
                expressionA.add(orClause);
            }
        }

        // Use the result from step 3 to construct the minimal expression for B
        ArrayList<ArrayList<Integer>> expressionB = new ArrayList<>();
        for (Integer id : diffB) {
            ArrayList<Integer> orClause = new ArrayList<>();
            for (ArrayList<Integer> ids : b) {
                if (ids.contains(id)) {
                    orClause.add(id);
                }
            }
            if (!orClause.isEmpty()) {
                expressionB.add(orClause);
            }
        }

        return new ExpressionResult(expressionA, expressionB);
    }

    public static class ExpressionResult {
        public ArrayList<ArrayList<Integer>> expressionA;
        public ArrayList<ArrayList<Integer>> expressionB;

        public ExpressionResult(ArrayList<ArrayList<Integer>> expressionA, ArrayList<ArrayList<Integer>> expressionB) {
            this.expressionA = expressionA;
            this.expressionB = expressionB;
        }
    }

    public static class IdMapper {
        private final Map<String, Integer> stringToId = new HashMap<>();
        private final Map<Integer, String> idToString = new HashMap<>();
        private int nextId = 0;

        public ArrayList<ArrayList<Integer>> mapToIds(ArrayList<ArrayList<String>> strings) {
            ArrayList<ArrayList<Integer>> ids = new ArrayList<>();
            for (ArrayList<String> stringList : strings) {
                ArrayList<Integer> idList = new ArrayList<>();
                for (String string : stringList) {
                    idList.add(mapToId(string));
                }
                ids.add(idList);
            }
            return ids;
        }
        public ArrayList<ArrayList<String>> mapIdsToStrings(ArrayList<ArrayList<Integer>> ids) {
            ArrayList<ArrayList<String>> strings = new ArrayList<>();
            for (ArrayList<Integer> idList : ids) {
                ArrayList<String> stringList = new ArrayList<>();
                for (Integer id : idList) {
                    stringList.add(idToString.get(id));
                }
                strings.add(stringList);
            }
            return strings;
        }

        public int mapToId(String string) {
            if (!stringToId.containsKey(string)) {
                stringToId.put(string, nextId);
                idToString.put(nextId, string);
                nextId++;
            }
            return stringToId.get(string);
        }

        public String mapToId(int id) {
            return idToString.get(id);
        }
    }
}

