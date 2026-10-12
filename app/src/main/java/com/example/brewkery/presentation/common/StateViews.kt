package com.example.brewkery.presentation.common


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewkery.ui.theme.BrewkeryColors
import com.example.brewkery.ui.theme.BrewkeryShapes
import com.example.brewkery.ui.theme.BrewkeryText


@Composable
fun LoadingView(contentDescription: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .semantics { this.contentDescription = contentDescription },
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = BrewkeryColors.Terracotta)
    }
}

@Composable
fun ErrorView(
    message: String,
    retryLabel: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("☕", fontSize = 32.sp)
        Spacer(Modifier.height(12.dp))
        Text(
            text = message,
            style = BrewkeryText.Body,
            color = BrewkeryColors.InkMuted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = onRetry,
            shape = BrewkeryShapes.Button,
            colors = ButtonDefaults.buttonColors(
                containerColor = BrewkeryColors.Terracotta,
                contentColor = Color.White
            )
        ) {
            Text(retryLabel, style = BrewkeryText.BodyBold)
        }
    }
}

@Composable
fun EmptyView(title: String, subtitle: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("🔍", fontSize = 28.sp)
        Spacer(Modifier.height(8.dp))
        Text(title, style = BrewkeryText.BodyBold, color = BrewkeryColors.Ink)
        Text(
            text = subtitle,
            style = BrewkeryText.Body,
            color = BrewkeryColors.InkMuted,
            textAlign = TextAlign.Center
        )
    }
}