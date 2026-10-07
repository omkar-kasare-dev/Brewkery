package com.example.brewkery.cart

import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption
import com.example.brewkery.data.model.StoreMeta

internal val tall = SizeOption(id = "sz_small", label = "Tall (8 oz)", extraPrice = 0.0)
internal val grande = SizeOption(id = "sz_medium", label = "Grande (12 oz)", extraPrice = 0.65)

internal val testMeta = StoreMeta(
    tagline = "",
    currencySymbol = "$",
    deliveryFee = 2.5,
    taxRatePercent = 8.0,
    estimatedDeliveryTime = "20 - 30 mins"
)

internal fun testItem(id: Int = 1, basePrice: Double = 4.85) = MenuItem(
    id = id,
    categoryId = "cat_hot_coffee",
    name = "Item $id",
    tagline = "",
    description = "",
    basePrice = basePrice,
    rating = 4.5,
    reviewCount = 1,
    prepTime = "1 min",
    calories = 1,
    imageUrl = ""
)

internal fun testLine(
    item: MenuItem = testItem(),
    size: SizeOption? = null,
    milk: MilkOption? = null,
    sugar: String? = null,
    qty: Int = 1
) = CartLine(item = item, size = size, milk = milk, sugarLevel = sugar, quantity = qty)