package com.example.ourpact3.learn_mode;

import com.example.ourpact3.service.ScreenInfoExtractor;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private boolean needsSaving = false;

    public boolean isNeedsSaving()
    {
        return needsSaving;
    }

    public static class LabeledScreen
    {
        Set<String> ids;
        ScreenLabel label;

        public LabeledScreen(Set<String> ids, ScreenLabel label)
        {
            this.ids = ids;
            this.label = ScreenLabel.NOT_LABELED;
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

    // Method to convert AppLearnProgress object to JSON
    public JSONObject toJson() throws JSONException
    {
        JSONObject jsonObject = new JSONObject();

        // Convert labeledScreens to JSONArray
        JSONArray labeledScreensArray = new JSONArray();
        for (LabeledScreen labeledScreen : labeledScreens)
        {
            JSONObject labeledScreenJson = new JSONObject();
            labeledScreenJson.put("label", labeledScreen.label.name());
            JSONArray idsArray = new JSONArray(labeledScreen.ids);
            labeledScreenJson.put("ids", idsArray);
            labeledScreensArray.put(labeledScreenJson);
        }
        jsonObject.put("labeledScreens", labeledScreensArray);

        // Convert expressionGoodIds to JSONArray
        JSONArray goodIdsArray = new JSONArray(expressionGoodIds);
        jsonObject.put("expressionGoodIds", goodIdsArray);

        // Convert expressionBadIds to JSONArray
        JSONArray badIdsArray = new JSONArray(expressionBadIds);
        jsonObject.put("expressionBadIds", badIdsArray);

        return jsonObject;
    }

    public static AppLearnProgress fromJson(JSONObject jsonObject) throws JSONException
    {
        AppLearnProgress appLearnProgress = new AppLearnProgress();

        // Deserialize labeledScreens
        JSONArray labeledScreensArray = jsonObject.getJSONArray("labeledScreens");
        for (int i = 0; i < labeledScreensArray.length(); i++)
        {
            JSONObject labeledScreenJson = labeledScreensArray.getJSONObject(i);
            ScreenLabel label = ScreenLabel.valueOf(labeledScreenJson.getString("label"));
            JSONArray idsArray = labeledScreenJson.getJSONArray("ids");
            Set<String> ids = new TreeSet<>();
            for (int j = 0; j < idsArray.length(); j++)
            {
                ids.add(idsArray.getString(j));
            }
            appLearnProgress.labeledScreens.add(new LabeledScreen(ids, label));
        }

        // Deserialize expressionGoodIds
        JSONArray goodIdsArray = jsonObject.getJSONArray("expressionGoodIds");
        for (int i = 0; i < goodIdsArray.length(); i++)
        {
            appLearnProgress.expressionGoodIds.add(goodIdsArray.getString(i));
        }

        // Deserialize expressionBadIds
        JSONArray badIdsArray = jsonObject.getJSONArray("expressionBadIds");
        for (int i = 0; i < badIdsArray.length(); i++)
        {
            appLearnProgress.expressionBadIds.add(badIdsArray.getString(i));
        }

        return appLearnProgress;
    }

    /**
     * add a screen
     *
     * @param screen
     * @return
     */
    /*public void addScreen(@NotNull ScreenInfoExtractor.Screen screen, ScreenLabel label)
    {
        this.needsSaving = true;
        assert label != ScreenLabel.NOT_LABELED;
        Set<String> simplifiedNodes = simplifyIdNodes(screen.getIdNodes());
        LabeledScreen labeledScreen = new LabeledScreen(simplifiedNodes, label);
        this.labeledScreens.add(labeledScreen);
    }*/
    private LabeledScreen currentScreen = null;
    public void expandCurrentScreen(@NotNull ScreenInfoExtractor.Screen screen)
    {
        Set<String> newNodes = simplifyIdNodes(screen.getIdNodes());
        if(currentScreen == null)
        {
            LabeledScreen newScreen = new LabeledScreen(newNodes,ScreenLabel.NOT_LABELED);
            this.labeledScreens.add(newScreen);
            this.currentScreen = newScreen;
        } else
        {
            this.currentScreen.ids.addAll(newNodes);
        }
    }

    public void addNewScreen(@NotNull ScreenInfoExtractor.Screen screen)
    {
        Set<String> newNodes = simplifyIdNodes(screen.getIdNodes());
        LabeledScreen newScreen = new LabeledScreen(newNodes,ScreenLabel.NOT_LABELED);
        this.labeledScreens.add(newScreen);
        this.currentScreen = newScreen;
    }

    public LabeledScreen findAndSetNewCurrentScreen(@NotNull ScreenInfoExtractor.Screen screen)
    {
        Set<String> newIds = simplifyIdNodes(screen.getIdNodes());
        for(LabeledScreen screenLabeled : this.labeledScreens)
        {
            if(screenLabeled.ids.containsAll(newIds))
            {
                this.currentScreen = screenLabeled;
                return screenLabeled;
            }
        }
        return null;
    }

    public void labelCurrentScreen(ScreenLabel newLabel)
    {
        if(this.currentScreen != null)
        {
            this.currentScreen.label = newLabel;
        }
    }

    public void mergeIdenticalLabeledScreens() {
        ArrayList<LabeledScreen> uniqueScreens = new ArrayList<>();

        for (LabeledScreen currentScreen : this.labeledScreens) {
            // Überprüfen, ob currentScreen bereits in uniqueScreens vorhanden ist
            boolean isDuplicate = false;
            for (LabeledScreen uniqueScreen : uniqueScreens) {
                if (uniqueScreen.equals(currentScreen)) {
                    isDuplicate = true;
                    break; // Wenn ein Duplikat gefunden wurde, brechen Sie die Schleife ab
                }
            }
            // Wenn kein Duplikat gefunden wurde, fügen Sie es zur Liste der eindeutigen Bildschirme hinzu
            if (!isDuplicate) {
                uniqueScreens.add(currentScreen);
            }
        }

        // Aktualisieren Sie die ursprüngliche ArrayList mit den eindeutigen Bildschirmen
        this.labeledScreens = uniqueScreens;
    }

    public void recalculateExpressions()
    {
        this.needsSaving = true;
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
        this.expressionBadIds = result[1];
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
    } */

    public ScreenLabel getLabelFromCalculatedExpression(@NotNull ScreenInfoExtractor.Screen screen)
    {
        Set<String> simplifiedNodes = simplifyIdNodes(screen.getIdNodes());
        for (String goodId : this.expressionGoodIds)
        {
            if (simplifiedNodes.contains(goodId))
            {
                return ScreenLabel.GOOD;
            }
        }
        for (String badId : this.expressionBadIds)
        {
            if (simplifiedNodes.contains(badId))
            {
                return ScreenLabel.BAD;
            }
        }
        return ScreenLabel.NOT_LABELED;
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

    /*public boolean isScreenAlreadyAdded(@NotNull ScreenInfoExtractor.Screen screen)
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
    }*/

    public void saveToDisk()
    {
        this.needsSaving = false;
    }
/*
    public boolean removeScreen(@NotNull ScreenInfoExtractor.Screen screen)
    {
        this.needsSaving = true;
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
    }*/
}
