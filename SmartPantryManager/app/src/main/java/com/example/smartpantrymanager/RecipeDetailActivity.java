package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.model.RecipeIngredient;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "extra_recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        Toolbar toolbar = findViewById(R.id.toolbarRecipeDetail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        DatabaseHelper dbHelper = DatabaseHelper.getInstance(this);
        Recipe recipe = dbHelper.getRecipeById(recipeId);

        TextView tvName = findViewById(R.id.tvRecipeDetailName);
        TextView tvIngredients = findViewById(R.id.tvRecipeDetailIngredients);
        TextView tvSteps = findViewById(R.id.tvRecipeDetailSteps);

        if (recipe != null) {
            setTitle(recipe.getName());
            tvName.setText(recipe.getName());

            StringBuilder sb = new StringBuilder();
            for (RecipeIngredient ri : recipe.getIngredients()) {
                sb.append("• ").append(formatQuantity(ri.getQuantity())).append(" ")
                        .append(ri.getUnit()).append(" ").append(ri.getName()).append("\n");
            }
            tvIngredients.setText(sb.toString().trim());
            tvSteps.setText(recipe.getSteps());
        } else {
            tvName.setText("Recipe not found");
        }
    }

    private String formatQuantity(double q) {
        if (q == Math.floor(q)) return String.valueOf((long) q);
        return String.valueOf(q);
    }
}
