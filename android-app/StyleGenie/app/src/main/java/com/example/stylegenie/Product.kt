package com.example.stylegenie

import java.io.Serializable

class Product() : Serializable {
    var id: String = ""
    var category: String = ""
    var gender: String = ""
    var img_path: String = ""
    var images: List<String> = emptyList()
    var description: String = ""
    var neckline: String = ""
    var sleeve: String = ""
    var length: String = ""
    var style: String = ""
    var fabric: String = ""
    var occasion: String = ""
    var season: String = ""
    var special_design: String = ""
    var price: Int = 0
    var isFavorite: Boolean = false

    fun getFirstImageUrl(): String {
        return if (images.isNotEmpty()) {
            "$img_path/${images[0]}"
                .replace("github.com", "raw.githubusercontent.com")
                .replace("/tree/", "/")
        } else ""
    }
}
