package com.example.saleshub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.saleshub.repository.SalesRepository

class SalesViewModelFactory(
    private val repository: SalesRepository,
    private val clientViewModel: ClientViewModel,
    private val productViewModel: ProductViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SalesViewModel(repository, clientViewModel, productViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
