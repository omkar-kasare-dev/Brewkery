package com.example.brewkery.cart


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/** Upper bound for a single cart line, enforced here so every caller gets the same rule. */
const val MAX_LINE_QUANTITY = 99

/** In-memory cart + last placed order. The brief says it's fine for the cart to reset on app close. */
@Singleton
class CartRepository @Inject constructor() {

    private val _lines = MutableStateFlow<List<CartLine>>(emptyList())
    val lines: StateFlow<List<CartLine>> = _lines.asStateFlow()

    private val _activeOrder = MutableStateFlow<Order?>(null)
    val activeOrder: StateFlow<Order?> = _activeOrder.asStateFlow()

    /** Same item + same choices as an existing line merges quantities (capped). */
    fun add(line: CartLine) {
        _lines.update { current ->
            if (current.any { it.id == line.id }) {
                current.map {
                    if (it.id == line.id) {
                        it.copy(quantity = (it.quantity + line.quantity).coerceAtMost(MAX_LINE_QUANTITY))
                    } else {
                        it
                    }
                }
            } else {
                current + line.copy(quantity = line.quantity.coerceIn(1, MAX_LINE_QUANTITY))
            }
        }
    }

    /** Zero or less removes the line. */
    fun setQuantity(lineId: String, quantity: Int) {
        _lines.update { current ->
            if (quantity <= 0) {
                current.filterNot { it.id == lineId }
            } else {
                current.map {
                    if (it.id == lineId) it.copy(quantity = quantity.coerceAtMost(MAX_LINE_QUANTITY)) else it
                }
            }
        }
    }

    /** "Clear Cart". Leaves any already-placed order untouched. */
    fun clear() {
        _lines.value = emptyList()
    }

    /** Clears the cart and creates the ticket. Returns null if the cart was empty. */
    fun placeOrder(estimatedWait: String): Order? {
        val placed = _lines.getAndUpdate { emptyList() }
        if (placed.isEmpty()) return null
        val order = Order(
            ticketId = "BK-${Random.nextInt(10_000, 100_000)}",
            itemCount = placed.sumOf { it.quantity },
            estimatedWait = estimatedWait
        )
        _activeOrder.value = order
        return order
    }
}