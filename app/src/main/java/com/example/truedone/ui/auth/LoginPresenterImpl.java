package com.example.truedone.ui.auth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.UserProfile;
import com.example.truedone.utils.PasswordHasher;
import com.example.truedone.utils.SessionManager;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.UserProfile;
import com.example.truedone.utils.PasswordHasher;
import com.example.truedone.utils.SessionManager;

public class LoginPresenterImpl implements AuthContract.LoginPresenter {
    private AuthContract.LoginView view;
    private AppDatabase db;
    private SessionManager sessionManager;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public LoginPresenterImpl(AuthContract.LoginView view, Context context) {
        this.view = view;
        this.db = AppDatabase.getDatabase(context);
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public void login(String input, String password, boolean rememberMe) {
        boolean hasError = false;

        // 1. Validate Empty Input
        if (input.isEmpty()) {
            view.showEmailError("Email or Username is required");
            hasError = true;
        }

        // 2. Validate Empty Password
        if (password.isEmpty()) {
            view.showPasswordError("Password is required");
            hasError = true;
        }

        if (hasError) return;

        // 3. Validate Password Length
        if (password.length() < 8 || password.length() > 16) {
            view.showPasswordError("Password must be 8-16 characters");
            return;
        }

        // 4. Clean input & Determine Type
        // If someone accidentally typed "@username", remove the "@" so the database can find it
        String cleanInput = input;
        if (cleanInput.startsWith("@") && !cleanInput.contains(".")) {
            cleanInput = cleanInput.substring(1);
        }

        // Check if the cleaned input is a formally valid email address
        boolean isEmail = Patterns.EMAIL_ADDRESS.matcher(cleanInput).matches();

        // We need a final variable to pass into the background thread
        final String loginQuery = cleanInput;

        // 5. Database Checking
        AppDatabase.databaseWriteExecutor.execute(() -> {

            // If it perfectly matched an email format, search by email. Otherwise, search by username.
            UserProfile user = isEmail
                    ? db.userDao().getUserByEmail(loginQuery)
                    : db.userDao().getUserByUsername(loginQuery);

            mainHandler.post(() -> {
                if (user == null) {
                    view.showEmailError("Account does not exist");
                } else if (!PasswordHasher.verify(password, user.passwordHash)) {
                    view.showPasswordError("Incorrect password");
                } else {
                    // 1. Save Session
                    sessionManager.saveSession(user.userId);
                    if (rememberMe) sessionManager.saveCredentials(loginQuery, password);

                    // 2. APPLY THEME BEFORE CHANGING SCREENS
                    AppCompatDelegate.setDefaultNightMode(
                            user.isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES
                                    : AppCompatDelegate.MODE_NIGHT_NO
                    );

                    // 3. Navigate to MainActivity
                    view.onLoginSuccess();
                }
            });
        });
    }
}
