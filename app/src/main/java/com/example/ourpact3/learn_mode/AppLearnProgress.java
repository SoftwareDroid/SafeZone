package com.example.ourpact3.learn_mode;

import com.example.ourpact3.service.ScreenInfoExtractor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class AppLearnProgress
{
    public enum ScreenLabel
    {
        NOT_LABELED,
        GOOD,
        BAD,
    }

    public static class LabeledScreen
    {
        Set<String> ids;
        ScreenLabel label;

        public LabeledScreen(Set<String> ids, ScreenLabel label)
        {
            this.ids = ids;
            this.label = label;
        }

        public Set<String> getIds()
        {
            return ids;
        }

        public ScreenLabel getLabel()
        {
            return label;
        }
    }

    private ArrayList<LabeledScreen> labeledScreens = new ArrayList<>();
    private Set<String> expressionGoodIds = new TreeSet<>();
    private Set<String> expressionBadIds = new TreeSet<>();

    /**
     * add a screen
     *
     * @param screen
     * @return
     */
    public void addScreen(@NotNull ScreenInfoExtractor.Screen screen, ScreenLabel label)
    {
        assert label != ScreenLabel.NOT_LABELED;
        Set<String> simplifiedNodes = simplifyIdNodes(screen.getIdNodes());
        LabeledScreen labeledScreen = new LabeledScreen(simplifiedNodes, label);
        this.labeledScreens.add(labeledScreen);
    }

    public void recalculateExpressions()
    {
        ArrayList<Set<String>> classGood = new ArrayList<>();
        ArrayList<Set<String>> classBad = new ArrayList<>();
        for (LabeledScreen screen : this.labeledScreens)
        {
            if (screen.label == ScreenLabel.GOOD)
            {
                classGood.add(screen.ids);
            } else if (screen.label == ScreenLabel.BAD)
            {
                classBad.add(screen.ids);
            }
        }
        DisjointSets<String> disjointSets = new DisjointSets<>();
        Set<String>[] result = disjointSets.calculateDisjointAggregatedFrequencySets(classGood, classBad);
        this.expressionGoodIds = result[0];
        this.expressionBadIds = result[0];
    }

    public Set<String> getExpressionGoodIds()
    {
        return Collections.unmodifiableSet(this.expressionGoodIds);
    }

    public Set<String> getExpressionBadIds()
    {
        return Collections.unmodifiableSet(this.expressionGoodIds);
    }

    /**
     * Retrieve the LabeledScreen associated with the given screen.
     *
     * @param screen The screen to look for.
     * @return The corresponding LabeledScreen, or null if not found.
     */
    public LabeledScreen getLabeledScreen(@NotNull ScreenInfoExtractor.Screen screen)
    {
        Set<ScreenInfoExtractor.Screen.ID_Node> idNodes = screen.getIdNodes();
        Set<String> simplifiedIds = simplifyIdNodes(idNodes);

        for (LabeledScreen labeledScreen : labeledScreens)
        {
            // Check if the IDs match
            if (!labeledScreen.ids.isEmpty() && labeledScreen.ids.equals(simplifiedIds))
            {
                return labeledScreen; // Return the found LabeledScreen
            }
        }
        return null; // Return null if no match is found
    }

    public void clear()
    {
        this.labeledScreens.clear();
        this.expressionGoodIds.clear();
        this.expressionBadIds.clear();
    }

    private static Set<String> simplifyIdNodes(Set<ScreenInfoExtractor.Screen.ID_Node> nodes)
    {
        Set<String> returnSet = new TreeSet<>();
        for (ScreenInfoExtractor.Screen.ID_Node n : nodes)
        {
            returnSet.add(n.id);
        }
        return returnSet;
    }

    public boolean isScreenAlreadyAdded(@NotNull ScreenInfoExtractor.Screen screen)
    {
        Set<ScreenInfoExtractor.Screen.ID_Node> idNodes = screen.getIdNodes();
        if (!idNodes.isEmpty())
        {
            for (LabeledScreen s : labeledScreens)
            {
                if (s.ids.equals(simplifyIdNodes(idNodes)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean removeScreen(@NotNull ScreenInfoExtractor.Screen screen)
    {
        Set<ScreenInfoExtractor.Screen.ID_Node> idNodes = screen.getIdNodes();
        Set<String> simplifiedIds = simplifyIdNodes(idNodes);

        // Iterate through the labeledScreens to find the matching one
        for (LabeledScreen labeledScreen : labeledScreens)
        {
            // Check if the IDs match
            if (!labeledScreen.ids.isEmpty() && labeledScreen.ids.equals(simplifiedIds))
            {
                labeledScreens.remove(labeledScreen); // Remove the found LabeledScreen
                return true; // Return true indicating successful removal
            }
        }
        return false; // Return false if no matching LabeledScreen was found
    }
}
