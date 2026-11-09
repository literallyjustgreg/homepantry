package com.example.smartpantrymanager.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Converts common pantry/recipe units into a base unit per category so
 * quantities can be fairly compared (e.g. 1kg pantry vs 500g required).
 */
public class UnitConverter {

    public enum Category { WEIGHT, VOLUME, COUNT, UNKNOWN }

    private static final Map<String, Double> WEIGHT_TO_GRAMS = new HashMap<>();
    private static final Map<String, Double> VOLUME_TO_ML = new HashMap<>();
    private static final Map<String, Double> COUNT_TO_UNIT = new HashMap<>();

    static {
        WEIGHT_TO_GRAMS.put("g", 1.0);
        WEIGHT_TO_GRAMS.put("gram", 1.0);
        WEIGHT_TO_GRAMS.put("grams", 1.0);
        WEIGHT_TO_GRAMS.put("kg", 1000.0);
        WEIGHT_TO_GRAMS.put("kilogram", 1000.0);
        WEIGHT_TO_GRAMS.put("kilograms", 1000.0);

        VOLUME_TO_ML.put("ml", 1.0);
        VOLUME_TO_ML.put("millilitre", 1.0);
        VOLUME_TO_ML.put("milliliter", 1.0);
        VOLUME_TO_ML.put("l", 1000.0);
        VOLUME_TO_ML.put("litre", 1000.0);
        VOLUME_TO_ML.put("liter", 1000.0);
        VOLUME_TO_ML.put("tsp", 5.0);
        VOLUME_TO_ML.put("teaspoon", 5.0);
        VOLUME_TO_ML.put("tbsp", 15.0);
        VOLUME_TO_ML.put("tablespoon", 15.0);
        VOLUME_TO_ML.put("cup", 240.0);
        VOLUME_TO_ML.put("cups", 240.0);

        COUNT_TO_UNIT.put("pcs", 1.0);
        COUNT_TO_UNIT.put("piece", 1.0);
        COUNT_TO_UNIT.put("pieces", 1.0);
        COUNT_TO_UNIT.put("unit", 1.0);
        COUNT_TO_UNIT.put("units", 1.0);
        COUNT_TO_UNIT.put("clove", 1.0);
        COUNT_TO_UNIT.put("cloves", 1.0);
        COUNT_TO_UNIT.put("slice", 1.0);
        COUNT_TO_UNIT.put("slices", 1.0);
        COUNT_TO_UNIT.put("", 1.0);
    }

    public static Category getCategory(String unit) {
        if (unit == null) return Category.UNKNOWN;
        String u = unit.trim().toLowerCase();
        if (WEIGHT_TO_GRAMS.containsKey(u)) return Category.WEIGHT;
        if (VOLUME_TO_ML.containsKey(u)) return Category.VOLUME;
        if (COUNT_TO_UNIT.containsKey(u)) return Category.COUNT;
        return Category.UNKNOWN;
    }

    public static double toBaseValue(double quantity, String unit) {
        if (unit == null) unit = "";
        String u = unit.trim().toLowerCase();
        Category cat = getCategory(u);
        switch (cat) {
            case WEIGHT: return quantity * WEIGHT_TO_GRAMS.get(u);
            case VOLUME: return quantity * VOLUME_TO_ML.get(u);
            case COUNT:  return quantity * COUNT_TO_UNIT.get(u);
            default:     return -1;
        }
    }

    /**
     * Returns true if the pantry quantity satisfies the recipe's required quantity.
     * If units fall into different/unknown categories we cannot safely compare
     * numerically, so we fall back to "pantry has some of it" rather than
     * breaking the match completely on messy data.
     */
    public static boolean satisfies(double pantryQuantity, String pantryUnit,
                                     double requiredQuantity, String requiredUnit) {
        Category pCat = getCategory(pantryUnit);
        Category rCat = getCategory(requiredUnit);

        if (pCat != Category.UNKNOWN && pCat == rCat) {
            double pBase = toBaseValue(pantryQuantity, pantryUnit);
            double rBase = toBaseValue(requiredQuantity, requiredUnit);
            return pBase >= rBase;
        }
        return pantryQuantity > 0;
    }
}
