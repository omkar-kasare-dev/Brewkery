package com.example.brewkery.presentation.detail


import com.example.brewkery.cart.ItemSelection
import com.example.brewkery.data.MenuError
import com.example.brewkery.data.model.MenuItem

sealed interface DetailUiState {

    data object Loading : DetailUiState

    data class Error(val error: MenuError) : DetailUiState

    data object NotFound : DetailUiState

    data class Content(
        val item: MenuItem,
        val currencySymbol: String,
        val selection: ItemSelection,
        val quantity: Int,
        val unitPriceCents: Long,
        val totalCents: Long
    ) : DetailUiState
}