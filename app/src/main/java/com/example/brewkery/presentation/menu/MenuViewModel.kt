package com.example.brewkery.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewkery.cart.CartRepository
import com.example.brewkery.data.MenuRepository
import com.example.brewkery.data.MenuResult
import com.example.brewkery.data.model.MenuItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    cartRepository: CartRepository
) : ViewModel() {

    /** null while loading. */
    private val menuResult = MutableStateFlow<MenuResult?>(null)
    private val selectedCategoryId = MutableStateFlow<String?>(null)

    /** Exposed separately so the text field is updated synchronously (no cursor jumps). */
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    val uiState: StateFlow<MenuUiState> = combine(
        menuResult,
        selectedCategoryId,
        _query,
        cartRepository.lines,
        cartRepository.activeOrder
    ) { result, categoryId, query, cartLines, activeOrder ->
        when (result) {
            null -> MenuUiState.Loading
            is MenuResult.Failure -> MenuUiState.Error(result.error)
            is MenuResult.Success -> MenuUiState.Success(
                meta = result.menu.meta,
                categories = result.menu.categories,
                selectedCategoryId = categoryId,
                items = result.menu.items.filterBy(categoryId, query),
                cartCount = cartLines.sumOf { it.quantity },
                cartSubtotalCents = cartLines.sumOf { it.lineTotalCents },
                activeOrder = activeOrder
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MenuUiState.Loading)

    private var loadJob: Job? = null

    init {
        loadMenu()
    }

    /** Initial load and Retry. Ignored while a request is already in flight. */
    fun loadMenu() {
        if (loadJob?.isActive == true) return
        menuResult.value = null
        loadJob = viewModelScope.launch {
            menuResult.value = menuRepository.getMenu()
        }
    }

    fun selectCategory(categoryId: String?) {
        selectedCategoryId.value = categoryId
    }

    fun onQueryChange(query: String) {
        _query.value = query
    }
}

/** Category + free-text filter. Text matches name, tagline or ingredients, ignoring case. */
internal fun List<MenuItem>.filterBy(categoryId: String?, query: String): List<MenuItem> {
    val text = query.trim()
    return filter { item ->
        (categoryId == null || item.categoryId == categoryId) &&
                (
                        text.isEmpty() ||
                                item.name.contains(text, ignoreCase = true) ||
                                item.tagline.contains(text, ignoreCase = true) ||
                                item.ingredients.any { it.contains(text, ignoreCase = true) }
                        )
    }
}