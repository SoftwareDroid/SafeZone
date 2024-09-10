package com.example.ourpact3.learn_mode;
/*
import static com.example.ourpact3.learn_mode.Main.ExpressionCalculator.getMinimalExpressions;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        // Beispiel-Daten
        Set<String> a = new HashSet<>(Arrays.asList("id1", "id2", "id3"));
        Set<String> b = new HashSet<>(Arrays.asList("id2", "id4", "id5"));

        IdMapper idMapper = new IdMapper();
        Set<Integer> aInt = idMapper.mapToIds(a);
        Set<Integer> bInt = idMapper.mapToIds(b);

        ExpressionResult result = getMinimalExpressions(aInt, bInt);
        System.out.println("Minimale Ausdrücke für A:");
        for (List<Integer> orClause : result.expressionA) {
            System.out.print("(");
            for (int i = 0; i < orClause.size(); i++) {
                System.out.print(idMapper.mapToId(orClause.get(i)));
                if (i < orClause.size() - 1) {
                    System.out.print(" and ");
                }
            }
            System.out.println(")");
        }
        System.out.println("Minimale Ausdrücke für B:");
        for (List<Integer> orClause : result.expressionB) {
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

    public static boolean matchesExpression(Set<Integer> set, List<List<Integer>> expression) {
        for (List<Integer> orClause : expression) {
            boolean matchesOrClause = true;
            for (Integer id : orClause) {
                if (!set.contains(id)) {
                    matchesOrClause = false;
                    break;
                }
            }
            if (matchesOrClause) {
                return true;
            }
        }
        return false;
    }


    public static class IdMapper {
        private final Map<String, Integer> stringToId = new HashMap<>();
        private final Map<Integer, String> idToString = new HashMap<>();
        private int nextId = 0;

        public Set<Integer> mapToIds(Set<String> strings) {
            Set<Integer> ids = new HashSet<>();
            for (String string : strings) {
                ids.add(mapToId(string));
            }
            return ids;
        }

        public Set<String> mapIdsToStrings(Set<Integer> ids) {
            Set<String> strings = new HashSet<>();
            for (Integer id : ids) {
                strings.add(idToString.get(id));
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
*/