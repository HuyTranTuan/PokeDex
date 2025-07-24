@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.jetpack.pokedex"
    compileSdk = 35

    packaging {
        resources {
            excludes += "/META-INF/*"
        }
    }

    defaultConfig {
        applicationId = "com.jetpack.pokedex"
        minSdk = 29
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
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.jetbrainsCompose.get() // Or your specific Compose compiler version
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.appdistribution.gradle)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.squareup.okhttp3)
    implementation(libs.squareup.okhttp3.loggingInterceptor)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose.v190)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.ui)
    implementation(libs.material3)
    implementation(libs.ui.tooling.preview)
    androidTestImplementation(libs.ui.test.junit4)
    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.ui.graphics)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation(libs.androidx.compose.material3.material3)
    implementation(libs.coil.compose)
    implementation(libs.androidx.tools.v100alpha13)
    implementation(libs.volley)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.navigation.compose)
}