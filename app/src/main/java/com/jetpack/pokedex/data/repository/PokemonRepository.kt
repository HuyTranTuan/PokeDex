package com.jetpack.pokedex.data.repository

import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.data.model.PokemonListResponse
import com.jetpack.pokedex.data.source.PokemonApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IPokemonRepository {
    suspend fun fetchPokemonList(limit: Int, offset: Int): PokemonListResponse
}

class PokemonRepository(private val apiService: PokemonApiService) : IPokemonRepository {
    override suspend fun fetchPokemonList(limit: Int, offset: Int): PokemonListResponse {
        return withContext(Dispatchers.IO) {
            apiService.getPokemonList(limit, offset)
        }
    }
}