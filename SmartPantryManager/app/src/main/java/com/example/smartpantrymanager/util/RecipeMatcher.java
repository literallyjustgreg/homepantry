package com.example.smartpantrymanager.util;

import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeMatcher {

    public static class MatchResult {
        public Recipe recipe;
        public int missingCount = 0;
        public List<String> missingIngredients = new ArrayList<>();

        public MatchResult(Recipe recipe) { this.recipe = recipe; }
    }

    /** Evaluates exactly how many (and which) ingredients a recipe is missing. */
    public static MatchResult evaluate(Recipe recipe, List<PantryItem> pantry) {
        MatchResult result = new MatchResult(recipe);

        for (RecipeIngredient ri : recipe.getIngredients()) {
            String normRequired = IngredientNameUtils.normalize(ri.getName());
            boolean found = false;

            for (PantryItem item : pantry) {
                String normPantry = IngredientNameUtils.normalize(item.getName());
                if (normPantry.equals(normRequired)
                        && UnitConverter.satisfies(item.getQuantity(), item.getUnit(),
                                                    ri.getQuantity(), ri.getUnit())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.missingCount++;
                result.missingIngredients.add(ri.getName());
            }
        }
        return result;
    }

    /** Only recipes with ZERO missing ingredients qualify — the core assignment rule. */
    public static List<Recipe> getSuggested(List<Recipe> allRecipes, List<PantryItem> pantry) {
        List<Recipe> suggested = new ArrayList<>();
        for (Recipe r : allRecipes) {
            if (evaluate(r, pantry).missingCount == 0) {
                suggested.add(r);
            }
        }
        return suggested;
    }

    /** Bonus "Almost There" list — recipes missing exactly one ingredient. */
    public static List<Recipe> getAlmostThere(List<Recipe> allRecipes, List<PantryItem> pantry) {
        List<Recipe> almost = new ArrayList<>();
        for (Recipe r : allRecipes) {
            if (evaluate(r, pantry).missingCount == 1) {
                almost.add(r);
            }
        }
        return almost;
    }
}
