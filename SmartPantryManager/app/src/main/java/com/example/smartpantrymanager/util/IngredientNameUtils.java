package com.example.smartpantrymanager.util;

/**
 * Normalises raw ingredient names so that trivial real-world differences
 * (casing, whitespace, punctuation, simple plurals) do not break matching.
 * e.g. "Tomatoes", " tomato ", "TOMATO" all normalise to "tomato".
 */
public class IngredientNameUtils {

    public static String normalize(String rawName) {
        if (rawName == null) return "";
        String n = rawName.trim().toLowerCase();
        n = n.replaceAll("[^a-z0-9\\s]", ""); // strip punctuation
        n = n.replaceAll("\\s+", " ").trim();
        n = singularize(n);
        return n;
    }

    private static String singularize(String word) {
        if (word.endsWith("ies") && word.length() > 3) {
            return word.substring(0, word.length() - 3) + "y";       // berries -> berry
        }
        if (word.endsWith("oes") && word.length() > 3) {
            return word.substring(0, word.length() - 2);             // tomatoes -> tomato
        }
        if (word.endsWith("ches") || word.endsWith("shes") || word.endsWith("xes") || word.endsWith("sses")) {
            return word.substring(0, word.length() - 2);             // dishes -> dish
        }
        if (word.endsWith("s") && !word.endsWith("ss") && word.length() > 1) {
            return word.substring(0, word.length() - 1);             // eggs -> egg
        }
        return word;
    }
}
