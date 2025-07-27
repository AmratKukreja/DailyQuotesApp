package com.example.dailydose.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotes")
data class QuoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val quoteText: String,
    val author: String,
    val dateFetched: String,
    val isFavorite: Boolean = false
) 