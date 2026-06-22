package com.tension_app;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = getFragmentForItem(item.getItemId());
            if (fragment != null) {
                getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
                return true;
            }
            return false;
        });

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new DashboardFragment())
                .commit();
        }
    }

    private Fragment getFragmentForItem(int itemId) {
        if (itemId == R.id.nav_dashboard) return new DashboardFragment();
        if (itemId == R.id.nav_add) return new AddReadingFragment();
        if (itemId == R.id.nav_history) return new HistoryFragment();
        if (itemId == R.id.nav_stats) return new StatsFragment();
        if (itemId == R.id.nav_profile) return new ProfileFragment();
        return null;
    }

    public void navigateTo(int menuItemId) {
        bottomNav.setSelectedItemId(menuItemId);
    }
}
