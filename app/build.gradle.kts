plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    namespace = "com.dicoding.picodiploma.loginwithanimation"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.dicoding.picodiploma.loginwithanimation"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            setProguardFiles(listOf(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    testOptions.unitTests.isReturnDefaultValues = true
}

dependencies {

    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.paging:paging-common-android:3.3.0")
    implementation("androidx.compose.ui:ui-desktop:1.6.8")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.1")
    implementation("androidx.activity:activity-ktx:1.7.2")
    implementation ("com.squareup.picasso:picasso:2.8")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.2")

    // uses permission
    implementation("com.karumi:dexter:6.2.2")

    // coil
    implementation("io.coil-kt:coil:1.1.1")

    // get location
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.google.android.gms:play-services-maps:17.0.0")

    implementation("androidx.paging:paging-runtime:3.1.1") // atau versi terbaru

    testImplementation("io.mockk:mockk:1.10.6")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")

    androidTestImplementation("androidx.arch.core:core-testing:2.1.0") //InstantTaskExecutorRule
    androidTestImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1") //TestDispatcher

    testImplementation("androidx.arch.core:core-testing:2.1.0") // InstantTaskExecutorRule
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1") //TestDispatcher
    testImplementation("org.mockito:mockito-core:3.11.2")
    testImplementation("org.mockito:mockito-inline:3.11.2")
    testImplementation ("org.jetbrains.kotlin:kotlin-reflect:1.5.21")





}