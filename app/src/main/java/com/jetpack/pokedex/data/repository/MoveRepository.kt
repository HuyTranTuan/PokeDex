package com.jetpack.pokedex.data.repository

import com.jetpack.pokedex.data.model.MoveDetail
import com.jetpack.pokedex.data.model.MoveListResponse
import com.jetpack.pokedex.data.source.MoveApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IMoveRepository {
    suspend fun fetchMoveList(limit: Int, offset: Int) : MoveListResponse
}

class MoveRepository(private val apiService: MoveApiService) : IMoveRepository {
    override suspend fun fetchMoveList(limit: Int, offset: Int): MoveListResponse {
        return withContext(Dispatchers.IO) {
            apiService.getMoveList(limit, offset)
        }
    }
}