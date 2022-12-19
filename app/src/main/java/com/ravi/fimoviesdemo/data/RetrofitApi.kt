package com.ravi.fimoviesdemo.data

import com.ravi.fimoviesdemo.model.SearchData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitApi {
    @GET(".")
    suspend fun getSearchResult(
        @Query("apikey") apikey : String,
        @Query("s") searchKey : String,
        @Query("page") page : Int,
        @Query("type") type : String?="",
    ): Response<SearchData>
}