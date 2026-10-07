package com.example.brewkery.presentation.menu


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewkery.data.MenuRepository
import com.example.brewkery.data.MenuResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val repository: MenuRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<MenuUiState>(MenuUiState.Loading)
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null

    init {
        loadMenu()
    }

    /** Initial load and Retry. Ignored while a request is already in flight. */
    fun loadMenu() {
        if (loadJob?.isActive == true) return
        _uiState.value = MenuUiState.Loading
        loadJob = viewModelScope.launch {
            _uiState.value = when (val result = repository.getMenu()) {
                is MenuResult.Success -> MenuUiState.Success(result.menu)
                is MenuResult.Failure -> MenuUiState.Error(result.error)
            }
        }
    }

    /** [categoryId] = null selects "All Items". */
    fun selectCategory(categoryId: String?) {
        _uiState.update { state ->
            if (state is MenuUiState.Success) state.copy(selectedCategoryId = categoryId) else state
        }
    }
}