package com.example.brewkery.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewkery.cart.CartLine
import com.example.brewkery.cart.CartRepository
import com.example.brewkery.cart.PriceCalculator
import com.example.brewkery.data.MenuRepository
import com.example.brewkery.data.MenuResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val menuRepository: MenuRepository,
    private val cartRepository: CartRepository
) : ViewModel() {

    private val menuResult = MutableStateFlow<MenuResult?>(null) // null while loading
    private val orderPlaced = MutableStateFlow(false)
    private var loadJob: Job? = null

    /**
     * Store settings (fee, tax rate) come from the cached menu; the lines come from the cart.
     * Once the order is placed we stop updating: placing it empties the cart, and without this
     * the screen would flash its empty state while fading out into the Order screen.
     */
    val uiState: StateFlow<CartUiState> = combine(
        menuResult,
        cartRepository.lines,
        orderPlaced
    ) { result, lines, placed ->
        Triple(result, lines, placed)
    }.transformWhile { (result, lines, placed) ->
        if (placed) {
            false
        } else {
            emit(toUiState(result, lines))
            true
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, CartUiState.Loading)

    init {
        load()
    }

    /** Normally instant (menu is cached). Also used by Retry. */
    fun load() {
        if (loadJob?.isActive == true) return
        menuResult.value = null
        loadJob = viewModelScope.launch {
            menuResult.value = menuRepository.getMenu()
        }
    }

    /** The repository caps the quantity. */
    fun onIncrement(line: CartLine) {
        cartRepository.setQuantity(line.id, line.quantity + 1)
    }

    /** Going below 1 removes the line (handled by the repository). */
    fun onDecrement(line: CartLine) {
        cartRepository.setQuantity(line.id, line.quantity - 1)
    }

    fun clearCart() {
        cartRepository.clear()
    }

    /** Returns true if an order was created, so the screen knows to navigate. */
    fun placeOrder(): Boolean {
        val content = uiState.value as? CartUiState.Content ?: return false
        if (content.lines.isEmpty()) return false
        orderPlaced.value = true // freeze the state first, then empty the cart
        return cartRepository.placeOrder(content.meta.estimatedDeliveryTime) != null
    }

    private fun toUiState(result: MenuResult?, lines: List<CartLine>): CartUiState =
        when (result) {
            null -> CartUiState.Loading
            is MenuResult.Failure -> CartUiState.Error(result.error)
            is MenuResult.Success -> CartUiState.Content(
                lines = lines,
                bill = PriceCalculator.bill(lines, result.menu.meta),
                meta = result.menu.meta
            )
        }
}