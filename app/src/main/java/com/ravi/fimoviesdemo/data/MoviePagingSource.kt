package com.ravi.fimoviesdemo.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ravi.fimoviesdemo.data.Repository.Companion.DEFAULT_PAGE_INDEX
import com.ravi.fimoviesdemo.model.Movie
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class MoviePagingSource@Inject constructor(
    private val dataSource: RemoteDataSource, private val query:String,private val type:String=""):
    PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val page = params.key ?: DEFAULT_PAGE_INDEX
        return try {
            val response = dataSource.getMovies(query,page,type)
            if(!response.body()?.searchList.isNullOrEmpty()){
                LoadResult.Page(
                    response.body()?.searchList ?:emptyList(), prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1,
                    nextKey = if (response.body()?.searchList?.isEmpty() == true) null else page + 1
                )
            }else{
                LoadResult.Error(Throwable("no data found"))
            }
        } catch (exception: IOException) {
            return LoadResult.Error(exception)
        } catch (exception: HttpException) {
            return LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return null
    }

}