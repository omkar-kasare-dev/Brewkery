package com.example.brewkery.cart


import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption
import com.example.brewkery.data.model.StoreMeta
import kotlin.math.roundToLong

data class Bill(
    val subtotalCents: Long,
    val deliveryFeeCents: Long,
    val taxCents: Long
) {
    val totalCents: Long get() = subtotalCents + deliveryFeeCents + taxCents
}

object PriceCalculator {

    /** Matches a trailing "(+0.40)" in a sugar/serving label. */
    private val SUGAR_SURCHARGE = Regex("""\(\+(\d+(?:\.\d+)?)\)\s*$""")

    fun toCents(price: Double): Long = (price * 100).roundToLong()

    fun unitPriceCents(
        item: MenuItem,
        size: SizeOption?,
        milk: MilkOption?,
        sugarLevel: String?
    ): Long =
        toCents(item.basePrice) +
                (size?.let { toCents(it.extraPrice) } ?: 0L) +
                (milk?.let { toCents(it.extraPrice) } ?: 0L) +
                sugarSurchargeCents(sugarLevel)

    fun sugarSurchargeCents(sugarLevel: String?): Long {
        val match = sugarLevel?.let { SUGAR_SURCHARGE.find(it) } ?: return 0L
        return toCents(match.groupValues[1].toDouble())
    }

    /** Tax applies to the subtotal only; delivery is a flat fee. */
    fun bill(lines: List<CartLine>, meta: StoreMeta): Bill {
        val subtotal = lines.sumOf { it.lineTotalCents }
        val tax = (subtotal * meta.taxRatePercent / 100.0).roundToLong()
        return Bill(
            subtotalCents = subtotal,
            deliveryFeeCents = toCents(meta.deliveryFee),
            taxCents = tax
        )
    }
}