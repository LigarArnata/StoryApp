package com.dicoding.picodiploma.loginwithanimation

import android.content.Context
import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.dicoding.picodiploma.loginwithanimation.Api.ApiService
import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory

class PagingSource(private val service: ApiService,private val token: String) : PagingSource<Int,DetailStory>() {


    override fun getRefreshKey(state: PagingState<Int, DetailStory>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DetailStory> {
        return try {
            val nextPage = params.key ?: 1

            Log.d("token di paging",token)

            val response = service.fetchData(token,nextPage)

            LoadResult.Page(
                data = response.listStory,
                prevKey = if(nextPage == 1) null else nextPage -1,
                nextKey = nextPage + 1
            )
        } catch (e:Exception){
            println("error di bagian ini ${e.message}")
            LoadResult.Error(e)
        }
    }

}