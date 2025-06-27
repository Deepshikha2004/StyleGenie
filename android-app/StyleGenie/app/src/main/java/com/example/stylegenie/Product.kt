package com.example.stylegenie

data class Product(
    val imageResId: Int,
    val name: String,
    val price: String,
    var isFavorite: Boolean = false
)
