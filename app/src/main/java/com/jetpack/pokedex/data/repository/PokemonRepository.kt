package com.jetpack.pokedex.data.repository

import com.jetpack.pokedex.data.model.PokemonListResponse
import com.jetpack.pokedex.data.source.PokemonApiCallBack
import com.jetpack.pokedex.data.source.PokemonApiService

interface IPokemonRepository {
    fun getPokemonList(
        limit: Int,
        offset: Int,
        callback: PokemonApiCallBack<PokemonListResponse>
    )
}

class PokemonRepository(private val apiService: PokemonApiService) : IPokemonRepository {
    override fun getPokemonList(
        limit: Int,
        offset: Int,
        callback: PokemonApiCallBack<PokemonListResponse>
    ) {
        apiService.getPokemonList(limit, offset, callback)
    }
}