package com.d_shield_parent.SharedPreference

import android.content.Context
import android.content.SharedPreferences

object shareprefManager {
    private const val PREF_NAME = "dshield_prefs"

    // ✅ Use consistent key names
    private const val KEY_TOKEN = "auth_token"
    private const val KEY_USER_ROLE = "user_role"
    private const val KEY_USER_TYPE = "user_Type"
    private const val KEY_PHONE = "user_phone"

    private const val KEY_POINTS    = "user_points"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLogin(token: String, role: String, userType: String, phone: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_USER_ROLE, role)
            .putString(KEY_USER_TYPE, userType)
            .putString(KEY_PHONE, phone)
            .apply()
    }

    fun getPhone(): String? {
        return prefs.getString(KEY_PHONE, null)
    }

    fun savePoints(points: Int) {
        prefs.edit().putInt("points", points).apply()
    }

    fun getPoints(): Int {
        return prefs.getInt("points", 0)
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun getUserType(): String? {
        return prefs.getString(KEY_USER_TYPE, null)
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrEmpty()
    }

    fun logout() {
        prefs.edit().clear().apply()
    }
}