package com.example.brewkery.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.brewkery.R
import com.example.brewkery.cart.PriceCalculator
import com.example.brewkery.data.MenuError
import com.example.brewkery.data.model.MilkOption
import com.example.brewkery.data.model.SizeOption
import com.example.brewkery.presentation.common.EmptyView
import com.example.brewkery.presentation.common.ErrorView
import com.example.brewkery.presentation.common.GradientButton
import com.example.brewkery.presentation.common.LoadingView
import com.example.brewkery.presentation.common.QuantityStepper
import com.example.brewkery.presentation.common.formatPrice
import com.example.brewkery.ui.theme.BrewkeryColors
import com.example.brewkery.ui.theme.BrewkeryText


@Composable
fun DetailScreen(
    onBack: () -> Unit,
    onAddedToCart: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DetailContent(
        uiState = uiState,
        onBack = onBack,
        onSizeSelected = viewModel::onSizeSelected,
        onMilkSelected = viewModel::onMilkSelected,
        onSugarSelected = viewModel::onSugarSelected,
        onDecrement = viewModel::onDecrement,
        onIncrement = viewModel::onIncrement,
        onAddToCart = {
            viewModel.addToCart()
            onAddedToCart()
        },
        onRetry = viewModel::load,
        modifier = modifier
    )
}

@Composable
fun DetailContent(
    uiState: DetailUiState,
    onBack: () -> Unit,
    onSizeSelected: (SizeOption) -> Unit,
    onMilkSelected: (MilkOption) -> Unit,
    onSugarSelected: (String) -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onAddToCart: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BrewkeryColors.Crema)
    ) {
        when (uiState) {
            DetailUiState.Loading -> StateFrame(onBack) {
                LoadingView(contentDescription = stringResource(R.string.loading_menu))
            }

            is DetailUiState.Error -> StateFrame(onBack) {
                ErrorView(
                    message = stringResource(uiState.error.messageRes()),
                    retryLabel = stringResource(R.string.retry),
                    onRetry = onRetry
                )
            }

            DetailUiState.NotFound -> StateFrame(onBack) {
                EmptyView(
                    title = stringResource(R.string.item_not_found),
                    subtitle = stringResource(R.string.item_not_found_hint)
                )
            }

            is DetailUiState.Content -> DetailBody(
                state = uiState,
                onBack = onBack,
                onSizeSelected = onSizeSelected,
                onMilkSelected = onMilkSelected,
                onSugarSelected = onSugarSelected,
                onDecrement = onDecrement,
                onIncrement = onIncrement,
                onAddToCart = onAddToCart
            )
        }
    }
}

private fun MenuError.messageRes(): Int = when (this) {
    MenuError.NETWORK -> R.string.error_network
    MenuError.SERVER -> R.string.error_server
    MenuError.UNKNOWN -> R.string.error_unknown
}

/** Loading / error / not-found keep a back button so the user is never stuck. */
@Composable
private fun StateFrame(onBack: () -> Unit, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        DetailTopBar(onBack = onBack)
        Box(Modifier.weight(1f)) { content() }
    }
}

@Composable
private fun DetailBody(
    state: DetailUiState.Content,
    onBack: () -> Unit,
    onSizeSelected: (SizeOption) -> Unit,
    onMilkSelected: (MilkOption) -> Unit,
    onSugarSelected: (String) -> Unit,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    onAddToCart: () -> Unit
) {
    val item = state.item
    val symbol = state.currencySymbol
    val options = item.customizations

    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
        ) {
            DetailTopBar(onBack = onBack)
            HeroImage(
                imageUrl = item.imageUrl,
                contentDescription = item.name,
                badge = item.badge
            )
            Spacer(Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.Top) {
                Text(
                    text = item.name,
                    style = BrewkeryText.DetailTitle,
                    color = BrewkeryColors.Ink,
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = formatPrice(PriceCalculator.toCents(item.basePrice), symbol),
                    style = BrewkeryText.PriceDetail,
                    color = BrewkeryColors.Terracotta
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.description,
                style = BrewkeryText.Description,
                color = BrewkeryColors.InkMuted
            )
            if (item.ingredients.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.key_ingredients),
                    style = BrewkeryText.SectionLabel,
                    color = BrewkeryColors.InkMuted
                )
                Spacer(Modifier.height(6.dp))
                IngredientChips(item.ingredients)
            }
            Spacer(Modifier.height(12.dp))

            if (options.sizes.isNotEmpty()) {
                OptionGroupCard(
                    title = stringResource(R.string.size_selection),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    SizeOptions(options.sizes, state.selection.size, symbol, onSizeSelected)
                }
            }
            if (options.milkOptions.isNotEmpty()) {
                OptionGroupCard(
                    title = stringResource(R.string.milk_options),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    MilkOptions(options.milkOptions, state.selection.milk, symbol, onMilkSelected)
                }
            }
            if (options.sugarLevels.isNotEmpty()) {
                OptionGroupCard(
                    title = stringResource(R.string.sugar_levels),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    SugarOptions(options.sugarLevels, state.selection.sugarLevel, onSugarSelected)
                }
            }
        }

        // Pinned (deliberate deviation: the prototype lets this row scroll with the content).
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(BrewkeryColors.Crema)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            QuantityStepper(
                quantity = state.quantity,
                onDecrement = onDecrement,
                onIncrement = onIncrement,
                decrementEnabled = state.quantity > 1
            )
            GradientButton(
                text = stringResource(R.string.add_to_cart_price, formatPrice(state.totalCents, symbol)),
                onClick = onAddToCart,
                modifier = Modifier.weight(1f)
            )
        }
    }
}