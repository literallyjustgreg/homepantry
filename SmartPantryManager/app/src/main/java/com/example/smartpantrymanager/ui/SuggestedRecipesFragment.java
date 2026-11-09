package com.example.smartpantrymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.RecipeDetailActivity;
import com.example.smartpantrymanager.adapter.RecipeAdapter;
import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.example.smartpantrymanager.model.Recipe;
import com.example.smartpantrymanager.util.RecipeMatcher;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private DatabaseHelper dbHelper;
    private RecipeAdapter suggestedAdapter;
    private RecipeAdapter almostThereAdapter;
    private TextView emptyView;
    private TextView almostThereLabel;
    private RecyclerView almostThereRecycler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_suggested_recipes, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        RecyclerView suggestedRecycler = view.findViewById(R.id.recyclerSuggested);
        suggestedRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        suggestedAdapter = new RecipeAdapter(new ArrayList<>(), this);
        suggestedRecycler.setAdapter(suggestedAdapter);

        almostThereRecycler = view.findViewById(R.id.recyclerAlmostThere);
        almostThereRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        almostThereAdapter = new RecipeAdapter(new ArrayList<>(), this);
        almostThereRecycler.setAdapter(almostThereAdapter);

        emptyView = view.findViewById(R.id.tvEmptySuggestions);
        almostThereLabel = view.findViewById(R.id.tvAlmostThereLabel);

        refresh();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }

    private void refresh() {
        List<PantryItem> pantry = dbHelper.getAllPantryItems();
        List<Recipe> allRecipes = dbHelper.getAllRecipes();

        List<Recipe> suggested = RecipeMatcher.getSuggested(allRecipes, pantry);
        List<Recipe> almostThere = RecipeMatcher.getAlmostThere(allRecipes, pantry);

        suggestedAdapter.updateRecipes(suggested);
        almostThereAdapter.updateRecipes(almostThere);

        emptyView.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);

        boolean showAlmost = !almostThere.isEmpty();
        almostThereLabel.setVisibility(showAlmost ? View.VISIBLE : View.GONE);
        almostThereRecycler.setVisibility(showAlmost ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
