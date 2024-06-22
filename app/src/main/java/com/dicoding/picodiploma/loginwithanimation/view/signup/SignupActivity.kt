package com.dicoding.picodiploma.loginwithanimation.view.signup

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dicoding.picodiploma.loginwithanimation.Api.ApiClient
import com.dicoding.picodiploma.loginwithanimation.data.Request.SignInRequest
import com.dicoding.picodiploma.loginwithanimation.data.Response.SigninResponse
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivitySignupBinding
import com.dicoding.picodiploma.loginwithanimation.view.login.LoginActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
        binding.signupButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val name = binding.nameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            if (password.length >= 8 && name.isNotEmpty() && email.isNotEmpty()) {
                val data = SignInRequest(
                    name, email, password
                )

                showProgressBar(true)
                callApi(data)

            } else {
                Toast.makeText(
                    applicationContext,
                    "Email atau nama atau password tidak boleh kosong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun callApi(body: SignInRequest) {
        ApiClient.apiService.registerUsers(body).enqueue(object : Callback<SigninResponse> {
            override fun onResponse(
                call: Call<SigninResponse>,
                response: Response<SigninResponse>
            ) {
                showProgressBar(false)
                if (response.isSuccessful) {
                    val data = response.body()
                    Log.d("ResponseBody", "$data")
                    AlertDialog.Builder(this@SignupActivity).apply {
                        setTitle("Sukses!")
                        setMessage("Pembuatan akun sukses")
                        setPositiveButton("Lanjut") { _, _ -> moveToLogin() }
                        create()
                        show()
                    }
                } else {
                    AlertDialog.Builder(this@SignupActivity).apply {
                        setTitle("Gagal!")
                        setMessage("Pembuatan akun gagal dikarenakan ${response.message()}")
                        setPositiveButton("Lanjut") { _, _ -> }
                        create()
                        show()
                    }
                    Log.d("Response", "Failed to get data. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<SigninResponse>, t: Throwable) {
                showProgressBar(false)
                Log.d("on error", t.toString())
            }
        })
    }

    private fun moveToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
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
