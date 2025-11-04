package com.example.truedone.ui.auth;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.UserProfile;
import com.example.truedone.utils.PasswordHasher;
import com.example.truedone.utils.SessionManager;
import java.util.UUID;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatDelegate;

import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.UserProfile;
import com.example.truedone.utils.PasswordHasher;
import com.example.truedone.utils.SessionManager;
import java.util.UUID;

public class RegisterPresenterImpl implements AuthContract.RegisterPresenter {
    private AuthContract.RegisterView view;
    private AppDatabase db;
    private SessionManager sessionManager;
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Context context;

    public RegisterPresenterImpl(AuthContract.RegisterView view, Context context) {
        this.view = view;
        this.context = context;
        this.db = AppDatabase.getDatabase(context);
        this.sessionManager = new SessionManager(context);
    }

    @Override
    public void register(String first, String last, String user, String email, String pass, String confirm) {
        boolean hasError = false;

        if (first.isEmpty() || last.isEmpty() || user.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            view.showGeneralError("All fields are required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.showEmailError("Invalid email address");
            hasError = true;
        }

        if (pass.length() < 8 || pass.length() > 16) {
            view.showPasswordError("Must be 8-16 characters");
            hasError = true;
        }

        if (confirm.length() < 8 || confirm.length() > 16) {
            view.showConfirmError("Must be 8-16 characters");
            hasError = true;
        }

        if (hasError) return;

        if (!pass.equals(confirm)) {
            view.showConfirmError("Passwords do not match");
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            boolean emailExists = db.userDao().emailExists(email);
            boolean userExists = db.userDao().usernameExists(user);

            mainHandler.post(() -> {
                if (emailExists) {
                    view.showEmailError("Email already registered");
                } else if (userExists) {
                    view.showUsernameError("Username already taken");
                } else {
                    saveUser(first, last, user, email, pass);
                }
            });
        });
    }

    private void saveUser(String first, String last, String user, String email, String pass) {
        UserProfile u = new UserProfile();
        u.userId = UUID.randomUUID().toString();
        u.firstName = first;
        u.lastName = last;
        u.username = user;
        u.email = email;
        u.passwordHash = PasswordHasher.hash(pass);
        u.createdAt = System.currentTimeMillis();

        int nightModeFlags = context.getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        boolean isSystemDark = nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES;
        u.isDarkMode = isSystemDark;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            db.userDao().insertUser(u);
            mainHandler.post(() -> {
                sessionManager.saveSession(u.userId);

                // Apply theme smoothly before navigating
                AppCompatDelegate.setDefaultNightMode(
                        u.isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES
                                : AppCompatDelegate.MODE_NIGHT_NO
                );

                view.onRegisterSuccess();
            });
        });
    }
}
