package com.example.codescanner.view

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.example.codescanner.R
import com.example.codescanner.databinding.ActivityLoginBinding
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, (R.layout.activity_login))
        init()
    }

    private fun init() {
        val pref = baseContext.getSharedPreferences("", Context.MODE_PRIVATE)
        val code = pref.getInt(getString(R.string.user_code), 0)
        val name = pref.getString(getString(R.string.user_name), "")
        val pass = pref.getString(getString(R.string.user_name), "0")
        val rememberMe = pref.getBoolean(getString(R.string.remember_me_key), false)
        MainScope().launch {
            delay(1500)
            if (code != 0 && name != "" && pass != "0" && rememberMe) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                binding.loginCard.visibility = VISIBLE
                binding.btnLogin.setOnClickListener { validateLogin() }
            }
        }
        checkPermissions()
    }

    private fun checkPermissions(): Boolean =
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                0
            )
            false
        } else true

    private fun validateLogin() {
        when {
            binding.etLoginSalesManCode.text.isNullOrEmpty() -> {
                binding.etLoginSalesManCode.error = "required"
            }
            binding.etLoginSalesManPassword.text.isNullOrEmpty() -> {
                binding.etLoginSalesManPassword.error = "required"
            }
            else -> login()
        }
    }

    private fun login() {
        val pref = getSharedPreferences("", Context.MODE_PRIVATE)
        val code = pref.getInt(getString(R.string.user_code), 0)
        val pass = pref.getString(getString(R.string.user_pass), "0")
        Log.i(TAG, "init: code $code")
        Log.i(TAG, "init: pass $pass")
        if (pass == binding.etLoginSalesManPassword.text.toString()
            && code.toString() == binding.etLoginSalesManCode.text.toString()) {
            if (binding.rememberMe.isChecked) {
                pref.edit().putBoolean(getString(R.string.remember_me_key), true).apply()
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } else {
            Toast.makeText(this, "InCorrect Credentials", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "LoginActivity"
    }
}