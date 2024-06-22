package com.dicoding.picodiploma.loginwithanimation.data


import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import com.dicoding.picodiploma.loginwithanimation.Api.ApiService
import com.dicoding.picodiploma.loginwithanimation.PagingSource
import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory


class StoryRepository(private val apiService: ApiService) {

    fun getStories(authToken: String): LiveData<PagingData<DetailStory>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PagingSource(apiService, authToken) }
        ).liveData
    }
}
