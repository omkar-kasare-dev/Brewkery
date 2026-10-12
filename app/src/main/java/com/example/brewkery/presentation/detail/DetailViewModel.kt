package com.example.brewkery.presentation.detail


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.brewkery.cart.CartLine
import com.example.brewkery.cart.CartRepository
import com.example.brewkery.cart.ItemSelection
import com.example.brewkery.cart.PriceCalculator
import com.example.brewkery.data.MenuRepository
import com.example.brewkery.data.MenuResult
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption
import com.example.brewkery.navigation.DetailRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAX_QUANTITY = 99

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val menuRepository: MenuRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val itemId: Int = savedStateHandle.toRoute<DetailRoute>().itemId

    private val menuResult = MutableStateFlow<MenuResult?>(null) // null while loading
    private val chosen = MutableStateFlow<ItemSelection?>(null)  // null until the user changes something
    private val quantity = MutableStateFlow(1)

    private var loadJob: Job? = null
    private var added = false

    val uiState: StateFlow<DetailUiState> = combine(menuResult, chosen, quantity) { result, selection, qty ->
        buildState(result, selection, qty)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DetailUiState.Loading)

    init {
        load()
    }

    /** The menu is normally already cached, so this returns instantly. Also used by Retry. */
    fun load() {
        if (loadJob?.isActive == true) return
        menuResult.value = null
        loadJob = viewModelScope.launch {
            menuResult.value = menuRepository.getMenu()
        }
    }

    fun onSizeSelected(size: SizeOption) = updateSelection { it.copy(size = size) }

    fun onMilkSelected(milk: MilkOption) = updateSelection { it.copy(milk = milk) }

    fun onSugarSelected(level: String) = updateSelection { it.copy(sugarLevel = level) }

    fun onIncrement() = quantity.update { (it + 1).coerceAtMost(MAX_QUANTITY) }

    fun onDecrement() = quantity.update { (it - 1).coerceAtLeast(1) }

    fun addToCart() {
        val content = uiState.value as? DetailUiState.Content ?: return
        if (added) return // guards against a double tap before navigation completes
        added = true
        cartRepository.add(
            CartLine(
                item = content.item,
                size = content.selection.size,
                milk = content.selection.milk,
                sugarLevel = content.selection.sugarLevel,
                quantity = content.quantity
            )
        )
    }

    private fun updateSelection(transform: (ItemSelection) -> ItemSelection) {
        val content = uiState.value as? DetailUiState.Content ?: return
        chosen.value = transform(content.selection)
    }

    private fun buildState(result: MenuResult?, selection: ItemSelection?, qty: Int): DetailUiState =
        when (result) {
            null -> DetailUiState.Loading
            is MenuResult.Failure -> DetailUiState.Error(result.error)
            is MenuResult.Success -> {
                val item = result.menu.items.firstOrNull { it.id == itemId }
                if (item == null) {
                    DetailUiState.NotFound
                } else {
                    val current = selection ?: ItemSelection.defaultFor(item)
                    val unitPrice = PriceCalculator.unitPriceCents(
                        item, current.size, current.milk, current.sugarLevel
                    )
                    DetailUiState.Content(
                        item = item,
                        currencySymbol = result.menu.meta.currencySymbol,
                        selection = current,
                        quantity = qty,
                        unitPriceCents = unitPrice,
                        totalCents = unitPrice * qty
                    )
                }
            }
        }
}