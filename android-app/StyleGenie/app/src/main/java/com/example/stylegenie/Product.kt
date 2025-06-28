package com.example.stylegenie

import java.io.Serializable

data class Product(
    val id: String = "",
    val category: String = "",      // 🟢 This will be used as product name
    val gender: String = "",
    val img_path: String = "",      // 🟢 Base URL for image
    val images: List<String> = emptyList(), // 🟢 Contains image names
    val description: String = "",
    val neckline: String = "",
    val sleeve: String = "",
    val length: String = "",
    val style: String = "",
    val fabric: String = "",
    val occasion: String = "",
    val season: String = "",
    val special_design: String = "",
    val price: Int = 0,             // 🛠️ Fixed type mismatch
    var isFavorite: Boolean = false
): Serializable {
    // 🔄 Helper function to get image URL
    fun getFirstImageUrl(): String {
        return if (images.isNotEmpty()) {
            "$img_path/${images[0]}"
                .replace("github.com", "raw.githubusercontent.com")
                .replace("/tree/", "/")  // GitHub to Raw GitHub
        } else ""
    }
}
