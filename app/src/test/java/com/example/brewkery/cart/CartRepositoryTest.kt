package com.example.brewkery.cart


import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CartRepositoryTest {

    private val repo = CartRepository()

    @Test
    fun addingSameCustomizationMergesQuantity() {
        repo.add(testLine(size = grande, qty = 1))
        repo.add(testLine(size = grande, qty = 2))

        assertEquals(1, repo.lines.value.size)
        assertEquals(3, repo.lines.value.first().quantity)
    }

    @Test
    fun differentCustomizationsStaySeparate() {
        repo.add(testLine(size = grande))
        repo.add(testLine(size = tall))

        assertEquals(2, repo.lines.value.size)
    }

    @Test
    fun sameOptionIdOnDifferentItemsStaysSeparate() {
        repo.add(testLine(testItem(id = 1), size = grande))
        repo.add(testLine(testItem(id = 2), size = grande))

        assertEquals(2, repo.lines.value.size)
    }

    @Test
    fun setQuantityUpdatesAndZeroRemoves() {
        val line = testLine(size = grande)
        repo.add(line)

        repo.setQuantity(line.id, 4)
        assertEquals(4, repo.lines.value.first().quantity)

        repo.setQuantity(line.id, 0)
        assertTrue(repo.lines.value.isEmpty())
    }

    @Test
    fun placeOrderClearsCartAndCreatesTicket() {
        repo.add(testLine(testItem(id = 1), qty = 2))
        repo.add(testLine(testItem(id = 2), qty = 1))

        val order = repo.placeOrder("20 - 30 mins")!!

        assertTrue(Regex("BK-\\d{5}").matches(order.ticketId))
        assertEquals(3, order.itemCount)
        assertEquals("20 - 30 mins", order.estimatedWait)
        assertTrue(repo.lines.value.isEmpty())
        assertEquals(order, repo.activeOrder.value)
    }

    @Test
    fun placeOrderOnEmptyCartReturnsNull() {
        assertNull(repo.placeOrder("20 - 30 mins"))
        assertNull(repo.activeOrder.value)
    }

    @Test
    fun clearEmptiesTheCartButKeepsThePlacedOrder() {
        repo.add(testLine(qty = 2))
        repo.placeOrder("20 - 30 mins")
        repo.add(testLine(qty = 1))

        repo.clear()

        assertTrue(repo.lines.value.isEmpty())
        assertTrue(repo.activeOrder.value != null)
    }

    @Test
    fun mergedQuantityIsCapped() {
        repo.add(testLine(qty = 60))
        repo.add(testLine(qty = 60))

        assertEquals(MAX_LINE_QUANTITY, repo.lines.value.first().quantity)
    }
}