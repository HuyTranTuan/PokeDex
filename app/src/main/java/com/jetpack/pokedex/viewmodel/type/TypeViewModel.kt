package com.jetpack.pokedex.viewmodel.type

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jetpack.pokedex.data.model.TypeDetail
import com.jetpack.pokedex.data.model.TypeListResponse
import com.jetpack.pokedex.data.repository.ITypeRepository
import com.jetpack.pokedex.data.source.TypeApiCallBack

open class TypeViewModel (private val repository: ITypeRepository) : ViewModel() {
    // LiveData to hold the list of Pokémon
    private val _typeList = MutableLiveData<List<TypeDetail>>()
    open val typeList: LiveData<List<TypeDetail>> = _typeList

    // LiveData for loading state
    private val _isLoading = MutableLiveData<Boolean>()
    open val isLoading: LiveData<Boolean> = _isLoading

    // LiveData for error messages
    private val _errorMessage = MutableLiveData<String?>()
    open val errorMessage: MutableLiveData<String?> = _errorMessage

    // Keep track of pagination
    private var currentOffset = 0
    private val pageSize = 937
    open var canLoadMore: Boolean = true

    init {
        fetchType() // Initial fetch
    }

    fun fetchType(loadMore: Boolean = false) {
        if (_isLoading.value == true) return // Prevent multiple simultaneous loads

        if (loadMore) {
            if (!canLoadMore) return // No more items to load
            currentOffset += pageSize
        } else {
            // Reset for a fresh load or initial load
            currentOffset = 0
            _typeList.value = emptyList() // Clear previous list for a fresh load
            canLoadMore = true
        }

        _isLoading.value = true

        repository.getTypeList(
            limit = pageSize,
            offset = currentOffset,
            callback = object : TypeApiCallBack<TypeListResponse> {
                override fun onSuccess(data: TypeListResponse) {
                    val currentList =
                        if (loadMore) _typeList.value ?: emptyList() else emptyList()
                    _typeList.postValue((currentList + data.results))

                    canLoadMore = true // Update if there's a next page
                    _isLoading.postValue(false)
                }

                override fun onError(message: String) {
                    _errorMessage.postValue(message)
                    _isLoading.postValue(false)
                    if (loadMore) { // If loading more failed, revert offset
                        currentOffset -= pageSize
                        if (currentOffset < 0) currentOffset = 0
                    }
                }
            }
        )
    }

    fun getTypeDetailById(typeId: String): TypeDetail? {
        return typeList.value?.find { it.id == typeId }
    }

    fun getTypeDetailByName(typeId: String): TypeDetail? {
        return typeList.value?.find { it.name == typeId }
    }
}