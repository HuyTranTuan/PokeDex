package com.jetpack.pokedex.viewmodel.move

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.jetpack.pokedex.data.model.MoveDetail
import com.jetpack.pokedex.data.model.MoveListResponse
import com.jetpack.pokedex.data.repository.IMoveRepository
import com.jetpack.pokedex.data.source.MoveApiCallBack

open class MoveViewModel(private val repository: IMoveRepository) : ViewModel() {
    // LiveData to hold the list of Move
    private val _moveList = MutableLiveData<List<MoveDetail>>()
    open val moveList: LiveData<List<MoveDetail>> = _moveList

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
        fetchMove() // Initial fetch
    }

    fun fetchMove(loadMore: Boolean = false) {
        if (_isLoading.value == true) return // Prevent multiple simultaneous loads

        if (loadMore) {
            if (!canLoadMore) return // No more items to load
            currentOffset += pageSize
        } else {
            // Reset for a fresh load or initial load
            currentOffset = 0
            _moveList.value = emptyList() // Clear previous list for a fresh load
            canLoadMore = true
        }

        _isLoading.value = true

        repository.getMoveList(
            limit = pageSize,
            offset = currentOffset,
            callback = object : MoveApiCallBack<MoveListResponse> {
                override fun onSuccess(data: MoveListResponse) {
                    val currentList = if (loadMore) _moveList.value ?: emptyList() else emptyList()
                    _moveList.postValue((currentList + data.results))

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

    fun getMoveDetailById(moveId: String): MoveDetail? {
        return moveList.value?.find { it.id == moveId }
    }

    fun getMoveDetailByName(moveName: String): MoveDetail? {
        return moveList.value?.find { it.name == moveName }
    }
}