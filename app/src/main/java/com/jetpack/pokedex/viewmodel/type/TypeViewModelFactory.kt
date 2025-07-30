package com.jetpack.pokedex.viewmodel.type

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.jetpack.pokedex.data.repository.ITypeRepository

class TypeViewModelFactory(
    private val typeRepository: ITypeRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TypeViewModel::class.java)) {
            return TypeViewModel(typeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}