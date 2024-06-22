package com.dicoding.picodiploma.loginwithanimation.di

import android.content.Context
import com.dicoding.picodiploma.loginwithanimation.Api.ApiClient
import com.dicoding.picodiploma.loginwithanimation.data.StoryRepository
import com.dicoding.picodiploma.loginwithanimation.data.UserRepository
import com.dicoding.picodiploma.loginwithanimation.data.pref.UserPreference
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataStore
import com.dicoding.picodiploma.loginwithanimation.data.sharedPreference.SharedPreferenceHelper

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
//    fun provideUserRepository(context: Context): UserRepository {
//        val apiService = ApiClient.apiService
//        return UserRepository(apiService)
//    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiClient.apiService
        return StoryRepository(apiService)
    }

    fun providePreferencesHelper(context: Context): SharedPreferenceHelper {
        return SharedPreferenceHelper.getInstance(context.applicationContext)
    }
}