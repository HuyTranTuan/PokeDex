package com.jetpack.pokedex.data.source

import com.jetpack.pokedex.data.model.PokemonListResponse

interface PokemonApiCallBack<T>{
    fun onSuccess(data: PokemonListResponse)
    fun onError(errorMessage: String)
}

interface PokemonApiService {
    fun getPokemonList(limit: Int = 1302, offset: Int = 0, callback: PokemonApiCallBack<PokemonListResponse>)
}