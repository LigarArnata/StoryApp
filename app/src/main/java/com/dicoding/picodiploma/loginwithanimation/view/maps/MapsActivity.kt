package com.dicoding.picodiploma.loginwithanimation.view.maps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.dicoding.picodiploma.loginwithanimation.Api.ApiClient
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.Constant
import com.dicoding.picodiploma.loginwithanimation.data.LatLong
import com.dicoding.picodiploma.loginwithanimation.data.Response.DetailStory
import com.dicoding.picodiploma.loginwithanimation.data.Response.ListStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.sharedPreference.SharedPreferenceHelper
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsActivity : AppCompatActivity(),OnMapReadyCallback {

    private lateinit var mMap : GoogleMap
    private lateinit var binding : ActivityMapsBinding
    private lateinit var list : MutableList<LatLng>
    private lateinit var listOfDesc : MutableList<DetailStory>
    private lateinit var pref : SharedPreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        list = mutableListOf()
        listOfDesc = mutableListOf()
        pref = SharedPreferenceHelper(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)


        val token = pref.getToken(Constant.TOKEN_KEY)

        getDataLocation(token!!)

    }

    private fun setMarker(markers: List<LatLng>, stories: List<DetailStory>) {
        if (markers.isEmpty() || stories.isEmpty()) return

        val builder = LatLngBounds.Builder()

        stories.forEach { story ->
            val lat = story.lat ?: return@forEach
            val lon = story.lon ?: return@forEach

            val position = LatLng(lat.toDouble(), lon.toDouble())
            mMap.addMarker(MarkerOptions().position(position).title("${story.name}, ${story.description}"))
            builder.include(position)

            println(position)
            println("${story.name} ${story.description}")
        }

        val bounds = builder.build()
        val padding = 100
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        mMap.moveCamera(cameraUpdate)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }

    private fun getDataLocation(token: String){
        ApiClient.apiService.getListStoriesWithLoc(token).enqueue(object : Callback<ListStoryResponse> {
            override fun onResponse(
                call: Call<ListStoryResponse>,
                response: Response<ListStoryResponse>
            ) {

                if (response.isSuccessful) {
                    val listStory = response.body()?.listStory
                    val toMutableList = listStory?.toMutableList()
                    listOfDesc = toMutableList!!
                    listStory?.forEach { t->
                        var dataLatLng = LatLng(t.lat?.toDouble()!!,t.lon?.toDouble()!!)
                        list.add(dataLatLng)
                    }

                    if (list.isNotEmpty() && listOfDesc.isNotEmpty()){
                        println(listOfDesc)
                        setMarker(list,listOfDesc)
                    }
                } else {
                    Log.d("Response", "Failed to get data. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                println(t.toString())
            }
        })
    }
}

