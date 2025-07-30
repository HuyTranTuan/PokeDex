package com.jetpack.pokedex.data.source

import com.jetpack.pokedex.data.model.TypeListResponse

interface TypeApiCallBack<T>{
    fun onSuccess(data: TypeListResponse)
    fun onError(errorMessage: String)
}

interface TypeApiService {
    fun getTypeList(limit: Int = 9, offset: Int = 0, callback: TypeApiCallBack<TypeListResponse>)
}