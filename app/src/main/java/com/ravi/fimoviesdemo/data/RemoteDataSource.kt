package com.ravi.fimoviesdemo.data

import com.ravi.fimoviesdemo.model.SearchData
import com.ravi.fimoviesdemo.util.Constants.Companion.API_KEY
import retrofit2.Response
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val api: RetrofitApi
    ) {
    suspend fun getMovies(searchKey:String,page:Int,type:String): Response<SearchData> {
        return api.getSearchResult(API_KEY,searchKey,page,type)
    }
}