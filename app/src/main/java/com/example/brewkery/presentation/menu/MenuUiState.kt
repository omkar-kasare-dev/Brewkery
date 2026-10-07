package com.example.brewkery.presentation.menu


import com.example.brewkery.data.MenuError
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MenuResponse

sealed interface MenuUiState {

    data object Loading : MenuUiState

    data class Success(
        val menu: MenuResponse,
        val selectedCategoryId: String? = null   // null = "All Items"
    ) : MenuUiState {
        val visibleItems: List<MenuItem>
            get() = if (selectedCategoryId == null) {
                menu.items
            } else {
                menu.items.filter { it.categoryId == selectedCategoryId }
            }
    }

    data class Error(val error: MenuError) : MenuUiState
}