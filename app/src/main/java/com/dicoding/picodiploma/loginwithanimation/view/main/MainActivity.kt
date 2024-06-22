package com.dicoding.picodiploma.loginwithanimation.view.main

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.picodiploma.loginwithanimation.Api.ApiClient
import com.dicoding.picodiploma.loginwithanimation.MyAdapter
import com.dicoding.picodiploma.loginwithanimation.R
import com.dicoding.picodiploma.loginwithanimation.data.Constant
import com.dicoding.picodiploma.loginwithanimation.data.Response.ListStoryResponse
import com.dicoding.picodiploma.loginwithanimation.data.sharedPreference.SharedPreferenceHelper
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityMainBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.detail.detailActivity
import com.dicoding.picodiploma.loginwithanimation.view.maps.MapsActivity
import com.dicoding.picodiploma.loginwithanimation.view.uploadPage.UploadActivity
import com.dicoding.picodiploma.loginwithanimation.view.welcome.WelcomeActivity
import com.dicoding.picodiploma.loginwithanimation.viewModel.MyViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), MainContract {
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RVAdapter
    private lateinit var adapter2: MyAdapter
    private lateinit var pref: SharedPreferenceHelper

    private val viewModel2 by viewModels<MyViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPreferenceHelper.getInstance(this)
        adapter2 = MyAdapter(this@MainActivity, this)
        adapter = RVAdapter(this@MainActivity, this, mutableListOf())
        binding.rvMain.adapter = adapter2
        binding.rvMain.layoutManager = LinearLayoutManager(this)

        val token = pref.getToken(Constant.TOKEN_KEY)

        viewModel.getSession().observe(this) { user ->
            println(user)
            if (!user.isLogin) {
                println("belum login")
                startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))
            } else {
                println("udah login")
                val token = pref.getToken(Constant.TOKEN_KEY)
            }
        }

        lifecycleScope.launch {
            viewModel2.myDataFlow.observe(this@MainActivity) { pagingData ->
                adapter2.submitData(lifecycle, pagingData)
            }
        }

        setupView()
        setupAction()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.floatingActionButton.setOnClickListener {
            viewModel.logout()
            pref.deleteData()
        }

        binding.btnToUpload.setOnClickListener {
            startActivity(Intent(this@MainActivity, UploadActivity::class.java))
        }

        binding.btnToMaps.setOnClickListener {
            startActivity(Intent(this@MainActivity, MapsActivity::class.java))
        }
    }

    private fun setupAdapter(response: ListStoryResponse) {
        adapter.setData(response.listStory.toMutableList())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var itemClicked = ""

        when (item.itemId) {
            R.id.btnAdd -> {
                itemClicked = "add Clicked"
            }
            R.id.btnLogout -> {
                itemClicked = "logout Clicked"
            }
        }

        Toast.makeText(applicationContext, itemClicked, Toast.LENGTH_LONG).show()
        return super.onOptionsItemSelected(item)
    }

    override fun moveToDetail(id: String, photo: String, desc: String, name: String) {
        val intent = Intent(this@MainActivity, detailActivity::class.java)
        intent.putExtra(Constant.KEY_ID, id)
        intent.putExtra(Constant.KEY_PHOTO, photo)
        intent.putExtra(Constant.KEY_DESC, desc)
        intent.putExtra(Constant.KEY_NAME, name)
        startActivity(intent)
    }
}