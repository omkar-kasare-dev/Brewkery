package com.example.brewkery.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.brewkery.R

val PlusJakartaSans = FontFamily(
    Font(R.font.plus_jakarta_sans_regular, FontWeight.Normal),
    Font(R.font.plus_jakarta_sans_medium, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_semibold, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_bold, FontWeight.Bold),
    Font(R.font.plus_jakarta_sans_extrabold, FontWeight.ExtraBold)
)

val JetBrainsMono = FontFamily(
    Font(R.font.jetbrains_mono_regular, FontWeight.Normal),
    Font(R.font.jetbrains_mono_bold, FontWeight.Bold)
)

private fun sans(size: Float, weight: FontWeight, tracking: Float = 0f, lineHeight: Float = size * 1.35f) =
    TextStyle(
        fontFamily = PlusJakartaSans,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = lineHeight.sp,
        letterSpacing = tracking.sp
    )

private fun mono(size: Float, weight: FontWeight) =
    TextStyle(
        fontFamily = JetBrainsMono,
        fontWeight = weight,
        fontSize = size.sp,
        lineHeight = (size * 1.3f).sp
    )

/** The prototype's `.font-display` renders as the system font in the reference screenshots. *//** The prototype's `.font-display` renders as the system font in the reference screenshots. */
private fun display(size: Float, bold: FontWeight) =
    TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = size.sp,
        lineHeight = (size * 1.25f).sp
    )

object BrewkeryText {
    // Shared
    val Body = sans(12f, FontWeight.Normal)
    val BodyBold = sans(12f, FontWeight.Bold)
    val ScreenTitle = sans(12f, FontWeight.ExtraBold, tracking = 0.6f)
    val Cta = sans(13f, FontWeight.Bold)

    // Menu
    val HeaderOverline = sans(10f, FontWeight.Medium)
    val HeaderTitle = sans(12f, FontWeight.Bold)
    val Avatar = sans(11f, FontWeight.Bold)
    val BadgeCount = sans(8f, FontWeight.Bold)
    val Overline = sans(9f, FontWeight.Bold, tracking = 0.45f)
    val BannerTitle = sans(12f, FontWeight.Bold)
    val BannerSubtitle = sans(10f, FontWeight.Medium)
    val Pill = sans(10f, FontWeight.Bold)
    val Search = sans(11f, FontWeight.Medium)
    val Chip = sans(12f, FontWeight.Medium)
    val ChipSelected = sans(12f, FontWeight.Bold)
    val Badge = sans(8f, FontWeight.Bold, tracking = 0.4f)
    val CardTitle = sans(12f, FontWeight.Bold)
    val Rating = sans(10f, FontWeight.Medium)
    val PriceCard = mono(12f, FontWeight.Bold)
    val Customize = sans(10f, FontWeight.Bold)
    val BarCount = sans(10f, FontWeight.Bold)
    val BarLabel = sans(10f, FontWeight.Normal)
    val BarTotal = sans(12f, FontWeight.Bold)
    val BarCta = sans(12f, FontWeight.Bold)

    // Detail
    val DetailTitle = display(15f, FontWeight.Bold)
    val PriceDetail = mono(16f, FontWeight.Bold)
    val Description = sans(11f, FontWeight.Normal, lineHeight = 18f)
    val SectionLabel = sans(10f, FontWeight.Bold, tracking = 0.5f)
    val IngredientChip = sans(9f, FontWeight.Normal)
    val GroupTitle = sans(11f, FontWeight.Bold)
    val OptionExtra = mono(9f, FontWeight.Normal)
    val RowExtra = mono(10f, FontWeight.Normal)
    val SugarChip = sans(11f, FontWeight.Medium)
    val SugarChipSelected = sans(11f, FontWeight.Bold)
    val HeroBadge = sans(9f, FontWeight.Bold, tracking = 0.4f)

    // Cart
    val CartSubtitle = sans(10f, FontWeight.Normal)
    val CartPrice = mono(11f, FontWeight.Bold)
    val ClearCart = sans(10f, FontWeight.Bold)
    val BillValue = mono(12f, FontWeight.Normal)
    val TotalLabel = sans(14f, FontWeight.Bold)
    val TotalValue = mono(16f, FontWeight.Bold)

    // Order
    val OrderOverline = sans(10f, FontWeight.Bold, tracking = 0.5f)
    val OrderHeading = display(18f, FontWeight.Bold)
    val TicketLabel = sans(9f, FontWeight.SemiBold, tracking = 0.3f)
    val TicketId = mono(14f, FontWeight.Bold)
    val StatusChip = sans(9f, FontWeight.Bold, tracking = 0.45f)
    val StatusLabel = sans(10f, FontWeight.Normal)
}