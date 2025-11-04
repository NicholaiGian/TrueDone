package com.example.truedone.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import com.example.truedone.data.AppDatabase;
import com.example.truedone.data.model.UserProfile;
import com.example.truedone.databinding.FragmentProfileBinding;
import com.example.truedone.ui.auth.LoginActivity;
import com.example.truedone.utils.SessionManager;
import java.util.Locale;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private AppDatabase db;
    private SessionManager sessionManager;
    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        db = AppDatabase.getDatabase(getContext());
        sessionManager = new SessionManager(requireContext());
        userId = sessionManager.getSavedUserId();

        loadProfile();

        binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> toggleDarkMode(isChecked));

        binding.btnSignOut.setOnClickListener(v -> {
            sessionManager.clearSession();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadProfile() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserProfile user = db.userDao().getUserById(userId);
            int total = db.taskDao().getTotalCount(userId);
            int completed = db.taskDao().getCompletedCount(userId);

            int percentage = total > 0 ? (completed * 100 / total) : 0;

            getActivity().runOnUiThread(() -> {
                if (user != null) {
                    binding.tvName.setText(user.firstName + " " + user.lastName);
                    binding.tvUsername.setText("@" + user.username);
                    binding.tvEmail.setText(user.email);

                    // Detach listener temporarily so it doesn't fire when we set it
                    binding.switchDarkMode.setOnCheckedChangeListener(null);

                    // Set the switch to match the database
                    binding.switchDarkMode.setChecked(user.isDarkMode);

                    // Re-attach the listener
                    binding.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> toggleDarkMode(isChecked));

                    binding.tvStatTotal.setText(String.valueOf(total));
                    binding.tvStatRate.setText(percentage + "%");
                }
            });
        });
    }

    private void toggleDarkMode(boolean enable) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            UserProfile user = db.userDao().getUserById(userId);
            if (user != null) {
                user.isDarkMode = enable;
                db.userDao().updateUser(user);
                getActivity().runOnUiThread(() -> {
                    AppCompatDelegate.setDefaultNightMode(
                            enable ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                    );
                });
            }
        });
    }
}
