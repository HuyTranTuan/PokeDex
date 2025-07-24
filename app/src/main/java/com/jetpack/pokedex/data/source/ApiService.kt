package com.jetpack.pokedex.data.source

import com.jetpack.pokedex.data.model.PokemonListResponse

interface ApiCallBack<T>{
    fun onSuccess(data: T)
    fun onError(errorMessage: String)
}

interface ApiService {
    fun getPokemonList(limit: Int = 1100, offset: Int = 0, callback: ApiCallBack<PokemonListResponse>)
}