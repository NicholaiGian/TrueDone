package com.example.truedone.ui.auth;

public interface AuthContract {
    // --- LOGIN ---
    interface LoginView {
        void onLoginSuccess();
        void showEmailError(String error);    // For Email/Username field
        void showPasswordError(String error); // For Password field
        void showGeneralError(String error);  // For generic failures (toast/snackbar)
    }

    interface LoginPresenter {
        void login(String emailOrUser, String password, boolean rememberMe);
    }

    // --- REGISTER ---
    interface RegisterView {
        void onRegisterSuccess();
        void showEmailError(String error);
        void showUsernameError(String error);
        void showPasswordError(String error);
        void showConfirmError(String error);
        void showGeneralError(String error);
    }

    interface RegisterPresenter {
        void register(String first, String last, String user, String email, String pass, String confirm);
    }
}
