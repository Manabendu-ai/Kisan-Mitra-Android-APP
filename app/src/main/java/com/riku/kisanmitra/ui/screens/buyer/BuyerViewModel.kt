package com.riku.kisanmitra.ui.screens.buyer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riku.kisanmitra.domain.model.*
import com.riku.kisanmitra.domain.repository.MarketRepository
import com.riku.kisanmitra.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BuyerViewModel @Inject constructor(
    private val repository: MarketRepository
) : ViewModel() {

    private val _livePrices = MutableStateFlow<UiState<List<Crop>>>(UiState.Loading)
    val livePrices: StateFlow<UiState<List<Crop>>> = _livePrices

    private val _listings = MutableStateFlow<UiState<List<Listing>>>(UiState.Loading)
    val listings: StateFlow<UiState<List<Listing>>> = _listings

    private val _orders = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val orders: StateFlow<UiState<List<Order>>> = _orders

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getLivePrices().collect { _livePrices.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repository.getListings().collect { _listings.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repository.getOrdersForBuyer("b1").collect { _orders.value = UiState.Success(it) }
        }
    }

    fun placeOrder(listing: Listing, quantity: Double) {
        viewModelScope.launch {
            val order = Order(
                id = System.currentTimeMillis().toString(),
                listingId = listing.id,
                buyerId = "b1",
                driverId = null,
                status = OrderStatus.ORDER_RECEIVED,
                quantity = quantity,
                totalPrice = quantity * listing.pricePerKg
            )
            repository.placeOrder(order)
        }
    }

    fun updateListingPrice(listingId: String, newPrice: Double) {
        viewModelScope.launch {
            repository.updateListingPrice(listingId, newPrice)
        }
    }
}
