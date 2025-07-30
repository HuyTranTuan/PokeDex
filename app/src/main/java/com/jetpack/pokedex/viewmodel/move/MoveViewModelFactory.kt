package com.jetpack.pokedex.viewmodel.move

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jetpack.pokedex.data.repository.IMoveRepository

class MoveViewModelFactory (
    private val moveRepository: IMoveRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MoveViewModel::class.java)) {
            return MoveViewModel(moveRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}