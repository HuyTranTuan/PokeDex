package com.jetpack.pokedex.viewmodel.generation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jetpack.pokedex.data.model.GenerationDetail
import com.jetpack.pokedex.data.model.GenerationListResponse
import com.jetpack.pokedex.data.repository.IGenerationRepository
import com.jetpack.pokedex.data.source.GenerationApiCallBack

open class GenerationViewModel (private val repository: IGenerationRepository) : ViewModel() {
    // LiveData to hold the list of Pokémon
    private val _generationList = MutableLiveData<List<GenerationDetail>>()
    open val generationList: LiveData<List<GenerationDetail>> = _generationList

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
        fetchGeneration() // Initial fetch
    }

    fun fetchGeneration(loadMore: Boolean = false) {
        if (_isLoading.value == true) return // Prevent multiple simultaneous loads

        if (loadMore) {
            if (!canLoadMore) return // No more items to load
            currentOffset += pageSize
        } else {
            // Reset for a fresh load or initial load
            currentOffset = 0
            _generationList.value = emptyList() // Clear previous list for a fresh load
            canLoadMore = true
        }

        _isLoading.value = true

        repository.getGenerationList(
            limit = pageSize,
            offset = currentOffset,
            callback = object : GenerationApiCallBack<GenerationListResponse> {
                override fun onSuccess(data: GenerationListResponse) {
                    val currentList =
                        if (loadMore) _generationList.value ?: emptyList() else emptyList()
                    _generationList.postValue((currentList + data.results))

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

    fun getGenerationDetailById(generationId: String): GenerationDetail? {
        return generationList.value?.find { it.id == generationId }
    }

    fun getGenerationDetailByName(generationId: String): GenerationDetail? {
        return generationList.value?.find { it.name == generationId }
    }
}