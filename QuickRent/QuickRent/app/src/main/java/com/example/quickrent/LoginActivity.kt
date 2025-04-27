package com.example.quickrent

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quickrent.network.RetrofitClient
import com.example.quickrent.network.model.LoginRequest
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerLink = findViewById<TextView>(R.id.registerLink)
        val forgotPasswordLink = findViewById<TextView>(R.id.forgotPasswordLink)

        // 📌 Переход на экран регистрации
        registerLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        // 📌 Логин через сервер (Retrofit)
        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            Log.d("LoginActivity", "Отправляю: Email=$email, Password=$password")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                login(email, password)  // ✅ Используем Retrofit, а не Firebase
            } else {
                Log.e("LoginActivity", "Поля email или пароль пустые!")
                Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show()
            }
        }

        // 📌 Переход на восстановление пароля
        forgotPasswordLink.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            intent.putExtra("email", emailField.text.toString())
            startActivity(intent)
        }
    }

    private fun login(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.login(LoginRequest(email, password))

                Log.d("LoginActivity", "Ответ сервера: ${response.code()} - ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    val token = response.body()!!.token
                    val userId = responseBody.user.id  // Получаем userId из ответа сервера

                    Toast.makeText(this@LoginActivity, "Вход выполнен!", Toast.LENGTH_SHORT).show()

                    Log.d("LoginActivity", "Полученный токен: $token")
                    Log.d("LoginActivity", "Полученный userId: $userId")

                    // ✅ Сохранение токена и userId в SharedPreferences
                    val sharedPreferences = getSharedPreferences("QuickRentPrefs", MODE_PRIVATE)
                    sharedPreferences.edit().apply {
                        putString("auth_token", token)
                        putLong("user_id", userId)  // Сохраняем user_id
                        apply()
                    }
                    Log.d("Auth", "Token and userId saved: $token, $userId")

                    // ✅ Переход в MainActivity
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                    finish()
                } else {
                    Log.e("LoginActivity", "Ошибка входа: ${response.errorBody()?.string()}")
                    Toast.makeText(this@LoginActivity, "Ошибка входа", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("LoginActivity", "Ошибка сервера: ${e.message}")
                Toast.makeText(this@LoginActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
