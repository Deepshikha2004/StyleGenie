package com.example.stylegenie

import java.io.Serializable

data class Product(
    var id: String = "",
    val category: String = "",
    val gender: String = "",
    val img_path: String = "",
    val images: List<String> = emptyList(),
    val description: String = "",
    val neckline: String = "",
    val sleeve: String = "",
    val length: String = "",
    val style: String = "",
    val fabric: String = "",
    val occasion: String = "",
    val season: String = "",
    val special_design: String = "",
    val price: Int = 0,

    // This is a workaround to allow Firebase to deserialize both "true"/"false" strings and booleans
    var isFavoriteRaw: Any? = false
) : Serializable {

    var isFavorite: Boolean = false
        get() = when (isFavoriteRaw) {
            is Boolean -> isFavoriteRaw as Boolean
            is String -> (isFavoriteRaw as String).toBoolean()
            else -> false
        }

    // 🔄 Helper function to get image URL
    fun getFirstImageUrl(): String {
        return if (images.isNotEmpty()) {
            "$img_path/${images[0]}"
                .replace("github.com", "raw.githubusercontent.com")
                .replace("/tree/", "/")
        } else ""
    }
}
