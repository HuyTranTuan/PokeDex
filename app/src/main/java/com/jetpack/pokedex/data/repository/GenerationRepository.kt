package com.jetpack.pokedex.data.repository

import com.jetpack.pokedex.data.model.GenerationDetail
import com.jetpack.pokedex.data.model.GenerationListResponse
import com.jetpack.pokedex.data.source.GenerationApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface IGenerationRepository {
    suspend fun fetchGenerationList(limit: Int, offset: Int) : GenerationListResponse
}

class GenerationRepository(private val apiService: GenerationApiService) : IGenerationRepository{
    override suspend fun fetchGenerationList(limit: Int, offset: Int): GenerationListResponse {
        return withContext(Dispatchers.IO) {
            apiService.getGenerationList(limit, offset)
        }
    }
}