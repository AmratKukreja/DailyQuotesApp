package com.example.dailydose.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface QuoteApiService {
    
    @GET("today")
    suspend fun getTodayQuote(): Response<List<QuoteResponse>>
    
    companion object {
        const val BASE_URL = "https://zenquotes.io/api/"
    }
} 