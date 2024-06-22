package com.dicoding.picodiploma.loginwithanimation.Api

import com.dicoding.picodiploma.loginwithanimation.data.Request.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.data.Request.SignInRequest
import com.dicoding.picodiploma.loginwithanimation.data.Response.ListStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.Response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.Response.SigninResponse
import com.dicoding.picodiploma.loginwithanimation.data.Response.UploadResponse
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    fun registerUsers(@Body raw : SignInRequest ): Call<SigninResponse>

    @POST("login")
    fun loginUsers(@Body raw : LoginRequest ): Call<LoginResponse>

    @GET("stories")
    suspend fun getListStories(
        @Header("Authorization") auth : String?,
        @Query("page") page: Int,
        @Query("size") size: Int = 20
    ): ListStoryResponse

    @GET("stories")
    suspend fun fetchData(
        @Header("Authorization") token: String ,
        @Query("page") page: Int
    ): ListStoryResponse

    @GET("stories")
    fun getListStoriesWithLoc(
        @Header("Authorization") auth : String?,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("location") param: Int = 1
    ): Call<ListStoryResponse>

    @Multipart
    @POST("stories")
    fun uploadImage(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat : Float,
        @Part("lon") lon : Float
    ): Call<UploadResponse>
}
