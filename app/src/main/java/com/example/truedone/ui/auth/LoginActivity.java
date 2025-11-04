package com.example.truedone.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.truedone.databinding.ActivityLoginBinding;
import com.example.truedone.ui.main.MainActivity;

public class LoginActivity extends AppCompatActivity implements AuthContract.LoginView {
    private ActivityLoginBinding binding;
    private AuthContract.LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Serving as both the View Interface and the Context.
        presenter = new LoginPresenterImpl(this, this);

        binding.btnLogin.setOnClickListener(v -> {
            // Clear previous errors
            binding.tilEmail.setError(null);
            binding.tilPassword.setError(null);

            String input = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();
            boolean rememberMe = binding.cbRememberMe.isChecked();

            presenter.login(input, password, rememberMe);
        });

        binding.btnGoToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
            finish(); // Closes Login so pressing "Back" from Register doesn't reopen Login
        });
    }

    @Override
    public void showGeneralError(String error) {
        // A simple Toast works perfectly for general login errors:
        android.widget.Toast.makeText(this, error, android.widget.Toast.LENGTH_SHORT).show();
    }
    @Override
    public void showEmailError(String error) {
        binding.tilEmail.setError(error);
    }

    @Override
    public void showPasswordError(String error) {
        binding.tilPassword.setError(error);
    }



    @Override
    public void onLoginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}