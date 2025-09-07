package com.jetpack.pokedex.di

import com.jetpack.pokedex.data.repository.GenerationRepository
import com.jetpack.pokedex.data.repository.MoveRepository
import com.jetpack.pokedex.data.repository.PokemonRepository
import com.jetpack.pokedex.data.repository.TypeRepository
import com.jetpack.pokedex.data.source.GenerationApiService
import com.jetpack.pokedex.data.source.MoveApiService
import com.jetpack.pokedex.data.source.OkHttpApiService
import com.jetpack.pokedex.data.source.PokemonApiService
import com.jetpack.pokedex.data.source.TypeApiService
import com.jetpack.pokedex.viewmodel.generation.GenerationViewModel
import com.jetpack.pokedex.viewmodel.move.MoveViewModel
import com.jetpack.pokedex.viewmodel.pokemon.PokemonViewModel
import com.jetpack.pokedex.viewmodel.type.TypeViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {
    single {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // 1. Define the SINGLETON for the CONCRETE class OkHttpApiService
    single {
        OkHttpApiService(get()) // `get()` here resolves OkHttpClient
    }

    // 2. Now, for each interface, tell Koin to use the
    //    EXISTING OkHttpApiService singleton when that interface is requested.
    single<PokemonApiService> {
        get<OkHttpApiService>()
    }

    single<MoveApiService> {
        get<OkHttpApiService>()
    }

    single<GenerationApiService> {
        get<OkHttpApiService>()
    }

    single<TypeApiService> {
        get<OkHttpApiService>()
    }

    // Repositories - these will now correctly get the OkHttpApiService instance
    // cast to their required interface type automatically by Kotlin.
    single {
        PokemonRepository(get())
    }
    single {
        MoveRepository(get())
    }
    single {
        GenerationRepository(get())
    }
    single {
        TypeRepository(get())
    }

    // ViewModel definitions
    viewModel {
        PokemonViewModel(get())
    }
    viewModel {
        MoveViewModel(get())
    }
    viewModel {
        TypeViewModel(get())
    }
    viewModel {
        GenerationViewModel(get())
    }
}