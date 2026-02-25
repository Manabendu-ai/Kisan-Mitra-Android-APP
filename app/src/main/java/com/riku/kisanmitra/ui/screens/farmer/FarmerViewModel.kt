package com.riku.kisanmitra.ui.screens.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riku.kisanmitra.domain.model.*
import com.riku.kisanmitra.domain.repository.MarketRepository
import com.riku.kisanmitra.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class FarmerViewModel @Inject constructor(
    private val repository: MarketRepository
) : ViewModel() {

    private val _listingsState = MutableStateFlow<UiState<List<Listing>>>(UiState.Loading)
    val listingsState: StateFlow<UiState<List<Listing>>> = _listingsState

    private val _ordersState = MutableStateFlow<UiState<List<Order>>>(UiState.Loading)
    val ordersState: StateFlow<UiState<List<Order>>> = _ordersState

    private val _aiAdviceState = MutableStateFlow<UiState<AiPriceAdvice>?>(null)
    val aiAdviceState: StateFlow<UiState<AiPriceAdvice>?> = _aiAdviceState

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            repository.getListings().collect { _listingsState.value = UiState.Success(it) }
        }
        viewModelScope.launch {
            repository.getOrdersForFarmer("f1").collect { _ordersState.value = UiState.Success(it) }
        }
    }

    fun getPriceAdvice(cropType: String, quantity: Double) {
        viewModelScope.launch {
            _aiAdviceState.value = UiState.Loading
            repository.getAiPriceAdvice(cropType, quantity)
                .onSuccess { _aiAdviceState.value = UiState.Success(it) }
                .onFailure { _aiAdviceState.value = UiState.Error(it.message ?: "Error") }
        }
    }

    fun postListing(
        cropName: String,
        quantity: Double,
        price: Double,
        images: List<String>,
        onListingPosted: () -> Unit
    ) {
        viewModelScope.launch {
            val finalImages = if (images.isEmpty()) {
                when (cropName.lowercase(Locale.ROOT).trim()) {
                    "onion" -> listOf("https://cdn.pixabay.com/photo/2016/05/04/13/46/onion-1371434_1280.jpg")
                    "potato" -> listOf("https://cdn.pixabay.com/photo/2016/08/11/08/04/potatoes-1585060_1280.jpg")
                    "tomato" -> listOf("https://cdn.pixabay.com/photo/2011/03/16/16/01/tomatoes-5356_1280.jpg")
                    else -> emptyList()
                }
            } else {
                images
            }

            val listing = Listing(
                id = System.currentTimeMillis().toString(),
                farmerId = "f1",
                cropName = cropName,
                quantity = quantity,
                pricePerKg = price,
                harvestDate = Date(),
                images = finalImages
            )
            repository.createListing(listing)
            onListingPosted()
        }
    }
}
