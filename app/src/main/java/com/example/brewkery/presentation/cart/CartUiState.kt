package com.example.brewkery.presentation.cart

import com.example.brewkery.cart.Bill
import com.example.brewkery.cart.CartLine
import com.example.brewkery.data.MenuError
import com.example.brewkery.data.model.StoreMeta

sealed interface CartUiState {

    data object Loading : CartUiState

    data class Error(val error: MenuError) : CartUiState

    /** An empty cart is still Content; the screen shows the empty state. */
    data class Content(
        val lines: List<CartLine>,
        val bill: Bill,
        val meta: StoreMeta
    ) : CartUiState
}