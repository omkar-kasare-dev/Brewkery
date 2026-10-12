package com.example.brewkery

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.brewkery.navigation.BrewkeryNavHost
import com.example.brewkery.presentation.menu.MenuUiState
import com.example.brewkery.presentation.menu.MenuViewModel
import dagger.hilt.android.AndroidEntryPoint

/** TEMPORARY: data-layer smoke test. Replaced when the real UI is built. */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

   // private val viewModel: MenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                BrewkeryNavHost()
                /*
                val state by viewModel.uiState.collectAsState()

                val summary = when (val s = state) {
                    MenuUiState.Loading -> "Loading..."
                    is MenuUiState.Success ->
                        "Success: ${s.menu.items.size} items, ${s.menu.categories.size} categories, " +
                                "visible=${s.visibleItems.size}, first=${s.menu.items.firstOrNull()?.name}"
                    is MenuUiState.Error -> "Error: ${s.error}"
                }
                LaunchedEffect(summary) { Log.d("BrewkeryCheck", summary) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding()
                        .padding(16.dp)
                ) {
                    Text(summary)
                    Button(onClick = { viewModel.loadMenu() }) { Text("Retry") }
                    if (state is MenuUiState.Success) {
                        Button(onClick = { viewModel.selectCategory("cat_bakery") }) { Text("Bakery only") }
                        Button(onClick = { viewModel.selectCategory(null) }) { Text("All items") }
                    }
                } */
            }
        }
    }
}