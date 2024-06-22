package com.dicoding.picodiploma.loginwithanimation.data.Response

data class ListStoryResponse (
    val error : Boolean?,
    val message : String?,
    val listStory : List<DetailStory>
)

data class DetailStory(
    val id: String?,
    val name: String?,
    val description: String?,
    val photoUrl: String?,
    val createdAt: String?,
    val lat: String,
    val lon: Float?
)

