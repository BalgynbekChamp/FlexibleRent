package com.example.quickrent.screens

import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.gridlayout.widget.GridLayout
import com.example.quickrent.R

class UploadPhotoFragment : Fragment() {

    private lateinit var photoGrid: GridLayout
    private lateinit var uploadButton: Button
    private val imageUris = mutableListOf<Uri>()

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUris.add(it)
            addImageToGrid(it)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_upload_photo, container, false)

        photoGrid = view.findViewById(R.id.photoGrid)
        uploadButton = view.findViewById(R.id.uploadButton)

        addImagePlaceholder()

        uploadButton.setOnClickListener {
            Toast.makeText(requireContext(), "Объявление отправлено с фото!", Toast.LENGTH_SHORT).show()
            // Тут будет код для загрузки на сервер
        }

        return view
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
}
