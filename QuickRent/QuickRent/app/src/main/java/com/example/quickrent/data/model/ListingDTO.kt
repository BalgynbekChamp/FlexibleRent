package com.example.quickrent.data.model

import java.math.BigDecimal
import java.time.LocalDate

data class ListingDTO(
    val id: Long? = null,
    val title: String,
    val userId: Long,
    val description: String,
    val status: String, // можно через Enum если хочешь
    val locationId: Long,
    val imageUrl: String? = null,
    val price: BigDecimal,
    val viewsCount: Int = 0,
    val categoryId: Long,
    val availableFrom: String,
    val availableTo: String,
    val createdAt: String? = null, // ISO строка, потому что LocalDateTime сложнее сериализовать
    val isPopular: Boolean = false,
    val toolId: Long? = null
)
