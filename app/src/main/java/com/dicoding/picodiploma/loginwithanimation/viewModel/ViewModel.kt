package com.dicoding.picodiploma.loginwithanimation.viewModel

//import android.app.Application
//import android.util.Log
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.viewModelScope
//import androidx.paging.Pager
//import androidx.paging.PagingConfig
//import androidx.paging.PagingData
//import androidx.paging.cachedIn
//import androidx.paging.liveData
//import com.dicoding.picodiploma.loginwithanimation.Api.ApiClient
//import com.dicoding.picodiploma.loginwithanimation.PagingSource
//import com.dicoding.picodiploma.loginwithanimation.data.Constant
//import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory
//import com.dicoding.picodiploma.loginwithanimation.data.sharedPreference.SharedPreferenceHelper
//
//class MyViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val pref: SharedPreferenceHelper by lazy {
//        SharedPreferenceHelper.getInstance(application.applicationContext)
//    }
//
//
//    var service = ApiClient.apiService
//
//    val myDataFlow : LiveData<PagingData<DetailStory>> = Pager(PagingConfig(pageSize = 2 )) {
//        Log.d("token di view model", getToken()!!)
//
//        PagingSource(service,getToken()!!)
//    }.liveData.cachedIn(viewModelScope)
//
//
//
//    fun getToken(): String? {
//        return pref.getToken(Constant.TOKEN_KEY)
//    }
//
//}

import android.app.Application
import android.util.Log
import androidx.compose.ui.window.application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.picodiploma.loginwithanimation.Api.ApiClient
import com.dicoding.picodiploma.loginwithanimation.data.Constant
import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.sharedPreference.SharedPreferenceHelper

class MyViewModel(
    private val repository: StoryRepository,
    private val pref: SharedPreferenceHelper
) : ViewModel() {

    val myDataFlow: LiveData<PagingData<DetailStory>> = repository.getStories(getToken() ?: "").cachedIn(viewModelScope)


    fun getToken(): String? {
        return pref.getToken(Constant.TOKEN_KEY)
    }
}