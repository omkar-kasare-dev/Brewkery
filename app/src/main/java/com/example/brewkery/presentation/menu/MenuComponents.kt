package com.example.brewkery.presentation.menu


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search


import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.example.brewkery.R
import com.example.brewkery.cart.PriceCalculator
import com.example.brewkery.data.model.Category
import com.example.brewkery.data.model.MenuItem
import com.example.brewkery.presentation.common.formatPrice

import com.example.brewkery.ui.theme.BrewkeryColors
import com.example.brewkery.ui.theme.BrewkeryShapes
import com.example.brewkery.ui.theme.BrewkeryText

/** Tinted banner used for both "Store info" and the active-order tracker. */
@Composable
fun InfoBanner(
    emoji: String,
    overline: String,
    title: String,
    subtitle: String,
    pill: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val pillShape = RoundedCornerShape(8.dp)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(BrewkeryShapes.Panel)
            .background(BrewkeryColors.Tint)
            .border(1.dp, BrewkeryColors.Border, BrewkeryShapes.Panel)
            .then(
                if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick)
                else Modifier
            )
            .semantics(mergeDescendants = true) {}
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 16.sp)
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(overline, style = BrewkeryText.Overline, color = BrewkeryColors.Terracotta)
                Spacer(Modifier.width(4.dp))
                Box(
                    Modifier
                        .size(5.dp)
                        .background(BrewkeryColors.Terracotta, CircleShape)
                )
            }
            Text(title, style = BrewkeryText.BannerTitle, color = BrewkeryColors.Ink)
            Text(subtitle, style = BrewkeryText.BannerSubtitle, color = BrewkeryColors.InkMuted)
        }
        Text(
            text = pill,
            style = BrewkeryText.Pill,
            color = BrewkeryColors.Ink,
            modifier = Modifier
                .clip(pillShape)
                .background(Color.White)
                .border(1.dp, BrewkeryColors.Border, pillShape)
                .padding(horizontal = 12.dp, vertical = 5.dp)
        )
    }
}

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val shape = BrewkeryShapes.Card
    val description = stringResource(R.string.search_description)

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        textStyle = BrewkeryText.Search.copy(color = BrewkeryColors.Ink),
        cursorBrush = SolidColor(BrewkeryColors.Terracotta),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
        modifier = modifier
            .fillMaxWidth()
            .height(35.dp)
            .semantics { contentDescription = description },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(Color.White)
                    .border(1.dp, BrewkeryColors.Border, shape)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = BrewkeryColors.InkMuted,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(Modifier.width(8.dp))
                Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search_placeholder),
                            style = BrewkeryText.Search,
                            color = BrewkeryColors.InkMuted
                        )
                    }
                    innerTextField()
                }
            }
        }
    )
}

@Composable
fun CategoryChips(
    categories: List<Category>,
    selectedCategoryId: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        item(key = "all") {
            CategoryChip(
                label = stringResource(R.string.category_all),
                selected = selectedCategoryId == null,
                onClick = { onSelect(null) }
            )
        }
        items(categories, key = { it.id }) { category ->
            CategoryChip(
                label = "${category.icon} ${category.name}",
                selected = category.id == selectedCategoryId,
                onClick = { onSelect(category.id) }
            )
        }
    }
}

@Composable
private fun CategoryChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = label,
        style = if (selected) BrewkeryText.ChipSelected else BrewkeryText.Chip,
        color = if (selected) Color.White else BrewkeryColors.Ink,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) BrewkeryColors.Espresso else Color.White)
            .then(
                if (selected) Modifier
                else Modifier.border(1.dp, BrewkeryColors.Border, CircleShape)
            )
            .selectable(selected = selected, role = Role.Tab, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    )
}

@Composable
fun MenuItemCard(
    item: MenuItem,
    currencySymbol: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imagePlaceholder = remember { ColorPainter(BrewkeryColors.Tint) }
    val ratingDescription = stringResource(
        R.string.rating_description,
        item.rating.toString(),
        item.reviewCount
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(BrewkeryShapes.Card)
            .background(Color.White)
            .border(1.dp, BrewkeryColors.Border, BrewkeryShapes.Card)
            .clickable(
                onClickLabel = stringResource(R.string.customize_item, item.name),
                onClick = onClick
            )
            .semantics(mergeDescendants = true) {}
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null, // decorative: the card's text already names the item
            contentScale = ContentScale.Crop,
            placeholder = imagePlaceholder,
            error = imagePlaceholder,
            modifier = Modifier
                .size(64.dp)
                .clip(BrewkeryShapes.Card)
        )
        Spacer(Modifier.width(12.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(top = 10.dp)
                .heightIn(min = 76.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                item.badge?.let { badge ->
                    Text(
                        text = badge,
                        style = BrewkeryText.Badge,
                        color = BrewkeryColors.BadgeText,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(BrewkeryColors.BadgeBackground)
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                }
                Text(item.name, style = BrewkeryText.CardTitle, color = BrewkeryColors.Ink)
                Text(
                    text = "⭐ ${item.rating} (${item.reviewCount})",
                    style = BrewkeryText.Rating,
                    color = BrewkeryColors.InkMuted,
                    modifier = Modifier.semantics { contentDescription = ratingDescription }
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = formatPrice(PriceCalculator.toCents(item.basePrice), currencySymbol),
                    style = BrewkeryText.PriceCard,
                    color = BrewkeryColors.Terracotta
                )
                Spacer(Modifier.weight(1f))
                // Visual only: the whole card is the click target (a ~19dp button is too small to tap).
                Text(
                    text = stringResource(R.string.customize_action),
                    style = BrewkeryText.Customize,
                    color = Color.White,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(BrewkeryColors.Terracotta)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun CartBar(
    count: Int,
    subtotalText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = BrewkeryShapes.Panel
    val description = pluralStringResource(R.plurals.cart_bar_description, count, count, subtotalText)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, shape)
            .clip(shape)
            .background(BrewkeryColors.Espresso)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) { contentDescription = description }
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(BrewkeryColors.Terracotta, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(count.toString(), style = BrewkeryText.BarCount, color = Color.White)
        }
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                text = stringResource(R.string.view_your_cart),
                style = BrewkeryText.BarLabel,
                color = BrewkeryColors.BarLabel
            )
            Text(subtotalText, style = BrewkeryText.BarTotal, color = Color.White)
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = stringResource(R.string.proceed_to_checkout),
            style = BrewkeryText.BarCta,
            color = BrewkeryColors.Amber
        )
    }
}