package com.example.brewkery.cart


import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption

/** The customer's current choice for each customization group. Null = the item has no such group. */
data class ItemSelection(
    val size: SizeOption?,
    val milk: MilkOption?,
    val sugarLevel: String?
) {
    companion object {
        /** First option without a surcharge; falls back to the first option. */
        fun defaultFor(item: MenuItem): ItemSelection {
            val options = item.customizations
            return ItemSelection(
                size = options.sizes.firstOrNull { PriceCalculator.toCents(it.extraPrice) == 0L }
                    ?: options.sizes.firstOrNull(),
                milk = options.milkOptions.firstOrNull { PriceCalculator.toCents(it.extraPrice) == 0L }
                    ?: options.milkOptions.firstOrNull(),
                sugarLevel = options.sugarLevels.firstOrNull { PriceCalculator.sugarSurchargeCents(it) == 0L }
                    ?: options.sugarLevels.firstOrNull()
            )
        }
    }
}