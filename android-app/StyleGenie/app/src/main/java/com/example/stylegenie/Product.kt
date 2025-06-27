package com.example.stylegenie

import java.io.Serializable

data class Product(
    val imageResId: Int,
    val name: String,
    val price: String,
    val description: String = "No description available.",
    var isFavorite: Boolean = false
) : Serializable
