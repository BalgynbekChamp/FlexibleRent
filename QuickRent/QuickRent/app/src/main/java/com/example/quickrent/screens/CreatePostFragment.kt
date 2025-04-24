package com.example.quickrent.screens

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.quickrent.R
import com.example.quickrent.network.RetrofitClient
import com.example.quickrent.data.model.ListingDTO
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDate

class CreatePostFragment : Fragment() {

    private lateinit var titleField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var priceField: EditText
    private lateinit var categoryIdField: EditText
    private lateinit var availableFromField: EditText
    private lateinit var availableToField: EditText
    private lateinit var createPostButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        titleField = view.findViewById(R.id.titleField)
        descriptionField = view.findViewById(R.id.descriptionField)
        priceField = view.findViewById(R.id.priceField)
        categoryIdField = view.findViewById(R.id.categoryIdField)
        availableFromField = view.findViewById(R.id.availableFromField)
        availableToField = view.findViewById(R.id.availableToField)
        createPostButton = view.findViewById(R.id.createPostButton)

        createPostButton.setOnClickListener {
            createPost()
        }

        availableFromField.setOnClickListener {
            showDatePickerDialog { date ->
                availableFromField.setText(date)
            }
        }

        availableToField.setOnClickListener {
            showDatePickerDialog { date ->
                availableToField.setText(date)
            }
        }

        return view
    }

    private fun createPost() {
        val title = titleField.text.toString()
        val description = descriptionField.text.toString()
        val price = priceField.text.toString().toBigDecimalOrNull()
        val categoryId = categoryIdField.text.toString().toLongOrNull()
        val availableFrom = availableFromField.text.toString()
        val availableTo = availableToField.text.toString()

        if (title.isBlank() || description.isBlank() || price == null || categoryId == null) {
            Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val listing = ListingDTO(
            id = null,
            title = title,
            userId = 1L, // тестовое значение
            description = description,
            status = "ACTIVE",
            locationId = 1L,
            imageUrl = null,
            price = price,
            viewsCount = 0,
            categoryId = categoryId,
            availableFrom = availableFrom,
            availableTo = availableTo,
            createdAt = null,
            isPopular = false,
            toolId = null
        )

        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.createListing(token, listing)

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Объявление создано!", Toast.LENGTH_SHORT).show()
                    clearFields()

                    // Переход на фрагмент загрузки фото
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, UploadPhotoFragment())
                        .addToBackStack(null)  // Добавляем в back stack
                        .commit()
                } else {
                    Toast.makeText(requireContext(), "Ошибка создания: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Ошибка: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun clearFields() {
        titleField.text.clear()
        descriptionField.text.clear()
        priceField.text.clear()
        categoryIdField.text.clear()
        availableFromField.text.clear()
        availableToField.text.clear()
    }

    private fun showDatePickerDialog(onDateSelected: (String) -> Unit) {
        val today = LocalDate.now()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                onDateSelected(selectedDate)
            },
            today.year,
            today.monthValue - 1,
            today.dayOfMonth
        )
        datePicker.show()
    }
}
