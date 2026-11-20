package com.bengkelin.app.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Konfigurasi singleton Retrofit untuk koneksi ke server database REST API.
 */
object ApiClient {

    /**
     * URL API Backend:
     * - Emulator Android default localhost: "http://10.0.2.2:5000/"
     * - HP Fisik melalui Wi-Fi lokal: "http://192.168.1.xxx:5000/"
     * - Server Produksi/Cloud Run/VPS: "https://api.bengkelin.id/"
     */
    private const val BASE_URL = "http://10.0.2.2:5000/"

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    val authService: AuthApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApiService::class.java)
    }

    val workshopService: WorkshopApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WorkshopApiService::class.java)
    }
}
