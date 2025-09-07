package com.jetpack.pokedex.viewmodel.pokemon

import android.util.Log
import kotlinx.coroutines.launch
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.data.model.PokemonListResponse
import com.jetpack.pokedex.data.repository.PokemonRepository

open class PokemonViewModel(private val repository: PokemonRepository) : ViewModel() {
    // LiveData to hold the list of Pokémon
    private val _pokemonList = MutableLiveData<List<Pokemon>>()
    open val pokemonList: LiveData<List<Pokemon>> = _pokemonList

    private val _pokemonDetail = MutableLiveData<Pokemon>()
    open val pokemon: LiveData<Pokemon> = _pokemonDetail

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    open val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    open val errorMessage: MutableLiveData<String?> = _errorMessage

    // Keep track of pagination
    private var currentOffset = 0
    private val pageSize = 1302
    open var canLoadMore: Boolean = true

    fun fetchPokemon(loadMore: Boolean = false) {
        viewModelScope.launch {
            if (_isLoading.value == true) return@launch

            if (loadMore) {
                if (!canLoadMore) return@launch
                currentOffset += pageSize
            } else {
                currentOffset = 0
                _pokemonList.value = emptyList()
                canLoadMore = true
            }
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response: PokemonListResponse = repository.fetchPokemonList(pageSize, currentOffset)
                _pokemonList.value = response.results
            } catch (e: Exception) {
                Log.e("PokemonViewModel", "Error loading Pokemon list", e)
                _errorMessage.value = "Failed to load Pokémon: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getPokemonDetailByName(pokemonName: String): Pokemon? {
        return pokemon.value ?: pokemonList.value?.find { it.name == pokemonName }
    }
}

