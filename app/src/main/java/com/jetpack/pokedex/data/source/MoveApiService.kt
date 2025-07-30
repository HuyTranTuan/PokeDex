package com.jetpack.pokedex.data.source

import com.jetpack.pokedex.data.model.MoveListResponse

interface MoveApiCallBack<T>{
    fun onSuccess(data: MoveListResponse)
    fun onError(errorMessage: String)
}

interface MoveApiService {
    fun getMoveList(limit: Int = 937, offset: Int = 0, callback: MoveApiCallBack<MoveListResponse>)
}