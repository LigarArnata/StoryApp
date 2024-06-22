package com.dicoding.picodiploma.loginwithanimation.view.detail

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.data.Constant
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso

class detailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.getStringExtra(Constant.KEY_ID)
        val name = intent.getStringExtra(Constant.KEY_NAME)
        val desc = intent.getStringExtra(Constant.KEY_DESC)
        val photoUrl = intent.getStringExtra(Constant.KEY_PHOTO)
        Log.d("id",id!!)

        setupView(name!!,desc!!,photoUrl!!)


    }

    private fun setupView(name:String,desc: String,photoUrl: String){
        binding.tvDetailName.text = name
        binding.tvDetailDesc.text = desc
        Picasso.get().load(photoUrl).into(binding.imageView2)
    }
}