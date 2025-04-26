package com.example.quickrent.data.model

import java.math.BigDecimal

data class ListingDTO(
    val id: Long? = null,
    val title: String,
    val userId: Long,
    val description: String,
    val status: String,
    val locationId: Long,
    val price: BigDecimal,
    val viewsCount: Int = 0,
    val categoryId: Long,
    val availableFrom: String,
    val availableTo: String,
    val createdAt: String? = null,
    val isPopular: Boolean = false,
    val toolId: Long? = null,
    val photos: List<PhotoDTO> = emptyList() // <--- добавили сюда!
)
