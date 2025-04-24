package com.example.quickrent

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Проверяем сохранённый токен
        val prefs = getSharedPreferences("QuickRentPrefs", MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)


        // Определяем, куда переходим после 1.5 сек
        val nextActivity: Class<*> =
            if ((token != null)) MainActivity::class.java else LoginActivity::class.java

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed({
            startActivity(Intent(this@SplashActivity, nextActivity))
            finish()
        }, 1500)
    }
}