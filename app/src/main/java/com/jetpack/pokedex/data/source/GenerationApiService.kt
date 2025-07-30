package com.jetpack.pokedex.data.source

import com.jetpack.pokedex.data.model.GenerationListResponse

interface GenerationApiCallBack<T>{
    fun onSuccess(data: GenerationListResponse)
    fun onError(errorMessage: String)
}

interface GenerationApiService {
    fun getGenerationList(limit: Int = 9, offset: Int = 0, callback: GenerationApiCallBack<GenerationListResponse>)
}