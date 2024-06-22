package com.dicoding.picodiploma.loginwithanimation.data.pref.dataModel

data class UserModel(
    val email: String,
    val token: String,
    val isLogin: Boolean = false,
)