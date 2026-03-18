package com.example.roy.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "ROY_PREFS"; // ✅ Un solo nombre
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_TOKEN = "token";

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // Guardar sesión
    public void saveSession(int userId, String token) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    // Obtener userId
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    // Obtener token
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    // Verificar si hay sesión
    public boolean isLoggedIn() {
        return getUserId() != -1 && getToken() != null;
    }

    // Cerrar sesión
    public void logout() {
        editor.clear();
        editor.apply();
    }
}