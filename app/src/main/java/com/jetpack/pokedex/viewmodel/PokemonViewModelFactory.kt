package com.jetpack.pokedex.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jetpack.pokedex.data.repository.IPokemonRepository

class PokemonViewModelFactory(
    private val pokemonRepository: IPokemonRepository // Or PokemonRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PokemonViewModel::class.java)) {
            return PokemonViewModel(pokemonRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}