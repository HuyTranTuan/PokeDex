package com.jetpack.pokedex

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.*
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.jetpack.pokedex.data.repository.GenerationRepository
import com.jetpack.pokedex.data.repository.MoveRepository
import com.jetpack.pokedex.data.repository.PokemonRepository
import com.jetpack.pokedex.data.repository.TypeRepository
import com.jetpack.pokedex.data.source.OkHttpApiService
import com.jetpack.pokedex.viewmodel.generation.GenerationViewModel
import com.jetpack.pokedex.viewmodel.generation.GenerationViewModelFactory
import com.jetpack.pokedex.viewmodel.move.MoveViewModel
import com.jetpack.pokedex.viewmodel.move.MoveViewModelFactory
import com.jetpack.pokedex.viewmodel.pokemon.PokemonViewModel
import com.jetpack.pokedex.viewmodel.pokemon.PokemonViewModelFactory
import com.jetpack.pokedex.viewmodel.type.TypeViewModel
import com.jetpack.pokedex.viewmodel.type.TypeViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import kotlinx.coroutines.async


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

        lifecycleScope.launch { // Launch a coroutine in the activity's lifecycle scope
            val pokemonRepositoryDeferred = async(Dispatchers.IO) { PokemonRepository(apiService) }
            val moveRepositoryDeferred = async(Dispatchers.IO) { MoveRepository(apiService) }
            val generationRepositoryDeferred = async(Dispatchers.IO) { GenerationRepository(apiService) }
            val typeRepositoryDeferred = async(Dispatchers.IO) { TypeRepository(apiService) }

            val pokemonRepository = pokemonRepositoryDeferred.await()
            val moveRepository = moveRepositoryDeferred.await()
            val generationRepository = generationRepositoryDeferred.await()
            val typeRepository = typeRepositoryDeferred.await()

            val pokemonViewModelFactory = PokemonViewModelFactory(pokemonRepository)
            val moveViewModelFactory = MoveViewModelFactory(moveRepository)
            val generationViewModelFactory = GenerationViewModelFactory(generationRepository)
            val typeViewModelFactory = TypeViewModelFactory(typeRepository)

            val pokemonViewModel = ViewModelProvider(this@MainActivity, pokemonViewModelFactory)[PokemonViewModel::class.java]
            val moveViewModel = ViewModelProvider(this@MainActivity, moveViewModelFactory)[MoveViewModel::class.java]
            val generationViewModel = ViewModelProvider(this@MainActivity, generationViewModelFactory)[GenerationViewModel::class.java]
            val typeViewModel = ViewModelProvider(this@MainActivity, typeViewModelFactory)[TypeViewModel::class.java]

            // Observe LiveData for logging or UI updates
            // It's better to observe LiveData rather than just polling .value once
            pokemonViewModel.pokemonList.observe(this@MainActivity) {}
            moveViewModel.moveList.observe(this@MainActivity) {}
            typeViewModel.typeList.observe(this@MainActivity) {}
            generationViewModel.generationList.observe(this@MainActivity) {}

            setContent {
                MainScreen(pokemonViewModel, moveViewModel, typeViewModel, generationViewModel)
            }
        }

    }
}
