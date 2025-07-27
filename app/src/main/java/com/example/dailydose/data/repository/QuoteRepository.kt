package com.example.dailydose.data.repository

import android.util.Log
import com.example.dailydose.data.local.QuoteDao
import com.example.dailydose.data.local.QuoteEntity
import com.example.dailydose.data.remote.QuoteApiService
import com.example.dailydose.data.remote.QuoteResponse
import com.example.dailydose.utils.Resource
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class QuoteRepository(
    private val apiService: QuoteApiService,
    private val quoteDao: QuoteDao
) {
    
    fun getAllQuotes(): Flow<List<QuoteEntity>> {
        return quoteDao.getAllQuotes()
    }
    
    fun getFavoriteQuotes(): Flow<List<QuoteEntity>> {
        return quoteDao.getFavoriteQuotes()
    }
    
    suspend fun getLatestQuote(): QuoteEntity? {
        return quoteDao.getLatestQuote()
    }
    
    suspend fun getRandomQuote(): QuoteEntity? {
        return quoteDao.getRandomQuote()
    }
    
    suspend fun fetchTodayQuote(): Resource<QuoteEntity> {
        return try {
            val response = apiService.getTodayQuote()
            val responseBody = response.body()
            
            if (response.isSuccessful && responseBody != null && responseBody.isNotEmpty()) {
                val quoteResponse = responseBody[0] // Get first quote from the list
                val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                
                // Clean up the quote text and author
                val cleanQuoteText = quoteResponse.quote.trim()
                val cleanAuthor = quoteResponse.author.trim()
                
                val quoteEntity = QuoteEntity(
                    quoteText = cleanQuoteText,
                    author = cleanAuthor,
                    dateFetched = currentDate
                )
                
                // Save to database
                quoteDao.insertQuote(quoteEntity)
                
                Resource.Success(quoteEntity)
            } else {
                // If API fails, create a motivational quote with current timestamp to ensure variety
                Log.w("QuoteRepository", "API failed, returning fallback quote")
                Resource.Error("API temporarily unavailable")
            }
        } catch (e: Exception) {
            Log.e("QuoteRepository", "Error fetching quote", e)
            Resource.Error("Network error: ${e.message ?: "Unknown error"}")
        }
    }
    
    suspend fun toggleFavoriteStatus(quoteId: Int, isFavorite: Boolean) {
        quoteDao.updateFavoriteStatus(quoteId, isFavorite)
    }
    
    suspend fun updateQuote(quote: QuoteEntity) {
        quoteDao.updateQuote(quote)
    }
    
    suspend fun deleteQuote(quote: QuoteEntity) {
        quoteDao.deleteQuote(quote)
    }
    
    suspend fun insertQuote(quote: QuoteEntity) {
        // Check if quote already exists to avoid duplicates
        val existingQuote = quoteDao.getQuoteByText(quote.quoteText)
        if (existingQuote == null) {
            quoteDao.insertQuote(quote)
        }
    }
    
    suspend fun getQuoteCount(): Int {
        return quoteDao.getQuoteCount()
    }
    
    suspend fun getAllQuotesList(): List<QuoteEntity> {
        return quoteDao.getAllQuotesList()
    }
} 