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

        logoutButton.setOnClickListener {
            logout(requireContext())
        }

        return view
    }

    private fun logout(context: Context) {
        val prefs = context.getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", null)

        if (token != null) {
            val apiService = RetrofitClient.api // қолданатын Retrofit API экземпляры
            val call = apiService.logout("Bearer $token")

            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    // Локалды токенді өшіреміз
                    prefs.edit().remove("auth_token").apply()

                    // Экранды Login-ге ауыстырамыз
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    // Егер сұраныс сәтсіз болса
                    Toast.makeText(context, "Logout failed: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}