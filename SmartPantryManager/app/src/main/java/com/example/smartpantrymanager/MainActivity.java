package com.example.smartpantrymanager;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.smartpantrymanager.ui.PantryListFragment;
import com.example.smartpantrymanager.ui.SettingsFragment;
import com.example.smartpantrymanager.ui.SuggestedRecipesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        if (savedInstanceState == null) {
            loadFragment(new PantryListFragment());
        }

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selected = null;
            int id = item.getItemId();
            if (id == R.id.nav_pantry) {
                selected = new PantryListFragment();
            } else if (id == R.id.nav_suggestions) {
                selected = new SuggestedRecipesFragment();
            } else if (id == R.id.nav_settings) {
                selected = new SettingsFragment();
            }
            if (selected != null) {
                loadFragment(selected);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }
}
