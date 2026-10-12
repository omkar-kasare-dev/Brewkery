package com.example.brewkery.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.brewkery.R
import com.example.brewkery.cart.CartLine
import com.example.brewkery.presentation.common.EmptyView
import com.example.brewkery.presentation.common.ErrorView
import com.example.brewkery.presentation.common.EspressoButton
import com.example.brewkery.presentation.common.GradientButton
import com.example.brewkery.presentation.common.LoadingView
import com.example.brewkery.presentation.common.formatPrice
import com.example.brewkery.presentation.common.messageRes
import com.example.brewkery.ui.theme.BrewkeryColors


@Composable
fun CartScreen(
    onBack: () -> Unit,
    onOrderPlaced: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CartContent(
        uiState = uiState,
        onBack = onBack,
        onIncrement = viewModel::onIncrement,
        onDecrement = viewModel::onDecrement,
        onClearCart = viewModel::clearCart,
        onPlaceOrder = { if (viewModel.placeOrder()) onOrderPlaced() },
        onRetry = viewModel::load,
        modifier = modifier
    )
}

@Composable
fun CartContent(
    uiState: CartUiState,
    onBack: () -> Unit,
    onIncrement: (CartLine) -> Unit,
    onDecrement: (CartLine) -> Unit,
    onClearCart: () -> Unit,
    onPlaceOrder: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    val hasLines = (uiState as? CartUiState.Content)?.lines?.isNotEmpty() == true

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BrewkeryColors.Crema)
            .statusBarsPadding()
    ) {
        CartTopBar(
            onBack = onBack,
            showClear = hasLines,
            onClear = onClearCart,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        when (uiState) {
            CartUiState.Loading -> Box(Modifier.weight(1f)) {
                LoadingView(contentDescription = stringResource(R.string.loading_menu))
            }

            is CartUiState.Error -> Box(Modifier.weight(1f)) {
                ErrorView(
                    message = stringResource(uiState.error.messageRes()),
                    retryLabel = stringResource(R.string.retry),
                    onRetry = onRetry
                )
            }

            is CartUiState.Content -> CartBody(
                state = uiState,
                onBack = onBack,
                onIncrement = onIncrement,
                onDecrement = onDecrement,
                onPlaceOrder = onPlaceOrder
            )
        }
    }
}

@Composable
private fun ColumnScope.CartBody(
    state: CartUiState.Content,
    onBack: () -> Unit,
    onIncrement: (CartLine) -> Unit,
    onDecrement: (CartLine) -> Unit,
    onPlaceOrder: () -> Unit
) {
    val symbol = state.meta.currencySymbol

    if (state.lines.isEmpty()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            EmptyView(
                title = stringResource(R.string.cart_empty_title),
                subtitle = stringResource(R.string.cart_empty_subtitle),
                emoji = "🛍️"
            )
            Spacer(Modifier.height(8.dp))
            EspressoButton(text = stringResource(R.string.back_to_menu), onClick = onBack)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .weight(1f)
            .fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(state.lines, key = { it.id }) { line ->
            CartLineRow(
                line = line,
                currencySymbol = symbol,
                onIncrement = { onIncrement(line) },
                onDecrement = { onDecrement(line) }
            )
        }
    }

    // Bill + Place Order stay pinned to the bottom, as in the prototype.
    Column(
        modifier = Modifier
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        BillCard(bill = state.bill, meta = state.meta)
        Spacer(Modifier.height(12.dp))
        GradientButton(
            text = stringResource(R.string.place_order, formatPrice(state.bill.totalCents, symbol)),
            onClick = onPlaceOrder,
            height = 44.dp,
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        )
    }
}