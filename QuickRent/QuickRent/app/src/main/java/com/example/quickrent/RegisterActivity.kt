package com.example.quickrent

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.quickrent.network.RetrofitClient
import com.example.quickrent.data.model.RegisterRequest
import com.example.quickrent.data.model.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Инициализация элементов
        val nameField = findViewById<EditText>(R.id.nameField)
        val surnameField = findViewById<EditText>(R.id.surnameField)
        val phoneField = findViewById<EditText>(R.id.phoneField)
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val confirmPasswordField = findViewById<EditText>(R.id.confirmPasswordField)
        val registerButton = findViewById<Button>(R.id.registerButton)
        val loginLink = findViewById<TextView>(R.id.loginLink)

        // Переход на экран авторизации
        loginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }



        // Обработчик клика по кнопке регистрации
        registerButton.setOnClickListener {
            val name = nameField.text.toString().trim()
            val lastname = surnameField.text.toString().trim()
            val phoneNumber = phoneField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            // Проверка на пустые поля
            if (name.isNotEmpty() && lastname.isNotEmpty() && phoneNumber.isNotEmpty() &&
                email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                // Проверка на совпадение пароля
                if (password == confirmPassword) {
                    registerUser(name, lastname, phoneNumber, email, password, confirmPassword)
                } else {
                    Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Функция для отправки данных на сервер через Retrofit
    private fun registerUser(name: String, lastname: String, phoneNumber: String, email: String, password: String, confirmPassword: String) {
        val registerRequest = RegisterRequest(name, lastname, phoneNumber, email, password, confirmPassword)

        lifecycleScope.launch {
            try {
                // Отправка данных на сервер
                val response: Response<RegisterResponse> = RetrofitClient.api.register(registerRequest)

                // Обработка ответа от сервера
                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Регистрация успешна!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@RegisterActivity, "Ошибка регистрации: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Ошибка при подключении к серверу", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
