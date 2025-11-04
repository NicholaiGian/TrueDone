package com.example.truedone.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.truedone.databinding.ActivityRegisterBinding;
import com.example.truedone.ui.main.MainActivity;

public class RegisterActivity extends AppCompatActivity implements AuthContract.RegisterView {

    private ActivityRegisterBinding binding;
    private AuthContract.RegisterPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        presenter = new RegisterPresenterImpl(this, this);

        binding.btnRegister.setOnClickListener(v -> {
            // Clear previous errors
            binding.tilFirstName.setError(null);
            binding.tilLastName.setError(null);
            binding.tilUsername.setError(null);
            binding.tilEmail.setError(null);
            binding.tilPassword.setError(null);
            binding.tilConfirmPassword.setError(null);

            String first = binding.etFirstName.getText().toString().trim();
            String last = binding.etLastName.getText().toString().trim();
            String user = binding.etUsername.getText().toString().trim();
            String email = binding.etEmail.getText().toString().trim();
            String pass = binding.etPassword.getText().toString().trim();
            String confirm = binding.etConfirmPassword.getText().toString().trim();

            presenter.register(first, last, user, email, pass, confirm);
        });

        binding.btnGoToLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    public void showGeneralError(String error) {
        // a simple Toast message for general errors
        android.widget.Toast.makeText(this, error, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showEmailError(String error) {
        binding.tilEmail.setError(error);
    }

    @Override
    public void showUsernameError(String error) {
        binding.tilUsername.setError(error);
    }

    @Override
    public void showPasswordError(String error) {
        binding.tilPassword.setError(error);
    }

    @Override
    public void showConfirmError(String error) {
        binding.tilConfirmPassword.setError(error);
    }

    @Override
    public void onRegisterSuccess() {
        // Automatically route to the main app after successful registration
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
