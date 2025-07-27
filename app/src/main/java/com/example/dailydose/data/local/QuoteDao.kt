package com.example.dailydose.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    
    @Query("SELECT * FROM quotes ORDER BY dateFetched DESC")
    fun getAllQuotes(): Flow<List<QuoteEntity>>
    
    @Query("SELECT * FROM quotes WHERE isFavorite = 1 ORDER BY dateFetched DESC")
    fun getFavoriteQuotes(): Flow<List<QuoteEntity>>
    
    @Query("SELECT * FROM quotes ORDER BY dateFetched DESC LIMIT 1")
    suspend fun getLatestQuote(): QuoteEntity?
    
    @Query("SELECT * FROM quotes ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomQuote(): QuoteEntity?
    
    @Query("SELECT COUNT(*) FROM quotes")
    suspend fun getQuoteCount(): Int
    
    @Query("SELECT * FROM quotes")
    suspend fun getAllQuotesList(): List<QuoteEntity>
    
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertQuote(quote: QuoteEntity)
    
    @Query("SELECT * FROM quotes WHERE quoteText = :quoteText LIMIT 1")
    suspend fun getQuoteByText(quoteText: String): QuoteEntity?
    
    @Update
    suspend fun updateQuote(quote: QuoteEntity)
    
    @Query("UPDATE quotes SET isFavorite = :isFavorite WHERE id = :quoteId")
    suspend fun updateFavoriteStatus(quoteId: Int, isFavorite: Boolean)
    
    @Delete
    suspend fun deleteQuote(quote: QuoteEntity)
    
    @Query("DELETE FROM quotes")
    suspend fun deleteAllQuotes()
} 