package com.ravi.fimoviesdemo.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.paging.*
import com.ravi.fimoviesdemo.data.Repository
import com.ravi.fimoviesdemo.data.Repository.Companion.DEFAULT_PAGE_INDEX
import com.ravi.fimoviesdemo.model.Movie
import com.ravi.fimoviesdemo.model.SearchData
import com.ravi.fimoviesdemo.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    fun fetchMovies(query:String,type:String=""): Flow<PagingData<Movie>> {
        return repository.loadMovieFlow(query=query, type = type).cachedIn(viewModelScope)
    }
}