package com.example.brewkery.presentation.common


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.brewkery.R
import com.example.brewkery.ui.theme.BrewkeryColors
import com.example.brewkery.ui.theme.BrewkeryShapes
import com.example.brewkery.ui.theme.BrewkeryText


enum class StepperSize { Large, Compact }

@Composable
fun QuantityStepper(
    quantity: Int,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    size: StepperSize = StepperSize.Large,
    decrementEnabled: Boolean = true
) {
    val large = size == StepperSize.Large
    val shape = if (large) BrewkeryShapes.Button else RoundedCornerShape(8.dp)
    val quantityDescription = stringResource(R.string.quantity_value, quantity)

    Row(
        modifier = modifier
            .height(if (large) 40.dp else 26.dp)
            .clip(shape)
            .background(if (large) Color.White else BrewkeryColors.Tint)
            .border(1.dp, BrewkeryColors.Border, shape)
            .padding(horizontal = if (large) 6.dp else 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        StepperButton(
            symbol = "−",
            description = stringResource(R.string.decrease_quantity),
            width = if (large) 24.dp else 18.dp,
            enabled = decrementEnabled,
            onClick = onDecrement
        )
        Text(
            text = quantity.toString(),
            style = BrewkeryText.BodyBold,
            color = BrewkeryColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .widthIn(min = 14.dp)
                .semantics { contentDescription = quantityDescription }
        )
        StepperButton(
            symbol = "+",
            description = stringResource(R.string.increase_quantity),
            width = if (large) 24.dp else 18.dp,
            enabled = true,
            onClick = onIncrement
        )
    }
}

@Composable
private fun StepperButton(
    symbol: String,
    description: String,
    width: Dp,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .width(width)
            .fillMaxHeight()
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = symbol,
            style = BrewkeryText.BodyBold,
            color = BrewkeryColors.Ink.copy(alpha = if (enabled) 1f else 0.3f)
        )
    }
}