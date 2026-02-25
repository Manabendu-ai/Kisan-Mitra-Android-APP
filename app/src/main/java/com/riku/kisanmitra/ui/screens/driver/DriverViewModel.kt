package com.riku.kisanmitra.ui.screens.driver

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.riku.kisanmitra.domain.model.Trip
import com.riku.kisanmitra.domain.model.TripStatus
import com.riku.kisanmitra.domain.repository.MarketRepository
import com.riku.kisanmitra.ui.state.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriverViewModel @Inject constructor(
    private val repository: MarketRepository
) : ViewModel() {

    private val _tripsState = MutableStateFlow<UiState<List<Trip>>>(UiState.Loading)
    val tripsState: StateFlow<UiState<List<Trip>>> = _tripsState

    private val _isOnline = MutableStateFlow(true)
    val isOnline: StateFlow<Boolean> = _isOnline

    init {
        loadTrips()
    }

    private fun loadTrips() {
        viewModelScope.launch {
            repository.getTrips().collect {
                _tripsState.value = UiState.Success(it)
            }
        }
    }

    fun toggleOnlineStatus() {
        _isOnline.value = !_isOnline.value
    }

    fun acceptTrip(tripId: String) {
        viewModelScope.launch {
            repository.updateTripStatus(tripId, TripStatus.ACCEPTED.name)
        }
    }

    fun completeTrip(tripId: String) {
        viewModelScope.launch {
            repository.updateTripStatus(tripId, TripStatus.DELIVERED.name)
        }
    }
}
