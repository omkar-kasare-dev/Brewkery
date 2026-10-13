package com.example.brewkery.presentation.cart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.brewkery.R
import com.example.brewkery.cart.Bill
import com.example.brewkery.cart.CartLine
import com.example.brewkery.data.model.StoreMeta
import com.example.brewkery.presentation.common.BackButton
import com.example.brewkery.presentation.common.QuantityStepper
import com.example.brewkery.presentation.common.StepperSize
import com.example.brewkery.presentation.common.formatPrice

import com.example.brewkery.ui.theme.BrewkeryColors
import com.example.brewkery.ui.theme.BrewkeryShapes
import com.example.brewkery.ui.theme.BrewkeryText

@Composable
fun CartTopBar(
    onBack: () -> Unit,
    showClear: Boolean,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BackButton(onClick = onBack)
        Text(
            text = stringResource(R.string.cart_title),
            style = BrewkeryText.ScreenTitle,
            color = BrewkeryColors.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        if (showClear) {
            Text(
                text = stringResource(R.string.clear_cart),
                style = BrewkeryText.ClearCart,
                color = BrewkeryColors.Danger,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .clickable(role = Role.Button, onClick = onClear)
            )
        } else {
            Spacer(Modifier.width(48.dp)) // keeps the title where it was
        }
    }
}

@Composable
fun CartLineRow(
    line: CartLine,
    currencySymbol: String,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Includes the sugar/serving level so two lines of the same item never look identical.
    val options = listOfNotNull(line.size?.label, line.milk?.name, line.sugarLevel).joinToString(" • ")

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(BrewkeryShapes.Card)
            .background(Color.White)
            .border(1.dp, BrewkeryColors.Border, BrewkeryShapes.Card)
            .semantics(mergeDescendants = true) {}
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(line.item.name, style = BrewkeryText.CardTitle, color = BrewkeryColors.Ink)
            if (options.isNotEmpty()) {
                Text(options, style = BrewkeryText.CartSubtitle, color = BrewkeryColors.InkMuted)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = formatPrice(line.lineTotalCents, currencySymbol),
                style = BrewkeryText.CartPrice,
                color = BrewkeryColors.Terracotta
            )
        }
        Spacer(Modifier.width(8.dp))
        QuantityStepper(
            quantity = line.quantity,
            onDecrement = onDecrement,
            onIncrement = onIncrement,
            size = StepperSize.Compact
        )
    }
}

@Composable
fun BillCard(bill: Bill, meta: StoreMeta, modifier: Modifier = Modifier) {
    val symbol = meta.currencySymbol

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(BrewkeryShapes.Card)
            .background(Color.White)
            .border(1.dp, BrewkeryColors.Border, BrewkeryShapes.Card)
            .padding(14.dp)
    ) {
        BillRow(stringResource(R.string.subtotal), formatPrice(bill.subtotalCents, symbol))
        BillRow(stringResource(R.string.delivery_fee), formatPrice(bill.deliveryFeeCents, symbol))
        BillRow(
            label = stringResource(R.string.est_tax, meta.taxRatePercent.toString()),
            value = formatPrice(bill.taxCents, symbol)
        )
        DashedDivider(Modifier.padding(vertical = 8.dp))
        Row(
            modifier = Modifier.semantics(mergeDescendants = true) {},
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.total_payable),
                style = BrewkeryText.TotalLabel,
                color = BrewkeryColors.Ink,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatPrice(bill.totalCents, symbol),
                style = BrewkeryText.TotalValue,
                color = BrewkeryColors.Terracotta
            )
        }
    }
}

@Composable
private fun BillRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = BrewkeryText.Body,
            color = BrewkeryColors.InkMuted,
            modifier = Modifier.weight(1f)
        )
        Text(text = value, style = BrewkeryText.BillValue, color = BrewkeryColors.Ink)
    }
}

@Composable
private fun DashedDivider(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = BrewkeryColors.Border,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx()))
        )
    }
}