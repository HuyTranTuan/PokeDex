package com.jetpack.pokedex.viewmodel.move

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jetpack.pokedex.data.model.MoveDetail
import com.jetpack.pokedex.data.model.MoveListResponse
import com.jetpack.pokedex.data.repository.MoveRepository
import kotlinx.coroutines.launch

open class MoveViewModel(private val repository: MoveRepository) : ViewModel() {
    // LiveData to hold the list of Move
    private val _moveList = MutableLiveData<List<MoveDetail>>()
    open val moveList: LiveData<List<MoveDetail>> = _moveList

    private val _moveDetail = MutableLiveData<MoveDetail>()
    open val moveDetail: LiveData<MoveDetail> = _moveDetail

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

    fun fetchMove(loadMore: Boolean = false) {
        viewModelScope.launch {
            if (_isLoading.value == true) return@launch

            if (loadMore) {
                if (!canLoadMore) return@launch
                currentOffset += pageSize
            } else {
                currentOffset = 0
                _moveList.value = emptyList()
                canLoadMore = true
            }
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response: MoveListResponse = repository.fetchMoveList(pageSize, currentOffset)
                _moveList.value = response.results
            } catch (e: Exception) {
                Log.e("MoveViewModel", "Error loading Move list", e)
                _errorMessage.value = "Failed to load Move: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getMoveDetailByName(moveName: String): MoveDetail? {
        return moveDetail.value ?: moveList.value?.find { it.name == moveName }
    }
}