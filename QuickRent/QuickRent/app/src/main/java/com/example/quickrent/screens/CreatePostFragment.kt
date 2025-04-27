package com.example.quickrent.screens

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import androidx.lifecycle.lifecycleScope
import com.example.quickrent.R
import com.example.quickrent.data.model.ListingDTO
import com.example.quickrent.data.model.PhotoDTO
import com.example.quickrent.data.model.CategoryDTO
import com.example.quickrent.network.RetrofitClient
import kotlinx.coroutines.launch
import java.time.LocalDate
import android.app.AlertDialog
import android.content.DialogInterface

class CreatePostFragment : Fragment() {

    private lateinit var titleField: EditText
    private lateinit var descriptionField: EditText
    private lateinit var priceField: EditText
    private lateinit var categoryField: TextView
    private lateinit var availableFromField: EditText
    private lateinit var availableToField: EditText
    private lateinit var createPostButton: Button
    private lateinit var photoGrid: GridLayout
    private lateinit var locationIdField: EditText
    private lateinit var toolIdField: EditText

    private val photoList = mutableListOf<PhotoDTO>()
    private val imageUris = mutableListOf<Uri>()

    private var categories: List<CategoryDTO> = listOf() // List to store categories
    private var selectedCategoryId: Long? = null // Store selected category ID

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUris.add(it)
            val photoDTO = createPhotoDTOFromUri(it)
            if (photoDTO != null) {
                photoList.add(photoDTO)
                addImageToGrid(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_create_post, container, false)

        titleField = view.findViewById(R.id.titleField)
        descriptionField = view.findViewById(R.id.descriptionField)
        priceField = view.findViewById(R.id.priceField)
        categoryField = view.findViewById(R.id.categoryField) // Now it's a TextView
        availableFromField = view.findViewById(R.id.availableFromField)
        availableToField = view.findViewById(R.id.availableToField)
        createPostButton = view.findViewById(R.id.createPostButton)
        photoGrid = view.findViewById(R.id.photoGrid)
        locationIdField = view.findViewById(R.id.locationIdField)
        toolIdField = view.findViewById(R.id.toolIdField)

        // Set up the category field
        categoryField.setOnClickListener { fetchCategories() }

        createPostButton.setOnClickListener { createPost() }
        availableFromField.setOnClickListener { showDatePickerDialog { availableFromField.setText(it) } }
        availableToField.setOnClickListener { showDatePickerDialog { availableToField.setText(it) } }

        addImagePlaceholder()

        return view
    }


    private fun fetchCategories() {
        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")


    }


    private fun showCategorySelectionDialog() {
        val categoryNames = categories.map { it.name }
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Выберите категорию")
        builder.setItems(categoryNames.toTypedArray()) { dialogInterface: DialogInterface, which: Int ->
            selectedCategoryId = categories[which].id
            categoryField.text = categories[which].name // Display the selected category name
        }
        builder.show()
    }

    private fun createPost() {
        val title = titleField.text.toString()
        val description = descriptionField.text.toString()
        val price = priceField.text.toString().toBigDecimalOrNull()
        val availableFrom = availableFromField.text.toString()
        val availableTo = availableToField.text.toString()
        val locationId = locationIdField.text.toString().toLongOrNull() ?: 1L
        val toolId = toolIdField.text.toString().toLongOrNull()

        // Use a local variable to avoid the smart cast error
        val localCategoryId = selectedCategoryId

        if (title.isBlank() || description.isBlank() || price == null || localCategoryId == null) {
            Toast.makeText(requireContext(), "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val listing = ListingDTO(
            id = null,
            title = title,
            userId = 1L,
            description = description,
            status = "ACTIVE",
            locationId = locationId,
            price = price,
            viewsCount = 0,
            categoryId = localCategoryId,  // Use local variable here
            availableFrom = availableFrom,
            availableTo = availableTo,
            createdAt = null,
            isPopular = false,
            toolId = toolId,
            photos = photoList
        )

        val sharedPreferences = requireContext().getSharedPreferences("QuickRentPrefs", Context.MODE_PRIVATE)
        val token = "Bearer " + (sharedPreferences.getString("auth_token", "") ?: "")

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.createListing(token, listing)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Объявление создано!", Toast.LENGTH_SHORT).show()
                    clearFields()
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
        availableFromField.text.clear()
        availableToField.text.clear()
        locationIdField.text.clear()
        toolIdField.text.clear()
        photoGrid.removeAllViews()
        imageUris.clear()
        photoList.clear()
        addImagePlaceholder()
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

    private fun addImagePlaceholder() {
        val imageView = ImageView(requireContext()).apply {
            setImageResource(R.drawable.ic_add_photo_create_post)
            layoutParams = ViewGroup.LayoutParams(300, 300)
            scaleType = ImageView.ScaleType.CENTER_INSIDE
            setPadding(16, 16, 16, 16)
            setBackgroundResource(R.drawable.border_bg)
            setOnClickListener {
                pickImageLauncher.launch("image/*")
            }
        }
        photoGrid.addView(imageView)
    }

    private fun addImageToGrid(uri: Uri) {
        val imageView = ImageView(requireContext()).apply {
            setImageURI(uri)
            layoutParams = ViewGroup.LayoutParams(300, 300)
            scaleType = ImageView.ScaleType.CENTER_CROP
        }
        photoGrid.addView(imageView, photoGrid.childCount - 1)
    }

    private fun createPhotoDTOFromUri(uri: Uri): PhotoDTO? {
        val returnCursor = requireContext().contentResolver.query(uri, null, null, null, null)
        returnCursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.moveToFirst()
            val name = it.getString(nameIndex)
            val size = it.getLong(sizeIndex)
            return PhotoDTO(
                filePath = uri.toString(),
                fileName = name,
                size = size,
                entityType = "LISTING",
                entityId = null
            )
        }
        return null
    }
}
