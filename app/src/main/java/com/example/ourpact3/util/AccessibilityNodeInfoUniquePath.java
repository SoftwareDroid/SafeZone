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
        // Get the resource name of the parent view
        String parentResourceName = getViewResourceName(node.getParent());

        // Get the index of the node in its parent
        int index = getIndexInParent(node);

        // Append the parent's resource name and the node's index to the path
        pathBuilder.insert(0, parentResourceName + "[" + index + "] -> ");

        // If the node has a parent, recursively build the path
        AccessibilityNodeInfo parent = node.getParent();
        if (parent != null) {
            buildPath(parent, pathBuilder);
        }
    }

    private static String getViewResourceName(AccessibilityNodeInfo node) {
        if (node == null) {
            return "";
        }

        String resourceName = null;
        try {
            resourceName = node.getViewIdResourceName();
        } catch (Exception e) {
            // Ignore exception
        }

        if (resourceName == null) {
            resourceName = node.getClassName() == null ? "" : node.getClassName().toString();
        }

        return resourceName;
    }

    private static int getIndexInParent(AccessibilityNodeInfo node) {
        if (node == null) {
            return -1;
        }

        AccessibilityNodeInfo parent = node.getParent();
        if (parent == null) {
            return -1;
        }

        int index = 0;
        for (int i = 0; i < parent.getChildCount(); i++) {
            AccessibilityNodeInfo child = parent.getChild(i);
            if (child == node) {
                index = i;
                break;
            }
        }

        return index;
    }
}