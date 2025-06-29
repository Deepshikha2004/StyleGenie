package com.example.stylegenie.network

data class FashionImage(
    val title: String,
    val image_url: String
)

data class BotResponse(
    val message: String,
    val images: List<FashionImage>

)
