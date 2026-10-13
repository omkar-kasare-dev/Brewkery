package com.example.brewkery.presentation.order

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.brewkery.R
import com.example.brewkery.cart.Order
import com.example.brewkery.presentation.common.EmptyView
import com.example.brewkery.presentation.common.EspressoButton
import com.example.brewkery.presentation.common.formatWait
import com.example.brewkery.ui.theme.BrewkeryColors
import com.example.brewkery.ui.theme.BrewkeryShapes
import com.example.brewkery.ui.theme.BrewkeryText

@Composable
fun OrderScreen(
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OrderViewModel = hiltViewModel()
) {
    val order by viewModel.order.collectAsStateWithLifecycle()
    OrderContent(order = order, onBackToMenu = onBackToMenu, modifier = modifier)
}

@Composable
fun OrderContent(
    order: Order?,
    onBackToMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrewkeryColors.Crema)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(16.dp)
    ) {
        if (order == null) {
            // Only reachable if the process was killed while on this screen.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                EmptyView(
                    title = stringResource(R.string.no_active_order),
                    subtitle = stringResource(R.string.no_active_order_hint),
                    emoji = "☕"
                )
            }
        } else {
            // Content is spread over the available height; when it doesn't fit
            // (landscape, small screens, large fonts) it scrolls instead of clipping.
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                val minHeight = maxHeight
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .heightIn(min = minHeight),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OrderHero()
                    TicketCard(order)
                }
            }
            Spacer(Modifier.height(12.dp))
        }
        EspressoButton(text = stringResource(R.string.back_to_menu), onClick = onBackToMenu)
    }
}

@Composable
private fun OrderHero(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = BrewkeryColors.Terracotta.copy(alpha = 0.35f),
                    spotColor = BrewkeryColors.Terracotta.copy(alpha = 0.35f)
                )
                .clip(CircleShape)
                .background(BrewkeryColors.Tint)
                .border(2.dp, BrewkeryColors.Terracotta, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("☕", fontSize = 32.sp)
        }
        Spacer(Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.order_dispatched),
            style = BrewkeryText.OrderOverline,
            color = BrewkeryColors.Terracotta
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.order_heading),
            style = BrewkeryText.OrderHeading,
            color = BrewkeryColors.Ink
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.order_subtitle),
            style = BrewkeryText.Body,
            color = BrewkeryColors.InkMuted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun TicketCard(order: Order, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(BrewkeryShapes.Panel)
            .background(Color.White)
            .border(1.dp, BrewkeryColors.Border, BrewkeryShapes.Panel)
            .semantics(mergeDescendants = true) {}
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.order_ticket),
                    style = BrewkeryText.TicketLabel,
                    color = BrewkeryColors.InkMuted
                )
                Text(
                    text = "#${order.ticketId}",
                    style = BrewkeryText.TicketId,
                    color = BrewkeryColors.Ink
                )
            }
            Text(
                text = stringResource(R.string.status_preparing),
                style = BrewkeryText.StatusChip,
                color = BrewkeryColors.PreparingText,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(BrewkeryColors.PreparingBackground)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
        TicketDivider()
        TicketRow(
            label = stringResource(R.string.estimated_wait),
            value = formatWait(order.estimatedWait),
            valueColor = BrewkeryColors.Terracotta
        )
        Spacer(Modifier.height(6.dp))
        TicketRow(
            label = stringResource(R.string.items_ordered),
            value = stringResource(R.string.items_count, order.itemCount),
            valueColor = BrewkeryColors.Ink
        )
        TicketDivider()
        Text(
            text = stringResource(R.string.status_label),
            style = BrewkeryText.StatusLabel,
            color = BrewkeryColors.InkMuted
        )
        Text(
            text = stringResource(R.string.status_accepted),
            style = BrewkeryText.BodyBold,
            color = BrewkeryColors.Success
        )
    }
}

@Composable
private fun TicketRow(label: String, value: String, valueColor: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, style = BrewkeryText.Body, color = BrewkeryColors.InkMuted)
        Text(value, style = BrewkeryText.BodyBold, color = valueColor)
    }
}

@Composable
private fun TicketDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(vertical = 10.dp),
        thickness = 1.dp,
        color = BrewkeryColors.Border
    )
}