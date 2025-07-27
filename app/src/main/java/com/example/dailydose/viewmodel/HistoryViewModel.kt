package com.example.dailydose.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailydose.data.local.QuoteEntity
import com.example.dailydose.data.repository.QuoteRepository
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: QuoteRepository) : ViewModel() {
    
    private val _showFavoritesOnly = MutableLiveData<Boolean>(false)
    val showFavoritesOnly: LiveData<Boolean> = _showFavoritesOnly
    
    val allQuotes: LiveData<List<QuoteEntity>> = repository.getAllQuotes().asLiveData()
    val favoriteQuotes: LiveData<List<QuoteEntity>> = repository.getFavoriteQuotes().asLiveData()
    
    fun toggleFavoriteFilter() {
        _showFavoritesOnly.value = !(_showFavoritesOnly.value ?: false)
    }
    
    fun toggleFavoriteStatus(quote: QuoteEntity) {
        viewModelScope.launch {
            val updatedQuote = quote.copy(isFavorite = !quote.isFavorite)
            repository.updateQuote(updatedQuote)
        }
    }
    
    fun deleteQuote(quote: QuoteEntity) {
        viewModelScope.launch {
            repository.deleteQuote(quote)
        }
    }
} 