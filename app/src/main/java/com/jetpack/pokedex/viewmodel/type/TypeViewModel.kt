package com.jetpack.pokedex.viewmodel.type

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.pokedex.data.model.TypeDetail
import com.jetpack.pokedex.data.model.TypeListResponse
import com.jetpack.pokedex.data.repository.TypeRepository
import kotlinx.coroutines.launch

open class TypeViewModel (private val repository: TypeRepository) : ViewModel() {
    // LiveData to hold the list of Pokémon
    private val _typeList = MutableLiveData<List<TypeDetail>>()
    open val typeList: LiveData<List<TypeDetail>> = _typeList

    private val _typeDetail = MutableLiveData<TypeDetail>()
    open val typeDetail: LiveData<TypeDetail> = _typeDetail

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

    fun fetchType(loadMore: Boolean = false) {
        viewModelScope.launch {
            if (_isLoading.value == true) return@launch

            if (loadMore) {
                if (!canLoadMore) return@launch
                currentOffset += pageSize
            } else {
                currentOffset = 0
                _typeList.value = emptyList()
                canLoadMore = true
            }
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response: TypeListResponse = repository.fetchTypeList(pageSize, currentOffset)
                _typeList.value = response.results
            } catch (e: Exception) {
                Log.e("TypeViewModel", "Error loading Type list", e)
                _errorMessage.value = "Failed to load Type: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getTypeDetailByName(typeId: String): TypeDetail? {
        return typeDetail.value?: typeList.value?.find { it.name == typeId }
    }
}