package com.example.codescanner.data

import android.app.Application
import android.content.Context
import com.example.codescanner.R

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        val pref = baseContext.getSharedPreferences("", Context.MODE_PRIVATE) ?: return
        with(pref.edit()) {
            val code = pref.getInt(getString(R.string.user_code) , 0)
            val name = pref.getString(getString(R.string.user_name) , "")
            val pass = pref.getString(getString(R.string.user_name) , "0")
            if (code == 0 && name == "" && pass == "0"){
                putInt(getString(R.string.user_code), 123456)
                putString(getString(R.string.user_pass) , "123456")
                putString(getString(R.string.user_name) , "User 1")
            }
            apply()
        }
    }
}