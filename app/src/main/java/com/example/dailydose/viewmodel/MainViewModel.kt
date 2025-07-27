package com.example.dailydose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailydose.data.local.QuoteEntity
import com.example.dailydose.data.repository.QuoteRepository
import com.example.dailydose.utils.Resource
import kotlinx.coroutines.launch

class MainViewModel(private val repository: QuoteRepository) : ViewModel() {
    
    private val _currentQuote = MutableLiveData<QuoteEntity?>()
    val currentQuote: LiveData<QuoteEntity?> = _currentQuote
    
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        loadQuote()
    }
    
    fun loadQuote() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            // First try to fetch from API
            when (val result = repository.fetchTodayQuote()) {
                is Resource.Success -> {
                    _currentQuote.value = result.data
                }
                is Resource.Error -> {
                    // If API fails, try to get latest from database
                    val latestQuote = repository.getLatestQuote()
                    if (latestQuote != null) {
                        _currentQuote.value = latestQuote
                        _error.value = "Showing offline quote"
                    } else {
                        // If no quotes in database, add some default motivational quotes
                        addDefaultQuotes()
                        val defaultQuote = repository.getLatestQuote()
                        if (defaultQuote != null) {
                            _currentQuote.value = defaultQuote
                        } else {
                            _error.value = result.message
                        }
                    }
                }
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
            }
            _loading.value = false
        }
    }
    
    private suspend fun addDefaultQuotes() {
        // Check if quotes already exist
        if (repository.getQuoteCount() > 0) {
            return
        }
        
        val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date())
        val defaultQuotes = listOf(
            QuoteEntity(
                quoteText = "The only way to do great work is to love what you do.",
                author = "Steve Jobs",
                dateFetched = currentDate
            ),
            QuoteEntity(
                quoteText = "Life is what happens to you while you're busy making other plans.",
                author = "John Lennon",
                dateFetched = currentDate
            ),
            QuoteEntity(
                quoteText = "The future belongs to those who believe in the beauty of their dreams.",
                author = "Eleanor Roosevelt",
                dateFetched = currentDate
            ),
            QuoteEntity(
                quoteText = "In the end, we will remember not the words of our enemies, but the silence of our friends.",
                author = "Martin Luther King Jr.",
                dateFetched = currentDate
            ),
            QuoteEntity(
                quoteText = "Success is not final, failure is not fatal: it is the courage to continue that counts.",
                author = "Winston Churchill",
                dateFetched = currentDate
            ),
            QuoteEntity(
                quoteText = "The way to get started is to quit talking and begin doing.",
                author = "Walt Disney",
                dateFetched = currentDate
            ),
            QuoteEntity(
                quoteText = "Innovation distinguishes between a leader and a follower.",
                author = "Steve Jobs",
                dateFetched = currentDate
            ),
            QuoteEntity(
                quoteText = "Your time is limited, don't waste it living someone else's life.",
                author = "Steve Jobs",
                dateFetched = currentDate
            ),
            QuoteEntity(
                quoteText = "It is during our darkest moments that we must focus to see the light.",
                author = "Aristotle",
                dateFetched = currentDate
            ),
            QuoteEntity(
                quoteText = "Believe you can and you're halfway there.",
                author = "Theodore Roosevelt",
                dateFetched = currentDate
            )
        )
        
        // Only add quotes that don't already exist (to avoid duplicates)
        defaultQuotes.forEach { quote ->
            repository.insertQuote(quote)
        }
    }
    
    fun refreshQuote() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            // Try to fetch from API first
            when (val result = repository.fetchTodayQuote()) {
                is Resource.Success -> {
                    _currentQuote.value = result.data
                }
                is Resource.Error -> {
                    // If API fails, show a different random quote from database
                    var quoteCount = repository.getQuoteCount()
                    if (quoteCount == 0) {
                        addDefaultQuotes()
                        quoteCount = repository.getQuoteCount()
                    }
                    
                    if (quoteCount > 0) {
                        val allQuotes = repository.getAllQuotesList()
                        val currentQuoteText = _currentQuote.value?.quoteText
                        
                        val availableQuotes = if (allQuotes.size > 1) {
                            allQuotes.filter { it.quoteText != currentQuoteText }
                        } else {
                            allQuotes
                        }
                        
                        if (availableQuotes.isNotEmpty()) {
                            val randomIndex = (0 until availableQuotes.size).random()
                            _currentQuote.value = availableQuotes[randomIndex]
                            _error.value = "Showing random quote (offline)"
                        } else {
                            _error.value = "No quotes available"
                        }
                    } else {
                        _error.value = result.message
                    }
                }
                is Resource.Loading -> {
                    // Handle loading state if needed
                }
            }
            _loading.value = false
        }
    }
    
    fun showRandomQuote() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            
            // Ensure we have quotes in database first
            var quoteCount = repository.getQuoteCount()
            if (quoteCount == 0) {
                addDefaultQuotes()
                quoteCount = repository.getQuoteCount()
            }
            
            if (quoteCount > 0) {
                // Get all quotes and pick one randomly (excluding current quote if possible)
                val allQuotes = repository.getAllQuotesList()
                val currentQuoteText = _currentQuote.value?.quoteText
                
                val availableQuotes = if (allQuotes.size > 1) {
                    allQuotes.filter { it.quoteText != currentQuoteText }
                } else {
                    allQuotes
                }
                
                if (availableQuotes.isNotEmpty()) {
                    val randomIndex = (0 until availableQuotes.size).random()
                    _currentQuote.value = availableQuotes[randomIndex]
                } else {
                    _error.value = "No quotes available"
                }
            } else {
                _error.value = "Failed to load quotes"
            }
            
            _loading.value = false
        }
    }
    
    fun toggleFavorite(quote: QuoteEntity) {
        viewModelScope.launch {
            val updatedQuote = quote.copy(isFavorite = !quote.isFavorite)
            repository.updateQuote(updatedQuote)
            _currentQuote.value = updatedQuote
        }
    }
    
    fun clearError() {
        _error.value = null
    }
} 