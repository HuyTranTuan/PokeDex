package com.jetpack.pokedex.data.repository

import com.jetpack.pokedex.data.model.TypeDetail
import com.jetpack.pokedex.data.model.TypeListResponse
import com.jetpack.pokedex.data.source.TypeApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface ITypeRepository {
    suspend fun fetchTypeList(limit: Int, offset: Int) : TypeListResponse
}

class TypeRepository(private val apiService: TypeApiService) : ITypeRepository {
    override suspend fun fetchTypeList(limit: Int, offset: Int): TypeListResponse {
        return withContext(Dispatchers.IO) {
            apiService.getTypeList(limit, offset)
        }
    }
}