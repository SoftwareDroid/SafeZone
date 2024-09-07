package com.example.ourpact3.service;

import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;

public class ScreenTextExtractor
{
    public static class Screen
    {
        public static class Node
        {
            public Node(boolean visible, boolean editable, String text)
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

        public ArrayList<Node> nodes = new ArrayList<>();
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Screen [");
            for (Node node : nodes) {
                sb.append("\n  Node [visible=").append(node.visible)
                        .append(", editable=").append(node.editable)
                        .append(", text='").append(node.text)
                        .append("', textInLowerCase='").append(node.textInLowerCase)
                        .append("']");
            }
            sb.append("\n]");
            return sb.toString();
        }
    }

    public static Screen extractTextElements(AccessibilityNodeInfo node, boolean isMagnificationEnabled)
    {
        Screen screen = new Screen();
        if(node != null)
        {
            if (node.getText() != null && node.getText().length() > 1)
            {
                String text = node.getText().toString();
                Screen.Node n = new Screen.Node(isMagnificationEnabled || node.isVisibleToUser(), node.isEditable(), text);
                screen.nodes.add(n);
            }

            int childCount = node.getChildCount();
            for (int i = 0; i < childCount; i++)
            {
                AccessibilityNodeInfo childNode = node.getChild(i);
                if (childNode != null)
                {
                    Screen tempScreen = extractTextElements(childNode, isMagnificationEnabled);
                    screen.nodes.addAll(tempScreen.nodes);
                }
            }
        }
        return screen;
    }
}
