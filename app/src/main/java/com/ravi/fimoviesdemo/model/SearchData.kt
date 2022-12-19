package com.ravi.fimoviesdemo.model

import com.google.gson.annotations.SerializedName
import com.ravi.fimoviesdemo.model.Movie

data class SearchData(
    @SerializedName("Search")
    val searchList: List<Movie>
)
