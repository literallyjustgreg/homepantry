package com.example.smartpantrymanager.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.R;
import com.example.smartpantrymanager.model.PantryItem;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryViewHolder> {

    public interface OnPantryItemListener {
        void onItemClick(PantryItem item);
        void onDeleteClick(PantryItem item);
    }

    private List<PantryItem> items;
    private final OnPantryItemListener listener;

    public PantryAdapter(List<PantryItem> items, OnPantryItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateItems(List<PantryItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PantryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pantry, parent, false);
        return new PantryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.name.setText(item.getName());
        String qtyText = formatQuantity(item.getQuantity()) + " " + (item.getUnit() == null ? "" : item.getUnit());
        holder.quantity.setText(qtyText.trim());

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText("Expires: " + item.getExpiryDate());
        } else {
            holder.expiry.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    private String formatQuantity(double q) {
        if (q == Math.floor(q)) return String.valueOf((long) q);
        return String.valueOf(q);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class PantryViewHolder extends RecyclerView.ViewHolder {
        TextView name, quantity, expiry;
        ImageButton deleteButton;

        PantryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvIngredientName);
            quantity = itemView.findViewById(R.id.tvIngredientQuantity);
            expiry = itemView.findViewById(R.id.tvIngredientExpiry);
            deleteButton = itemView.findViewById(R.id.btnDeleteIngredient);
        }
    }
}
