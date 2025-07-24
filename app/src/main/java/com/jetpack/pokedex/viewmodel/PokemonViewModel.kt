@file:Suppress("UNCHECKED_CAST")

package com.jetpack.pokedex.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.data.model.PokemonListResponse
import com.jetpack.pokedex.data.repository.IPokemonRepository // Use interface
import com.jetpack.pokedex.data.source.ApiCallBack

open class PokemonViewModel(private val repository: IPokemonRepository) : ViewModel() {
    // LiveData to hold the list of Pokémon
    private val _pokemonList = MutableLiveData<List<Pokemon>>()
    open val pokemonList: LiveData<List<Pokemon>> = _pokemonList

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    open val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    open val errorMessage: MutableLiveData<String?> = _errorMessage

    // Keep track of pagination
    private var currentOffset = 0
    private val pageSize = 1100
    open var canLoadMore: Boolean = true

    init {
        fetchPokemon() // Initial fetch
    }

    fun fetchPokemon(loadMore: Boolean = false) {
        if (_isLoading.value == true) return // Prevent multiple simultaneous loads

        if (loadMore) {
            if (!canLoadMore) return // No more items to load
            currentOffset += pageSize
        } else {
            // Reset for a fresh load or initial load
            currentOffset = 0
            _pokemonList.value = emptyList() // Clear previous list for a fresh load
            canLoadMore = true
        }

        _isLoading.value = true

        repository.getPokemonList(
            limit = pageSize,
            offset = currentOffset,
            callback = object : ApiCallBack<PokemonListResponse> {
                override fun onSuccess(data: PokemonListResponse) {
                    val currentList = if (loadMore) _pokemonList.value ?: emptyList() else emptyList()
                    _pokemonList.postValue(currentList + data.results) // Use postValue if called from background

                    canLoadMore = true // Update if there's a next page
                    _isLoading.postValue(false)
                }

                override fun onError(message: String) {
                    _errorMessage.postValue(message)
                    _isLoading.postValue(false)
                    if (loadMore) { // If loading more failed, revert offset
                        currentOffset -= pageSize
                        if (currentOffset < 0) currentOffset = 0
                    }
                }
            }
        )
    }

    fun getPokemonDetailById(pokemonId: String): Pokemon? {
        return pokemonList.value?.find { it.id == pokemonId }
    }

    fun getPokemonDetailByName(pokemonName: String): Pokemon? {
        return pokemonList.value?.find { it.name == pokemonName }
    }
}

