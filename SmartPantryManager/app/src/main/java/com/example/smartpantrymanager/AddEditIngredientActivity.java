package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.smartpantrymanager.data.DatabaseHelper;
import com.example.smartpantrymanager.model.PantryItem;

public class AddEditIngredientActivity extends AppCompatActivity {

    public static final String EXTRA_ITEM_ID = "extra_item_id";

    private DatabaseHelper dbHelper;
    private EditText etName, etQuantity, etUnit, etExpiry;
    private Button btnSave, btnDelete;
    private PantryItem existingItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_ingredient);

        Toolbar toolbar = findViewById(R.id.toolbarAddEdit);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        dbHelper = DatabaseHelper.getInstance(this);

        etName = findViewById(R.id.etIngredientName);
        etQuantity = findViewById(R.id.etIngredientQuantity);
        etUnit = findViewById(R.id.etIngredientUnit);
        etExpiry = findViewById(R.id.etIngredientExpiry);
        btnSave = findViewById(R.id.btnSaveIngredient);
        btnDelete = findViewById(R.id.btnDeleteIngredientForm);

        long itemId = getIntent().getLongExtra(EXTRA_ITEM_ID, -1);

        if (itemId != -1) {
            setTitle("Edit Ingredient");
            existingItem = dbHelper.getPantryItem(itemId);
            if (existingItem != null) {
                etName.setText(existingItem.getName());
                etQuantity.setText(String.valueOf(existingItem.getQuantity()));
                etUnit.setText(existingItem.getUnit());
                etExpiry.setText(existingItem.getExpiryDate());
            }
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            setTitle("Add Ingredient");
            btnDelete.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(v -> saveIngredient());
        btnDelete.setOnClickListener(v -> {
            if (existingItem != null) {
                dbHelper.deletePantryItem(existingItem.getId());
                Toast.makeText(this, "Ingredient deleted", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void saveIngredient() {
        String name = etName.getText().toString().trim();
        String qtyStr = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etName.setError("Ingredient name is required");
            etName.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(qtyStr)) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        double quantity;
        try {
            quantity = Double.parseDouble(qtyStr);
            if (quantity <= 0) {
                etQuantity.setError("Quantity must be greater than zero");
                etQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(unit)) {
            etUnit.setError("Unit is required (e.g. g, ml, pcs)");
            etUnit.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(expiry) && !expiry.matches("\\d{4}-\\d{2}-\\d{2}")) {
            etExpiry.setError("Use format YYYY-MM-DD");
            etExpiry.requestFocus();
            return;
        }

        PantryItem item = existingItem != null ? existingItem : new PantryItem();
        item.setName(name);
        item.setQuantity(quantity);
        item.setUnit(unit);
        item.setExpiryDate(TextUtils.isEmpty(expiry) ? null : expiry);

        if (existingItem != null) {
            dbHelper.updatePantryItem(item);
            Toast.makeText(this, "Ingredient updated", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.addPantryItem(item);
            Toast.makeText(this, "Ingredient added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }
}
