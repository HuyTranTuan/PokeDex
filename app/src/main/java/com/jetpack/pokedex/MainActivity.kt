package com.jetpack.pokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.*
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jetpack.pokedex.data.repository.PokemonRepository
import com.jetpack.pokedex.data.source.OkHttpApiService
import com.jetpack.pokedex.pages.PokemonListScreen
import com.jetpack.pokedex.viewmodel.PokemonViewModel
import com.jetpack.pokedex.viewmodel.PokemonViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlin.collections.get


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)
        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(3000)
            keepSplashScreen = false
        }
        enableEdgeToEdge()

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
        val apiService = OkHttpApiService(okHttpClient)
        val repository = PokemonRepository(apiService)
        val viewModelFactory = PokemonViewModelFactory(repository)
        var viewModel = ViewModelProvider(this, viewModelFactory)[PokemonViewModel::class.java]
        setContent {
            MainScreen(viewModel= viewModel)
        }

    }
}
