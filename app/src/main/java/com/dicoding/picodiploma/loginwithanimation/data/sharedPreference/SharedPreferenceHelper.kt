package com.dicoding.picodiploma.loginwithanimation.data.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import com.dicoding.picodiploma.loginwithanimation.data.Response.LoginResponse

class SharedPreferenceHelper(context: Context) {

    companion object {
        @Volatile
        private var instance: SharedPreferenceHelper? = null

        fun getInstance(context: Context): SharedPreferenceHelper {
            return instance ?: synchronized(this) {
                instance ?: SharedPreferenceHelper(context).also { instance = it }
            }
        }
    }

    private val prefName =  "sharedPreferece"
    private val sharedPref : SharedPreferences
    private val editor : SharedPreferences.Editor

    init {
        sharedPref = context.getSharedPreferences(prefName,Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

    fun putToken(key: String, data : LoginResponse){
        editor.putString(key,data.loginResult.token).apply()
    }

    fun getToken(key: String) : String?{
        return "Bearer ${sharedPref.getString(key, null)}"
    }

    fun deleteData(){
        editor.clear().apply()
    }

}