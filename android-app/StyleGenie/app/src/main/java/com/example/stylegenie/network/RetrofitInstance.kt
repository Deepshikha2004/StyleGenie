package com.example.stylegenie.network

import FashionApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val BASE_URL = "http://192:168:206:158:8000/" // Use actual IP for physical device

    val api: FashionApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FashionApiService::class.java)
    }
}
