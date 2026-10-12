package com.example.brewkery.presentation.common


import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.brewkery.R
import com.example.brewkery.ui.theme.BrewkeryColors


@Composable
fun BackButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    CircleIconButton(
        onClick = onClick,
        contentDescription = stringResource(R.string.navigate_back),
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            tint = BrewkeryColors.Ink,
            modifier = Modifier.size(16.dp)
        )
    }
}