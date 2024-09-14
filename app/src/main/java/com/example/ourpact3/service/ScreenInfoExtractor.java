package com.example.ourpact3.service;

import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ScreenInfoExtractor
{
    public static class Screen
    {
        // Constructor for Screen
        public Screen(Set<ID_Node> idNodes, ArrayList<TextNode> textNodes)
        {
            this.idNodes = idNodes != null ? idNodes : new HashSet<>();
            this.textNodes = textNodes != null ? textNodes : new ArrayList<>();
        }

        public static class TextNode
        {
            public TextNode(boolean visible, boolean editable, String text)
            {
                this.visible = visible;
                this.editable = editable;
                this.text = text;
                this.textInLowerCase = text.toLowerCase();
            }

            public final boolean visible;
            public final boolean editable;
            public final String text;
            public final String textInLowerCase;
        }

        public static class ID_Node
        {
            public final String className;
            public final String id;
            public final boolean visible;

            public ID_Node(String className, String id, boolean visible)
            {
                this.className = className;
                this.id = id;
                this.visible = visible;
            }

            @NonNull
            @Override
            public String toString()
            {
                return "ID_Node [className='" + className + "', id='" + id + "', visible=" + visible + "]";
            }
        }

        private final Set<ID_Node> idNodes;
        private final ArrayList<TextNode> textNodes;


        // Public method to get an immutable list of ID_Nodes
        public Set<ID_Node> getIdNodes()
        {
            return Collections.unmodifiableSet(idNodes);
        }

        public boolean isScreenPartOfClass(Set<String> minimizedClassExpression)
        {
            for (ID_Node node : idNodes)
            {
                if (minimizedClassExpression.contains(node.id))
                {
                    return true;
                }
            }
            return false;
        }

        // Public method to get an immutable list of TextNodes
        public List<TextNode> getTextNodes()
        {
            return Collections.unmodifiableList(textNodes);
        }

        @Override
        public String toString()
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Screen [");
            for (TextNode textNode : textNodes)
            {
                sb.append("\n  Node [visible=").append(textNode.visible)
                        .append(", editable=").append(textNode.editable)
                        .append(", text='").append(textNode.text)
                        .append("', textInLowerCase='").append(textNode.textInLowerCase)
                        .append("']");
            }
            // Append ID_Nodes
            for (ID_Node idNode : idNodes)
            {
                sb.append("\n  ").append(idNode.toString());
            }
            sb.append("\n]");
            return sb.toString();
        }
    }

    public static Screen extractTextElements(AccessibilityNodeInfo node, boolean isMagnificationEnabled)
    {
        HashSet<Screen.ID_Node> idNodes = new HashSet<>();
        ArrayList<Screen.TextNode> textNodes = new ArrayList<>();

        if (node != null)
        {
            // Extract ID_Node information
            String viewId = node.getViewIdResourceName();
            String className = node.getClassName() != null ? node.getClassName().toString() : "Unknown";
            boolean isVisible = node.isVisibleToUser();

            if (viewId != null && !viewId.isEmpty())
            {
                Screen.ID_Node idNode = new Screen.ID_Node(className, viewId, isVisible);
                idNodes.add(idNode);
            }

            // Extract text information
            if (node.getText() != null && node.getText().length() > 1)
            {
                String text = node.getText().toString();
                Screen.TextNode n = new Screen.TextNode(isMagnificationEnabled || node.isVisibleToUser(), node.isEditable(), text);
                textNodes.add(n);
            }

            int childCount = node.getChildCount();
            for (int i = 0; i < childCount; i++)
            {
                AccessibilityNodeInfo childNode = node.getChild(i);
                if (childNode != null)
                {
                    Screen tempScreen = extractTextElements(childNode, isMagnificationEnabled);
                    textNodes.addAll(tempScreen.textNodes);
                    idNodes.addAll(tempScreen.idNodes); // Collect ID_Nodes from child nodes
                }
            }
        }

        return new Screen(idNodes, textNodes); // Return a new Screen with both ID_Nodes and TextNodes
    }
}