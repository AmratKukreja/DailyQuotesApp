package com.example.dailydose.data.remote

import com.google.gson.annotations.SerializedName

data class QuoteResponse(
    @SerializedName("q")
    val quote: String,
    @SerializedName("a")
    val author: String,
    @SerializedName("i")
    val image: String? = null,
    @SerializedName("c")
    val length: String? = null,
    @SerializedName("h")
    val html: String? = null
) 