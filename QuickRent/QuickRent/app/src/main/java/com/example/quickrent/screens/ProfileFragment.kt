package com.example.quickrent.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.quickrent.LoginActivity
import com.example.quickrent.R
import com.example.quickrent.network.RetrofitClient
import com.example.quickrent.network.ApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val logoutButton: Button = view.findViewById(R.id.logoutButton)
        val renterButton: Button = view.findViewById(R.id.renterButton)
        val renteeButton: Button = view.findViewById(R.id.renteeButton)

        // Действие при нажатии на кнопку выхода
        logoutButton.setOnClickListener {
            logout(requireContext())
        }

        // Действие при нажатии на кнопку арендодателя
        renterButton.setOnClickListener {
            // Обработка для арендодателя
            Toast.makeText(requireContext(), "Секция Арендодателя выбрана", Toast.LENGTH_SHORT).show()
        }

        // Действие при нажатии на кнопку арендатора
        renteeButton.setOnClickListener {
            // Обработка для арендатора
            Toast.makeText(requireContext(), "Секция Арендатора выбрана", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun logout(context: Context) {
        val prefs = context.getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)

        if (token != null) {
            val apiService = RetrofitClient.api // экземпляр Retrofit API
            val call = apiService.logout("Bearer $token")

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    // Локально удаляем токен
                    prefs.edit().remove("auth_token").apply()

                    // Переходим на экран Login
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Если запрос не удался
                    Toast.makeText(context, "Logout failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }}
    }
