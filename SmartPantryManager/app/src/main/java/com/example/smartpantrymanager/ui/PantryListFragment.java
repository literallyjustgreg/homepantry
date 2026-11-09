package com.example.smartpantrymanager.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.AddEditIngredientActivity;
import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.adapter.PantryAdapter;
import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class PantryListFragment extends Fragment implements PantryAdapter.OnPantryItemListener {

    private DatabaseHelper dbHelper;
    private PantryAdapter adapter;
    private TextView emptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pantry_list, container, false);

        dbHelper = DatabaseHelper.getInstance(requireContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerPantry);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        emptyView = view.findViewById(R.id.tvEmptyPantry);

        adapter = new PantryAdapter(dbHelper.getAllPantryItems(), this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAddIngredient);
        fab.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddEditIngredientActivity.class)));

        refreshList();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    private void refreshList() {
        List<PantryItem> items = dbHelper.getAllPantryItems();
        adapter.updateItems(items);
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(PantryItem item) {
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(PantryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Ingredient")
                .setMessage("Remove \"" + item.getName() + "\" from your pantry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHelper.deletePantryItem(item.getId());
                    refreshList();
                    Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
