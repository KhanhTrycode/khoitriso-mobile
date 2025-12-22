import java.util.Properties

val localProps = Properties().apply {
    val file = rootProject.file("local.properties")
    if (file.exists()) load(file.inputStream())
}


plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.example.khoitriso"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.khoitriso"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            buildConfigField("String", "API_KEY", "\"${localProps.getProperty("API_KEY")}\"")
            buildConfigField("String", "GOOGLE_CLIENT_ID", "\"${localProps.getProperty("GOOGLE_CLIENT_ID")}\"")
            buildConfigField("Boolean", "DEBUG", "\"${localProps.getProperty("DEBUG")}\"")

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true

    }
}

dependencies {
    // --- Compose core ---
    implementation(libs.androidx.ui)
    implementation(libs.androidx.foundation)
    implementation(libs.androidx.ui.tooling.preview)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    testImplementation(libs.junit.junit)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.material3)
    // --- ConstraintLayout + BoxWithConstraints ---
    implementation(libs.androidx.constraintlayout.compose)

    // --- Animation ---
    implementation(libs.androidx.animation)
    implementation(libs.animation.core)
    implementation(libs.animation.graphics)

    // --- Lifecycle + ViewModel ---
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.viewmodel.compose)

    // --- Navigation ---
    implementation(libs.androidx.navigation.compose)

    // --- Hilt (DI) ---
    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    // --- Retrofit + OkHttp ---
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // --- Room Database ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.androidx.room.compiler)
    implementation(libs.androidx.room.paging)

    // --- DataStore (Preferences) ---
    implementation(libs.androidx.datastore.preferences)

    // --- Coroutines + Flow ---
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)

    // --- Coil (load ảnh Compose) ---
    implementation(libs.coil.compose)

    // --- Timber (logging) ---
    implementation(libs.timber)

    // --- Paging Compose ---
    implementation(libs.androidx.paging.compose)
    implementation (libs.androidx.paging.runtime)

    // --- Accompanist (animation/nav/permission/insets) ---
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.accompanist.insets)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.test.manifest)
    testImplementation ("org.mockito:mockito-core:5.5.0")
    testImplementation ("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.1")


    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation(libs.java.jwt)
    implementation ("com.google.android.gms:play-services-auth:20.6.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")
    implementation ("androidx.browser:browser:1.5.0")

    // --- Exo Player ---
    implementation(libs.androidx.media3.exoplayer)
    implementation(libs.androidx.media3.ui)

    implementation ("com.google.accompanist:accompanist-pager:0.30.1")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.30.1")
    
    // --- SignalR ---
    implementation("com.microsoft.signalr:signalr:6.0.8")
    implementation("androidx.compose.material:material-icons-extended:1.7.8") // Check for the latest version

    // Markwon core
    implementation ("io.noties.markwon:core:4.6.2")
    // Hỗ trợ HTML tag
    implementation ("io.noties.markwon:html:4.6.2")
    // Hỗ trợ LaTeX (Toán học)
    implementation ("io.noties.markwon:ext-latex:4.6.2")
    // Hỗ trợ hình ảnh
    implementation ("io.noties.markwon:image:4.6.2")
    // Hỗ trợ Inline parser (để xử lý text tốt hơn)
    implementation(libs.kotlinx.serialization.json) // Use the latest version
    implementation ("io.noties.markwon:inline-parser:4.6.2")
    implementation("androidx.appcompat:appcompat:1.6.1")
}

