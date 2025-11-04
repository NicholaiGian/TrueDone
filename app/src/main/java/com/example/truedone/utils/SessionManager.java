package com.example.truedone.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "TruedoneSession";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_CRED_ID = "cred_id";
    private static final String KEY_CRED_PW = "cred_pw";
    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveSession(String userId) {
        prefs.edit().putString(KEY_USER_ID, userId).apply();
    }

    public void saveCredentials(String loginId, String password) {
        prefs.edit().putString(KEY_CRED_ID, loginId)
                .putString(KEY_CRED_PW, password).apply();
    }

    public String getSavedUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String[] getSavedCredentials() {
        String id = prefs.getString(KEY_CRED_ID, null);
        String pw = prefs.getString(KEY_CRED_PW, null);
        if (id != null && pw != null) return new String[]{id, pw};
        return null;
    }

    public void clearSession() {
        prefs.edit().remove(KEY_USER_ID).apply();
    }
}