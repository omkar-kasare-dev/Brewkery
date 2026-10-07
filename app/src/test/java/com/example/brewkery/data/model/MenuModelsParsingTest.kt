package com.example.brewkery.data.model


import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuModelsParsingTest {

    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun parsesMenuResponseInDataJsonShape() {
        val menu = json.decodeFromString(MenuResponse.serializer(), SAMPLE_MENU)

        assertEquals(2.5, menu.meta.deliveryFee, 0.0001)
        assertEquals(8.0, menu.meta.taxRatePercent, 0.0001)
        assertEquals("20 - 30 mins", menu.meta.estimatedDeliveryTime)

        assertEquals(1, menu.categories.size)
        assertEquals("cat_hot_coffee", menu.categories.first().id)
        assertEquals("\u2615", menu.categories.first().icon)

        val item = menu.items.first()
        assertEquals(1, item.id)
        assertEquals("cat_hot_coffee", item.categoryId)
        assertEquals(4.85, item.basePrice, 0.0001)
        assertEquals("BESTSELLER", item.badge)
        assertEquals(4, item.ingredients.size)
        assertEquals(3, item.customizations.sizes.size)
        assertEquals(0.65, item.customizations.sizes[1].extraPrice, 0.0001)
        assertEquals("Roasted Almond Milk", item.customizations.milkOptions[1].name)
        assertEquals(3, item.customizations.sugarLevels.size)
    }

    @Test
    fun missingOptionalFieldsFallBackToDefaults() {
        val item = json.decodeFromString(MenuItem.serializer(), MINIMAL_ITEM)

        assertNull(item.badge)
        assertTrue(item.ingredients.isEmpty())
        assertTrue(item.customizations.sizes.isEmpty())
        assertTrue(item.customizations.milkOptions.isEmpty())
        assertTrue(item.customizations.sugarLevels.isEmpty())
    }

    private companion object {
        val SAMPLE_MENU = """
            {
              "meta": {
                "app": "Brewkery",
                "version": "1.0.0",
                "tagline": "Artisanal Coffee & Fresh Oven Bakes",
                "currency": "USD",
                "currency_symbol": "$",
                "delivery_fee": 2.50,
                "tax_rate_percent": 8.0,
                "estimated_delivery_time": "20 - 30 mins"
              },
              "categories": [
                { "id": "cat_hot_coffee", "name": "Hot Coffee", "icon": "\u2615", "item_count": 2 }
              ],
              "items": [
                {
                  "id": 1,
                  "category_id": "cat_hot_coffee",
                  "name": "Toasted Caramel Macchiato",
                  "tagline": "Double shot espresso with velvety oat milk & salted caramel",
                  "description": "Rich espresso roast layered with velvety steamed milk.",
                  "base_price": 4.85,
                  "rating": 4.9,
                  "review_count": 312,
                  "prep_time": "5-7 mins",
                  "calories": 240,
                  "image_url": "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=800&auto=format&fit=crop&q=80",
                  "badge": "BESTSELLER",
                  "ingredients": ["Signature Espresso", "Steamed Oat Milk", "Salted Caramel", "Bourbon Vanilla"],
                  "customizations": {
                    "sizes": [
                      { "id": "sz_small", "label": "Tall (8 oz)", "extra_price": 0.00 },
                      { "id": "sz_medium", "label": "Grande (12 oz)", "extra_price": 0.65 },
                      { "id": "sz_large", "label": "Venti (16 oz)", "extra_price": 1.25 }
                    ],
                    "sugar_levels": ["0% Unsweetened", "50% Mild", "100% Standard Sweet"],
                    "milk_options": [
                      { "id": "m_oat", "name": "Oat Milk (Barista Blend)", "extra_price": 0.00 },
                      { "id": "m_almond", "name": "Roasted Almond Milk", "extra_price": 0.50 },
                      { "id": "m_whole", "name": "Organic Whole Milk", "extra_price": 0.00 }
                    ]
                  }
                }
              ]
            }
        """.trimIndent()

        val MINIMAL_ITEM = """
            {
              "id": 9,
              "category_id": "cat_bakery",
              "name": "Test Item",
              "tagline": "t",
              "description": "d",
              "base_price": 1.0,
              "rating": 4.0,
              "review_count": 1,
              "prep_time": "1 min",
              "calories": 1,
              "image_url": "https://example.com/x.jpg"
            }
        """.trimIndent()
    }
}