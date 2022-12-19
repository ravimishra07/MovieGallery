package com.ravi.fimoviesdemo.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ravi.fimoviesdemo.data.Repository
import com.ravi.fimoviesdemo.model.Movie
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
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