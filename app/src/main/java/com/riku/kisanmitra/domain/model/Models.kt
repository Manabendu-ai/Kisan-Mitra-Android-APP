package com.riku.kisanmitra.domain.model

import com.riku.kisanmitra.R
import java.util.Date
import java.util.Locale

enum class UserRole {
    FARMER, BUYER, DRIVER, TRADER, NONE
}

data class Crop(
    val id: String,
    val name: String,
    val category: String,
    val currentMandiPrice: Double,
    val priceTrend: Double // percentage
)

data class Listing(
    val id: String,
    val farmerId: String,
    val cropName: String,
    val quantity: Double, // in kg
    val pricePerKg: Double,
    val harvestDate: Date,
    val images: List<String>,
    val status: ListingStatus = ListingStatus.ACTIVE,
    val farmerTrustScore: Float = 4.5f
) {
    val displayImage: Any
        get() = images.firstOrNull() ?: when (cropName.lowercase(Locale.ROOT).trim()) {
            "cabbage" -> R.drawable.cabbage
            "cauliflower" -> R.drawable.cauliflower
            "chilli" -> R.drawable.chilli
            "capcicum" -> R.drawable.capcicum
            "carrot" -> R.drawable.carrot
            "onion" -> R.drawable.onion
            "tomato" -> R.drawable.tomato
            "potato" -> R.drawable.potato
            else -> R.drawable.kisan_mitra_logo
        }
}

enum class ListingStatus {
    ACTIVE, SOLD, EXPIRED
}

data class Order(
    val id: String,
    val listingId: String,
    val buyerId: String,
    val driverId: String?,
    val status: OrderStatus,
    val quantity: Double,
    val totalPrice: Double,
    val timestamp: Long = System.currentTimeMillis()
)

enum class OrderStatus {
    LISTED, ORDER_RECEIVED, TRANSPORT_BOOKED, PICKED_UP, DELIVERED
}

data class AiPriceAdvice(
    val currentPrice: Double,
    val predicted24h: Double,
    val predicted48h: Double,
    val confidence: Double,
    val recommendation: String,
    val reasonText: String
)

data class Trip(
    val id: String,
    val driverId: String,
    val pickupLocation: String,
    val dropLocation: String,
    val weight: Double,
    val distance: Double,
    val earnings: Double,
    val status: TripStatus
)

enum class TripStatus {
    AVAILABLE, ACCEPTED, ON_THE_WAY, DELIVERED
}
