package com.dicoding.picodiploma.loginwithanimation.view.uploadPage

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.load
import coil.transform.CircleCropTransformation
import com.dicoding.picodiploma.loginwithanimation.Api.ApiClient
import com.dicoding.picodiploma.loginwithanimation.data.Constant
import com.dicoding.picodiploma.loginwithanimation.data.LatLong
import com.dicoding.picodiploma.loginwithanimation.data.Response.UploadResponse
import com.dicoding.picodiploma.loginwithanimation.data.sharedPreference.SharedPreferenceHelper
import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityUploadBinding
import com.dicoding.picodiploma.loginwithanimation.reduceFileImage
import com.dicoding.picodiploma.loginwithanimation.uriToFile
import com.dicoding.picodiploma.loginwithanimation.view.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.google.android.gms.maps.model.LatLng

class UploadActivity : AppCompatActivity() {

    private lateinit var binding : ActivityUploadBinding
    private val GALLERY_REQUEST_CODE = 1
    private val CAMERA_REQUEST_CODE = 2
    private val WRITE_EXTERNAL_STORAGE = 3

    private var photo : File? = null
    private lateinit var pref : SharedPreferenceHelper
    private lateinit var currentPhotoPath: String

    private var photoBitmap : Bitmap? = null

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var latLng : LatLong

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = SharedPreferenceHelper(this)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        latLng = LatLong(null, null)
        checkLocationPermission()
        setupView()
        val token = pref.getToken(Constant.TOKEN_KEY)
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getLastKnownLocation()
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLocation = LatLng(location.latitude, location.longitude)
                latLng.lat = currentLocation.latitude.toString()
                latLng.long = currentLocation.longitude.toString()
            }
        }
    }

    private fun setupView() {
        binding.btnUpload.setOnClickListener {
            uploadData()
        }

        binding.btnCamera.setOnClickListener {
            openCamera()
        }

        binding.btnGalery.setOnClickListener {
            openGaleri()
        }
    }

    private fun openGaleri() {
        Dexter.withContext(this).withPermissions(
            Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(object : PermissionListener, MultiplePermissionsListener {
            override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                gallery()
            }

            override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                Toast.makeText(this@UploadActivity, "You Have Denied the permission", Toast.LENGTH_SHORT).show()
                showDialogForPermission()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: PermissionRequest?,
                p1: PermissionToken?
            ) {
                showDialogForPermission()
            }

            override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                gallery()
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                p1: PermissionToken?
            ) {
                showDialogForPermission()
            }
        }).onSameThread().check()
    }

    private fun gallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    private fun openCamera() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()) {
                            camera()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showDialogForPermission()
                }
            }).onSameThread().check()
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        com.dicoding.picodiploma.loginwithanimation.createTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "com.dicoding.picodiploma.loginwithanimation",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)
            photo = myFile

            val result = BitmapFactory.decodeFile(photo?.path)

            binding.imageView3.load(result) {
                crossfade(true)
                crossfade(3000)
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun camera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA_REQUEST_CODE)

        com.dicoding.picodiploma.loginwithanimation.createTempFile(application).also {
            currentPhotoPath = it.absolutePath
        }
    }

    private fun uploadData() {
        val textDescripton = binding.editTextTextMultiLine.text.toString()
        Log.d("data", "$textDescripton")

        if (textDescripton.isEmpty() || photo == null) {
            Toast.makeText(this@UploadActivity, "Data Cannot Be Empty", Toast.LENGTH_SHORT).show()
        } else {
            val token = pref.getToken(Constant.TOKEN_KEY)
            sendDataToServer(token!!, photo!!, textDescripton)
        }
    }

    private fun sendDataToServer(token: String, foto: File, desc: String) {
        Log.d("tokenbaru", "$token")
        Log.d("foto", "$foto")

        val file: File = if (photoBitmap != null) {
            val filePath = saveBitmapToFile(photoBitmap!!)
            File(filePath)
        } else {
            reduceFileImage(foto)
        }

        val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            foto.name,
            requestImageFile
        )

        val latitude = latLng.lat?.toFloatOrNull()
        val longitude = latLng.long?.toFloatOrNull()

        ApiClient.apiService.uploadImage(
            token,
            imageMultipart,
            desc.toRequestBody("text/plain".toMediaType()),
            latitude ?: 0f,
            longitude ?: 0f
        ).enqueue(object : retrofit2.Callback<UploadResponse> {
            override fun onResponse(
                call: retrofit2.Call<UploadResponse>,
                response: retrofit2.Response<UploadResponse>
            ) {
                if (response.isSuccessful) {
                    successUpload()
                } else {
                    Toast.makeText(this@UploadActivity, "Failed to send data, code: ${response.code()}", Toast.LENGTH_LONG).show()
                    Log.d("Response", "Failed to get data. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: retrofit2.Call<UploadResponse>, t: Throwable) {
                println(t.toString())
            }
        })
    }

    private fun successUpload() {
        startActivity(Intent(this@UploadActivity, MainActivity::class.java))
        Toast.makeText(this, "Berhasil Upload ke server", Toast.LENGTH_LONG).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val bitmap = data?.extras?.get("data") as Bitmap

                    binding.imageView3.load(bitmap) {
                        crossfade(true)
                        crossfade(3000)
                        transformations(CircleCropTransformation())
                    }

                    val myFile = File(currentPhotoPath)
                    photoBitmap = bitmap
                    photo = myFile
                }
                GALLERY_REQUEST_CODE -> {
                    binding.imageView3.load(data?.data) {
                        crossfade(true)
                        crossfade(1000)
                        transformations(CircleCropTransformation())
                    }

                    val selectedImg: Uri = data?.data!!
                    val myFile = uriToFile(selectedImg, this)
                    photo = myFile
                }
            }
        }
    }

    private fun showDialogForPermission() {
        AlertDialog.Builder(this)
            .setMessage("You have denied the permission, please allow permission in setting")
            .setPositiveButton("Go To Setting") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun saveBitmapToFile(bitmap: Bitmap): String? {
        val filePath = File(getExternalFilesDir(null), "image.jpg").absolutePath
        return try {
            val file = File(filePath)
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            filePath // Kembalikan path file jika berhasil
        } catch (e: IOException) {
            e.printStackTrace()
            null // Kembalikan null jika terjadi kesalahan
        }
    }
}
