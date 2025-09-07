package com.jetpack.pokedex.viewmodel.generation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.pokedex.data.model.GenerationDetail
import com.jetpack.pokedex.data.model.GenerationListResponse
import com.jetpack.pokedex.data.repository.GenerationRepository
import kotlinx.coroutines.launch

open class GenerationViewModel (private val repository: GenerationRepository) : ViewModel() {
    // LiveData to hold the list of Pokémon
    private val _generationList = MutableLiveData<List<GenerationDetail>>()
    open val generationList: LiveData<List<GenerationDetail>> = _generationList

    private val _generationDetail = MutableLiveData<GenerationDetail>()
    open val generationDetail: LiveData<GenerationDetail> = _generationDetail

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

    fun fetchGeneration(loadMore: Boolean = false) {
        viewModelScope.launch {
            if (_isLoading.value == true) return@launch

            if (loadMore) {
                if (!canLoadMore) return@launch
                currentOffset += pageSize
            } else {
                currentOffset = 0
                _generationList.value = emptyList()
                canLoadMore = true
            }
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response: GenerationListResponse = repository.fetchGenerationList(pageSize, currentOffset)
                _generationList.value = response.results
            } catch (e: Exception) {
                Log.e("GenerationViewModel", "Error loading Generation list", e)
                _errorMessage.value = "Failed to load Generation: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun getGenerationDetailByName(generationId: String): GenerationDetail? {
        return generationDetail.value ?: generationList.value?.find { it.name == generationId }
    }
}