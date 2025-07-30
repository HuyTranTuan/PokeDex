package com.jetpack.pokedex.data.repository

import com.jetpack.pokedex.data.model.TypeListResponse
import com.jetpack.pokedex.data.source.TypeApiCallBack
import com.jetpack.pokedex.data.source.TypeApiService

interface ITypeRepository {
    fun getTypeList(
        limit: Int,
        offset: Int,
        callback: TypeApiCallBack<TypeListResponse>
    )
}

class TypeRepository(private val apiService: TypeApiService) : ITypeRepository {
    override fun getTypeList(
        limit: Int,
        offset: Int,
        callback: TypeApiCallBack<TypeListResponse>
    ) {
        apiService.getTypeList(limit, offset, callback)
    }
}