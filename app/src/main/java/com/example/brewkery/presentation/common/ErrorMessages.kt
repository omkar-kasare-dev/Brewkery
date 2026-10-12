package com.example.brewkery.presentation.common



import androidx.annotation.StringRes
import com.example.brewkery.R
import com.example.brewkery.data.MenuError

@StringRes
fun MenuError.messageRes(): Int = when (this) {
    MenuError.NETWORK -> R.string.error_network
    MenuError.SERVER -> R.string.error_server
    MenuError.UNKNOWN -> R.string.error_unknown
}