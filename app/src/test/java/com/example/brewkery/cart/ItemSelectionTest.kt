package com.example.brewkery.cart



import com.example.brewkery.data.model.Customizations
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ItemSelectionTest {

    private val oat = MilkOption("m_oat", "Oat Milk (Barista Blend)", 0.0)
    private val almond = MilkOption("m_almond", "Roasted Almond Milk", 0.5)

    @Test
    fun defaultsToFirstOptionWhenItHasNoSurcharge() {
        val item = testItem().copy(
            customizations = Customizations(
                sizes = listOf(tall, grande),
                sugarLevels = listOf("0% Unsweetened", "50% Mild"),
                milkOptions = listOf(oat, almond)
            )
        )

        val selection = ItemSelection.defaultFor(item)

        assertEquals(tall, selection.size)
        assertEquals(oat, selection.milk)
        assertEquals("0% Unsweetened", selection.sugarLevel)
        // Matches the prototype's $4.85 Add to Cart price for the macchiato.
        assertEquals(
            485L,
            PriceCalculator.unitPriceCents(item, selection.size, selection.milk, selection.sugarLevel)
        )
    }

    @Test
    fun skipsSurchargedOptionsToFindTheFreeOne() {
        val honey = MilkOption("t_honey", "Whipped Honey Butter", 0.5)
        val jam = MilkOption("t_jam", "Artisan Raspberry Jam", 0.5)
        val plain = MilkOption("t_plain", "Classic (No Spread)", 0.0)
        val item = testItem().copy(
            customizations = Customizations(
                sugarLevels = listOf("Light Wildflower Honey (+0.40)", "Zero Added Sugar"),
                milkOptions = listOf(honey, jam, plain)
            )
        )

        val selection = ItemSelection.defaultFor(item)

        assertEquals(plain, selection.milk)
        assertEquals("Zero Added Sugar", selection.sugarLevel)
    }

    @Test
    fun fallsBackToFirstOptionWhenEverythingHasASurcharge() {
        val item = testItem().copy(
            customizations = Customizations(
                sizes = listOf(SizeOption("a", "A", 1.0), SizeOption("b", "B", 2.0))
            )
        )

        assertEquals("a", ItemSelection.defaultFor(item).size?.id)
    }

    @Test
    fun itemWithoutCustomizationsHasNoSelection() {
        val selection = ItemSelection.defaultFor(testItem())

        assertNull(selection.size)
        assertNull(selection.milk)
        assertNull(selection.sugarLevel)
    }
}