package com.d_shield_parent

import android.app.Application
import com.d_shield_parent.SharedPreference.shareprefManager

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // ✅ Initialize SharedPreferences Manager
        shareprefManager.init(this)
    }
}