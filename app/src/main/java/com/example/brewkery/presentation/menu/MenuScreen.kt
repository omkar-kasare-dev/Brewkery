package com.example.brewkery.presentation.menu


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.brewkery.R
import com.example.brewkery.data.MenuError
import com.example.brewkery.presentation.common.CircleIconButton
import com.example.brewkery.presentation.common.EmptyView
import com.example.brewkery.presentation.common.ErrorView
import com.example.brewkery.presentation.common.LoadingView
import com.example.brewkery.presentation.common.formatPrice
import com.example.brewkery.presentation.common.formatWait
import com.example.brewkery.ui.theme.BrewkeryColors
import com.example.brewkery.ui.theme.BrewkeryText


@Composable
fun MenuScreen(
    onItemClick: (itemId: Int) -> Unit,
    onCartClick: () -> Unit,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MenuViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()

    MenuContent(
        uiState = uiState,
        query = query,
        onQueryChange = viewModel::onQueryChange,
        onCategorySelect = viewModel::selectCategory,
        onRetry = viewModel::loadMenu,
        onItemClick = onItemClick,
        onCartClick = onCartClick,
        onOrderClick = onOrderClick,
        modifier = modifier
    )
}

@Composable
fun MenuContent(
    uiState: MenuUiState,
    query: String,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onRetry: () -> Unit,
    onItemClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onOrderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier
        .fillMaxSize()
        .background(BrewkeryColors.Crema)
    ) {
        when (uiState) {
            MenuUiState.Loading -> LoadingView(
                contentDescription = stringResource(R.string.loading_menu),
                modifier = Modifier.statusBarsPadding()
            )

            is MenuUiState.Error -> ErrorView(
                message = stringResource(uiState.error.messageRes()),
                retryLabel = stringResource(R.string.retry),
                onRetry = onRetry,
                modifier = Modifier.statusBarsPadding()
            )

            is MenuUiState.Success -> MenuList(
                state = uiState,
                query = query,
                onQueryChange = onQueryChange,
                onCategorySelect = onCategorySelect,
                onItemClick = onItemClick,
                onCartClick = onCartClick,
                onOrderClick = onOrderClick
            )
        }
    }
}

private fun MenuError.messageRes(): Int = when (this) {
    MenuError.NETWORK -> R.string.error_network
    MenuError.SERVER -> R.string.error_server
    MenuError.UNKNOWN -> R.string.error_unknown
}

@Composable
private fun MenuList(
    state: MenuUiState.Success,
    query: String,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onItemClick: (Int) -> Unit,
    onCartClick: () -> Unit,
    onOrderClick: () -> Unit
) {
    val symbol = state.meta.currencySymbol
    val navigationBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomSpace = if (state.cartCount > 0) 84.dp else 16.dp

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding(),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 12.dp,
                bottom = navigationBarHeight + bottomSpace
            )
        ) {
            item(key = "header") {
                MenuHeader(
                    cartCount = state.cartCount,
                    onCartClick = onCartClick,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }
            state.activeOrder?.let { order ->
                item(key = "active-order") {
                    InfoBanner(
                        emoji = "☕",
                        overline = stringResource(R.string.active_order_label),
                        title = stringResource(R.string.active_order_title, order.ticketId),
                        subtitle = stringResource(
                            R.string.active_order_subtitle,
                            formatWait(order.estimatedWait)
                        ),
                        pill = stringResource(R.string.active_order_track),
                        onClick = onOrderClick,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }
            item(key = "store-info") {
                InfoBanner(
                    emoji = "🛵",
                    overline = stringResource(R.string.store_info_label),
                    title = stringResource(R.string.delivery_in, state.meta.estimatedDeliveryTime),
                    subtitle = stringResource(
                        R.string.flat_fee,
                        formatPrice(
                            com.example.brewkery.cart.PriceCalculator.toCents(state.meta.deliveryFee),
                            symbol
                        )
                    ),
                    pill = stringResource(R.string.store_open),
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
            item(key = "search") {
                SearchField(
                    query = query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }
            item(key = "chips") {
                CategoryChips(
                    categories = state.categories,
                    selectedCategoryId = state.selectedCategoryId,
                    onSelect = onCategorySelect,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
            if (state.items.isEmpty()) {
                item(key = "empty") {
                    EmptyView(
                        title = stringResource(R.string.empty_title),
                        subtitle = stringResource(R.string.empty_subtitle)
                    )
                }
            } else {
                items(state.items, key = { it.id }) { menuItem ->
                    MenuItemCard(
                        item = menuItem,
                        currencySymbol = symbol,
                        onClick = { onItemClick(menuItem.id) },
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                }
            }
        }

        if (state.cartCount > 0) {
            CartBar(
                count = state.cartCount,
                subtotalText = formatPrice(state.cartSubtotalCents, symbol),
                onClick = onCartClick,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
            )
        }
    }
}

@Composable
private fun MenuHeader(
    cartCount: Int,
    onCartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(BrewkeryColors.Espresso, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.avatar_initials),
                style = BrewkeryText.Avatar,
                color = Color.White
            )
        }
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.menu_overline),
                style = BrewkeryText.HeaderOverline,
                color = BrewkeryColors.InkMuted
            )
            Text(
                text = stringResource(R.string.menu_title),
                style = BrewkeryText.HeaderTitle,
                color = BrewkeryColors.Ink
            )
        }
        Box {
            CircleIconButton(
                onClick = onCartClick,
                contentDescription = pluralStringResource(
                    R.plurals.cart_button_description, cartCount, cartCount
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_shopping_bag),
                    contentDescription = null,
                    tint = BrewkeryColors.Ink,
                    modifier = Modifier.size(16.dp)
                )
            }
            if (cartCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 3.dp, end = 3.dp)
                        .size(14.dp)
                        .background(BrewkeryColors.Terracotta, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cartCount.toString(),
                        style = BrewkeryText.BadgeCount,
                        color = Color.White
                    )
                }
            }
        }
    }
}