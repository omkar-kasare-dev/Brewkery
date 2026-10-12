package com.example.brewkery.presentation.menu

import com.example.brewkery.cart.Order
import com.example.brewkery.data.MenuError
import com.example.brewkery.data.model.Category
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.StoreMeta

sealed interface MenuUiState {

    data object Loading : MenuUiState

    data class Error(val error: MenuError) : MenuUiState

    data class Success(
        val meta: StoreMeta,
        val categories: List<Category>,
        val selectedCategoryId: String?,   // null = "All Items"
        val items: List<MenuItem>,         // already filtered by category + search
        val cartCount: Int,
        val cartSubtotalCents: Long,
        val activeOrder: Order?
    ) : MenuUiState
}