package com.jetpack.pokedex.data.repository

import com.jetpack.pokedex.data.model.GenerationListResponse
import com.jetpack.pokedex.data.source.GenerationApiCallBack
import com.jetpack.pokedex.data.source.GenerationApiService

interface IGenerationRepository {
    fun getGenerationList(
        limit: Int,
        offset: Int,
        callback: GenerationApiCallBack<GenerationListResponse>
    )
}

class GenerationRepository(private val apiService: GenerationApiService) : IGenerationRepository{
    override fun getGenerationList(
        limit: Int,
        offset: Int,
        callback: GenerationApiCallBack<GenerationListResponse>
    ) {
        apiService.getGenerationList(limit, offset, callback)
    }
}