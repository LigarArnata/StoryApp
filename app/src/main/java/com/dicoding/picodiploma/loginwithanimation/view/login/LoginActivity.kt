package com.dicoding.picodiploma.loginwithanimation.view.login

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.Api.ApiClient
import com.dicoding.picodiploma.loginwithanimation.data.Request.LoginRequest
import com.dicoding.picodiploma.loginwithanimation.data.Response.LoginResponse
import com.dicoding.picodiploma.loginwithanimation.data.pref.dataModel.UserModel
import com.dicoding.picodiploma.loginwithanimation.data.Constant
import com.dicoding.picodiploma.loginwithanimation.data.sharedPreference.SharedPreferenceHelper
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityLoginBinding
import com.dicoding.picodiploma.loginwithanimation.view.ViewModelFactory
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding
    private lateinit var pref: SharedPreferenceHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPreferenceHelper(this)

        setupView()
        setupAction()
        animateImageView()
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
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (email.isNotEmpty() && password.length >= 8) {
                val data = LoginRequest(email, password)
                showProgressBar(true)
                callApi(data)
            }
        }
    }

    private fun callApi(body: LoginRequest) {
        ApiClient.apiService.loginUsers(body).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                showProgressBar(false)
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("ResponseBody", "$data")
                    pref.putToken(Constant.TOKEN_KEY, data!!)
                    viewModel.saveSession(UserModel(data.loginResult.name!!, "sample_token"))

                    AlertDialog.Builder(this@LoginActivity).apply {
                        setTitle("Sukses!")
                        setMessage("Sukses Login")
                        setPositiveButton("Lanjut") { _, _ -> moveToMain() }
                        create()
                        show()
                    }
                } else {
                    AlertDialog.Builder(this@LoginActivity).apply {
                        setTitle("Gagal!")
                        setMessage("Password atau email salah")
                        setPositiveButton("Lanjut") { _, _ -> }
                        create()
                        show()
                    }
                    Log.d("Response", "Failed to get data. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showProgressBar(false)
                Log.d("on error", t.toString())
            }
        })
    }

    private fun moveToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun animateImageView() {
        val imageView = binding.imageView
        imageView.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f).apply {
            duration = 2000
            start()
        }
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }
}
