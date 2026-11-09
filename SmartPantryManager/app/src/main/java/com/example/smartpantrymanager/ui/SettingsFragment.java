package com.example.smartpantrymanager.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartpantrymanager.R;

public class SettingsFragment extends Fragment {

    public static final String PREFS_NAME = "smart_pantry_prefs";
    public static final String KEY_EXPIRY_ALERTS = "expiry_alerts_enabled";
    public static final String KEY_PREFERRED_UNIT_SYSTEM = "preferred_unit_system";

    private SharedPreferences prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                              @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        prefs = requireContext().getSharedPreferences(PREFS_NAME, 0);

        Switch expirySwitch = view.findViewById(R.id.switchExpiryAlerts);
        RadioGroup unitGroup = view.findViewById(R.id.radioGroupUnits);

        expirySwitch.setChecked(prefs.getBoolean(KEY_EXPIRY_ALERTS, true));
        String preferredUnit = prefs.getString(KEY_PREFERRED_UNIT_SYSTEM, "metric");
        unitGroup.check("imperial".equals(preferredUnit) ? R.id.radioImperial : R.id.radioMetric);

        expirySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean(KEY_EXPIRY_ALERTS, isChecked).apply();
            Toast.makeText(requireContext(),
                    isChecked ? "Expiry alerts enabled" : "Expiry alerts disabled",
                    Toast.LENGTH_SHORT).show();
        });

        unitGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String value = checkedId == R.id.radioImperial ? "imperial" : "metric";
            prefs.edit().putString(KEY_PREFERRED_UNIT_SYSTEM, value).apply();
        });

        return view;
    }
}
