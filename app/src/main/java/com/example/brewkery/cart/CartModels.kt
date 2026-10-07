package com.example.brewkery.cart



import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption

data class CartLine(
    val item: MenuItem,
    val size: SizeOption?,
    val milk: MilkOption?,
    val sugarLevel: String?,
    val quantity: Int
) {
    /** Same item + same choices = same line. Option ids repeat across items, so item.id is part of the key. */
    val id: String get() = "${item.id}|${size?.id}|${milk?.id}|$sugarLevel"

    val unitPriceCents: Long
        get() = PriceCalculator.unitPriceCents(item, size, milk, sugarLevel)

    val lineTotalCents: Long
        get() = unitPriceCents * quantity
}

data class Order(
    val ticketId: String,
    val itemCount: Int,
    val estimatedWait: String
)