package com.example.truedone.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.truedone.R;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.UserProfile;
import com.example.truedone.ui.auth.LoginActivity;
import com.example.truedone.ui.main.MainActivity;
import com.example.truedone.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        SessionManager sessionManager = new SessionManager(this);
        String userId = sessionManager.getSavedUserId();

        if (userId != null) {
            // User is logged in: Fetch their theme preference in the background
            AppDatabase.databaseWriteExecutor.execute(() -> {
                UserProfile user = AppDatabase.getDatabase(this).userDao().getUserById(userId);

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (user != null) {
                        // Apply their saved theme
                        AppCompatDelegate.setDefaultNightMode(
                                user.isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES
                                        : AppCompatDelegate.MODE_NIGHT_NO
                        );
                    }
                    // Proceed to Main App
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                });
            });
        } else {
            // No user logged in: Default behavior, wait for 1.5s then go to Login
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }, 1500);
        }
    }
}