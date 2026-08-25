package com.marketplace.onehour.common.network

import android.content.Context
import android.content.SharedPreferences

/**
 * Persists the Sanctum bearer token across app restarts so Splash can
 * decide whether to skip straight past login.
 */
object TokenStore {
    private const val PREFS_NAME = "onehour_auth"
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_ROLE = "user_role"
    private const val KEY_NAME = "user_name"
    private const val KEY_PHONE = "user_phone"
    private const val KEY_EMAIL = "user_email"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveSession(token: String, role: String, name: String, phone: String, email: String?) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_ROLE, role)
            .putString(KEY_NAME, name)
            .putString(KEY_PHONE, phone)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    fun updateRole(role: String) {
        if (::prefs.isInitialized) prefs.edit().putString(KEY_ROLE, role).apply()
    }

    fun getToken(): String? = if (::prefs.isInitialized) prefs.getString(KEY_TOKEN, null) else null

    fun getRole(): String? = if (::prefs.isInitialized) prefs.getString(KEY_ROLE, null) else null

    fun getName(): String? = if (::prefs.isInitialized) prefs.getString(KEY_NAME, null) else null

    fun getPhone(): String? = if (::prefs.isInitialized) prefs.getString(KEY_PHONE, null) else null

    fun getEmail(): String? = if (::prefs.isInitialized) prefs.getString(KEY_EMAIL, null) else null

    fun isLoggedIn(): Boolean = !getToken().isNullOrBlank()

    fun clear() {
        if (::prefs.isInitialized) prefs.edit().clear().apply()
    }
}
