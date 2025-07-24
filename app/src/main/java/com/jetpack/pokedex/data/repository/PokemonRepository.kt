package com.jetpack.pokedex.data.repository

import com.jetpack.pokedex.data.model.PokemonListResponse
import com.jetpack.pokedex.data.source.ApiCallBack
import com.jetpack.pokedex.data.source.ApiService

// Interface for the Repository (optional but good for testing/DI)
interface IPokemonRepository {
    fun getPokemonList(
        limit: Int,
        offset: Int,
        callback: ApiCallBack<PokemonListResponse>
    )
}

class PokemonRepository(private val apiService: ApiService) : IPokemonRepository {
    override fun getPokemonList(
        limit: Int,
        offset: Int,
        callback: ApiCallBack<PokemonListResponse>
    ) {
        apiService.getPokemonList(limit, offset, callback)
    }
}