package com.example.dailydose

import android.app.Application
import com.example.dailydose.data.local.QuoteDatabase
import com.example.dailydose.data.remote.QuoteApiService
import com.example.dailydose.data.repository.QuoteRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class DailyDoseApplication : Application() {
    
    private val database by lazy { QuoteDatabase.getDatabase(this) }
    private val apiService by lazy { createApiService() }
    val repository by lazy { QuoteRepository(apiService, database.quoteDao()) }
    
    private fun createApiService(): QuoteApiService {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)
        
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
        
        return Retrofit.Builder()
            .baseUrl(QuoteApiService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(QuoteApiService::class.java)
    }
} 