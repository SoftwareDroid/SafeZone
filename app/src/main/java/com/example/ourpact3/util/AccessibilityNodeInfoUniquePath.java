package com.example.ourpact3.util;

import android.view.accessibility.AccessibilityNodeInfo;
import android.util.Log;

public class AccessibilityNodeInfoUniquePath {

    public static String getUniquePath(AccessibilityNodeInfo node) {
        if (node == null) {
            return "";
        }

        StringBuilder pathBuilder = new StringBuilder();
        buildPath(node, pathBuilder);
        return pathBuilder.toString();
    }

    private static void buildPath(AccessibilityNodeInfo node, StringBuilder pathBuilder) {
        // Add the current node's class name and text (if available)
        String className = node.getClassName() != null ? "": node.getClassName().toString();
        String text = node.getText() != null ? node.getText().toString() : "";
        String contentDescription = node.getContentDescription() != null ? node.getContentDescription().toString() : "";

        // Append the current node's information to the path
        pathBuilder.insert(0, className + (text.isEmpty() ? "" : "('" + text + "')") +
                (contentDescription.isEmpty() ? "" : " [desc: '" + contentDescription + "']") + " -> ");

        // If the node has a parent, recursively build the path
        AccessibilityNodeInfo parent = node.getParent();
        if (parent != null) {
            buildPath(parent, pathBuilder);
        }
    }
}
