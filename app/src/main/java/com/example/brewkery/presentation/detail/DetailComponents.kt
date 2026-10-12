package com.example.brewkery.presentation.detail


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.brewkery.R
import com.example.brewkery.cart.PriceCalculator
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption
import com.example.brewkery.presentation.common.CircleIconButton
import com.example.brewkery.presentation.common.formatPrice
import com.example.brewkery.ui.theme.BrewkeryColors
import com.example.brewkery.ui.theme.BrewkeryShapes
import com.example.brewkery.ui.theme.BrewkeryText


private val OptionShape = RoundedCornerShape(10.dp)
private val RowShape = RoundedCornerShape(8.dp)

private fun optionBorder(selected: Boolean) =
    if (selected) BorderStroke(1.5.dp, BrewkeryColors.Terracotta)
    else BorderStroke(1.dp, BrewkeryColors.Border)

private fun formatExtra(price: Double, symbol: String) =
    "+" + formatPrice(PriceCalculator.toCents(price), symbol)

@Composable
fun DetailTopBar(onBack: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircleIconButton(
            onClick = onBack,
            contentDescription = stringResource(R.string.navigate_back)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = BrewkeryColors.Ink,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(
            text = stringResource(R.string.detail_title),
            style = BrewkeryText.ScreenTitle,
            color = BrewkeryColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        // Decorative heart (the prototype shows it but the brief has no favorites feature).
        // Same 48dp footprint as the back button so the title stays centered.
        Box(Modifier.size(48.dp), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(33.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, BrewkeryColors.Border, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = BrewkeryColors.Heart,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

@Composable
fun HeroImage(
    imageUrl: String,
    contentDescription: String,
    badge: String?,
    modifier: Modifier = Modifier
) {
    val placeholder = remember { ColorPainter(BrewkeryColors.Tint) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(163.dp)
            .clip(BrewkeryShapes.Panel)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = contentDescription,
            contentScale = ContentScale.Crop,
            placeholder = placeholder,
            error = placeholder,
            modifier = Modifier.fillMaxSize()
        )
        badge?.let {
            Text(
                text = it,
                style = BrewkeryText.HeroBadge,
                color = BrewkeryColors.Amber,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 8.dp, start = 9.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(BrewkeryColors.Espresso.copy(alpha = 0.8f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IngredientChips(ingredients: List<String>, modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        ingredients.forEach { ingredient ->
            Text(
                text = ingredient,
                style = BrewkeryText.IngredientChip,
                color = BrewkeryColors.InkMuted,
                modifier = Modifier
                    .clip(RowShape)
                    .background(Color.White)
                    .border(1.dp, BrewkeryColors.Border, RowShape)
                    .padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
fun OptionGroupCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(BrewkeryShapes.Card)
            .background(Color.White)
            .border(1.dp, BrewkeryColors.Border, BrewkeryShapes.Card)
            .padding(11.dp)
    ) {
        Text(title, style = BrewkeryText.GroupTitle, color = BrewkeryColors.Ink)
        Spacer(Modifier.height(8.dp))
        content()
    }
}

/** Equal-width cards: label over "+$0.65". Selected = tint fill + terracotta border. */
@Composable
fun SizeOptions(
    sizes: List<SizeOption>,
    selected: SizeOption?,
    currencySymbol: String,
    onSelect: (SizeOption) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        sizes.forEach { size ->
            val isSelected = size.id == selected?.id
            Column(
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 46.dp)
                    .clip(OptionShape)
                    .background(if (isSelected) BrewkeryColors.Tint else Color.White)
                    .border(optionBorder(isSelected), OptionShape)
                    .selectable(
                        selected = isSelected,
                        role = Role.RadioButton,
                        onClick = { onSelect(size) }
                    )
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = size.label,
                    style = BrewkeryText.GroupTitle,
                    color = if (isSelected) BrewkeryColors.Terracotta else BrewkeryColors.Ink,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = formatExtra(size.extraPrice, currencySymbol),
                    style = BrewkeryText.OptionExtra,
                    color = if (isSelected) BrewkeryColors.Terracotta else BrewkeryColors.InkMuted
                )
            }
        }
    }
}

/** Full-width rows: name on the left, "+$0.50" on the right. */
@Composable
fun MilkOptions(
    options: List<MilkOption>,
    selected: MilkOption?,
    currencySymbol: String,
    onSelect: (MilkOption) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        options.forEach { option ->
            val isSelected = option.id == selected?.id
            val color = if (isSelected) BrewkeryColors.Terracotta else BrewkeryColors.InkMuted
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RowShape)
                    .background(if (isSelected) BrewkeryColors.Tint else Color.White)
                    .border(optionBorder(isSelected), RowShape)
                    .selectable(
                        selected = isSelected,
                        role = Role.RadioButton,
                        onClick = { onSelect(option) }
                    )
                    .padding(horizontal = 12.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = option.name,
                    style = if (isSelected) BrewkeryText.BodyBold else BrewkeryText.Body,
                    color = color,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formatExtra(option.extraPrice, currencySymbol),
                    style = BrewkeryText.RowExtra,
                    color = color
                )
            }
        }
    }
}

/** Chips. Note the selected style here is espresso (dark), unlike size and milk. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SugarOptions(
    levels: List<String>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        levels.forEach { level ->
            val isSelected = level == selected
            Text(
                text = level,
                style = if (isSelected) BrewkeryText.SugarChipSelected else BrewkeryText.SugarChip,
                color = if (isSelected) Color.White else BrewkeryColors.InkMuted,
                modifier = Modifier
                    .clip(RowShape)
                    .background(if (isSelected) BrewkeryColors.Espresso else Color.White)
                    .then(
                        if (isSelected) Modifier
                        else Modifier.border(1.dp, BrewkeryColors.Border, RowShape)
                    )
                    .selectable(
                        selected = isSelected,
                        role = Role.RadioButton,
                        onClick = { onSelect(level) }
                    )
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
    }
}