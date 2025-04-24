package com.example.quickrent

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        emailField = findViewById(R.id.emailField)
        resetButton = findViewById(R.id.resetButton)

        // 🔥 Email-ді автоматты түрде жазу
        val receivedEmail = intent.getStringExtra("email")
        emailField.setText(receivedEmail)

        resetButton.setOnClickListener {
            val email = emailField.text.toString()
            if (email.isNotEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Письмо для сброса пароля отправлено!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Ошибка сброса пароля", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Введите Email", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
