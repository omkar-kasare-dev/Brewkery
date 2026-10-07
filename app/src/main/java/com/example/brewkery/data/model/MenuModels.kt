package com.example.brewkery.data.model



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MenuResponse(
    val meta: StoreMeta,
    val categories: List<Category>,
    val items: List<MenuItem>
)

@Serializable
data class StoreMeta(
    val tagline: String,
    @SerialName("currency_symbol") val currencySymbol: String,
    @SerialName("delivery_fee") val deliveryFee: Double,
    @SerialName("tax_rate_percent") val taxRatePercent: Double,
    @SerialName("estimated_delivery_time") val estimatedDeliveryTime: String
)

@Serializable
data class Category(
    val id: String,
    val name: String,
    val icon: String
)

@Serializable
data class MenuItem(
    val id: Int,
    @SerialName("category_id") val categoryId: String,
    val name: String,
    val tagline: String,
    val description: String,
    @SerialName("base_price") val basePrice: Double,
    val rating: Double,
    @SerialName("review_count") val reviewCount: Int,
    @SerialName("prep_time") val prepTime: String,
    val calories: Int,
    @SerialName("image_url") val imageUrl: String,
    val badge: String? = null,
    val ingredients: List<String> = emptyList(),
    val customizations: Customizations = Customizations()
)

@Serializable
data class Customizations(
    val sizes: List<SizeOption> = emptyList(),
    @SerialName("sugar_levels") val sugarLevels: List<String> = emptyList(),
    @SerialName("milk_options") val milkOptions: List<MilkOption> = emptyList()
)

@Serializable
data class SizeOption(
    val id: String,
    val label: String,
    @SerialName("extra_price") val extraPrice: Double
)

@Serializable
data class MilkOption(
    val id: String,
    val name: String,
    @SerialName("extra_price") val extraPrice: Double
)