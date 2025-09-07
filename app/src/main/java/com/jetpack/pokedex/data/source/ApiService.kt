package com.jetpack.pokedex.data.source

import com.jetpack.pokedex.data.model.GenerationDetail
import com.jetpack.pokedex.data.model.GenerationListResponse
import com.jetpack.pokedex.data.model.MoveDetail
import com.jetpack.pokedex.data.model.MoveListResponse
import com.jetpack.pokedex.data.model.Pokemon
import com.jetpack.pokedex.data.model.PokemonListResponse
import com.jetpack.pokedex.data.model.TypeDetail
import com.jetpack.pokedex.data.model.TypeListResponse

interface PokemonApiService {
    suspend fun getPokemonList(limit: Int = 1302, offset: Int = 0) : PokemonListResponse
}

interface MoveApiService {
    suspend fun getMoveList(limit: Int = 937, offset: Int = 0) : MoveListResponse
}

interface GenerationApiService {
    suspend fun getGenerationList(limit: Int = 9, offset: Int = 0) : GenerationListResponse
}

interface TypeApiService {
    suspend fun getTypeList(limit: Int = 9, offset: Int = 0) : TypeListResponse
}