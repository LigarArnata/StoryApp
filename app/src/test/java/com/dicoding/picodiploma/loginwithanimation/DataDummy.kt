package com.dicoding.picodiploma.loginwithanimation

import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory


object DataDummy {
    fun generateDummyStoryResponse(): List<DetailStory> {
        val items: MutableList<DetailStory> = arrayListOf()
        for (i in 0..100) {
            val story = DetailStory(
                i.toString(),
                "createdAt + $i",
                "name $i",
                "desctiption $i",
                i.toString(),
                "id $i",
                i.toFloat(),
            )
            items.add(story)
        }
        return items
    }
}