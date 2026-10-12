package com.example.brewkery.presentation.order

import androidx.lifecycle.ViewModel
import com.example.brewkery.cart.CartRepository
import com.example.brewkery.cart.Order
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    cartRepository: CartRepository
) : ViewModel() {

    val order: StateFlow<Order?> = cartRepository.activeOrder
}