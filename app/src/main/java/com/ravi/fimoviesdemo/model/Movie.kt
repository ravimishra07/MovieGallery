package com.ravi.fimoviesdemo.model

import com.google.gson.annotations.SerializedName

data class Movie(
    @SerializedName("Year")
    val year: String,
    val imdbID: String,
    @SerializedName("Type")
    val type: String,
    @SerializedName("Poster")
    val poster: String,
    @SerializedName("Title")
    val movieTitle: String
)
