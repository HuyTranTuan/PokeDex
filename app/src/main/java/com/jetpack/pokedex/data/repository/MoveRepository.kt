package com.jetpack.pokedex.data.repository

import com.jetpack.pokedex.data.model.MoveListResponse
import com.jetpack.pokedex.data.source.MoveApiCallBack
import com.jetpack.pokedex.data.source.MoveApiService

interface IMoveRepository {
    fun getMoveList(
        limit: Int,
        offset: Int,
        callback: MoveApiCallBack<MoveListResponse>
    )
}

class MoveRepository(private val apiService: MoveApiService) : IMoveRepository {
    override fun getMoveList(
        limit: Int,
        offset: Int,
        callback: MoveApiCallBack<MoveListResponse>
    ) {
        apiService.getMoveList(limit, offset, callback)
    }
}