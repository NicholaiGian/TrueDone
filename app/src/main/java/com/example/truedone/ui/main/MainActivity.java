package com.example.truedone.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.truedone.R;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.databinding.ActivityMainBinding;
import com.example.truedone.ui.task.TaskStep1Activity;
import com.example.truedone.utils.SessionManager;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Enforce the user's saved theme preference on app launch
        SessionManager sm = new SessionManager(this);
        String userId = sm.getSavedUserId();
        if (userId != null) {
            AppDatabase.databaseWriteExecutor.execute(() -> {
                var user = AppDatabase.getDatabase(this).userDao().getUserById(userId);
                if (user != null) {
                    runOnUiThread(() -> {
                        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                                user.isDarkMode ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
                                        : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
                        );
                    });
                }
            });
        }

        // Default Fragment (Home)
        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }

        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Fragment fragment = null;

            if (itemId == R.id.nav_home) {
                fragment = new HomeFragment();
            } else if (itemId == R.id.nav_history) {
                fragment = new HistoryFragment();
            } else if (itemId == R.id.nav_profile) {
                fragment = new ProfileFragment();
            }

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });

        // The Camera Permission check happens on TaskStep1 Activity
        binding.fabAddTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TaskStep1Activity.class);
            startActivity(intent);
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commit();
    }
}