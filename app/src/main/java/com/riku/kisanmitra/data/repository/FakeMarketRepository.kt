package com.riku.kisanmitra.data.repository

import com.riku.kisanmitra.domain.model.*
import com.riku.kisanmitra.domain.repository.MarketRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class FakeMarketRepository @Inject constructor() : MarketRepository {

    private val listings = MutableStateFlow<List<Listing>>(emptyList())

    private val trips = MutableStateFlow<List<Trip>>(
        listOf(
            Trip("t1", "d1", "Mandya", "Mysuru", 500.0, 45.0, 1200.0, TripStatus.AVAILABLE),
            Trip("t2", "d1", "Tumakuru", "Bengaluru", 1200.0, 70.0, 2500.0, TripStatus.AVAILABLE),
            Trip("t3", "d1", "Hassan", "Mangaluru", 800.0, 170.0, 4500.0, TripStatus.AVAILABLE),
            Trip("t4", "d1", "Chamarajanagar", "Mysuru", 600.0, 60.0, 1500.0, TripStatus.AVAILABLE),
            Trip("t5", "d1", "Kolar", "Chennai", 1500.0, 250.0, 8000.0, TripStatus.AVAILABLE)
        )
    )

    override fun getLivePrices(): Flow<List<Crop>> = flow {
        emit(
            listOf(
                Crop("c1", "Tomato", "Vegetable", 28.0, 5.2),
                Crop("c2", "Onion", "Vegetable", 42.0, -2.1),
                Crop("c3", "Potato", "Vegetable", 20.0, 1.5),
                Crop("c4", "Green Chili", "Vegetable", 65.0, 8.4)
            )
        )
    }

    override fun getListings(): Flow<List<Listing>> = listings

    override suspend fun createListing(listing: Listing): Result<Unit> {
        delay(1000)
        listings.value = listings.value + listing
        return Result.success(Unit)
    }

    override suspend fun getAiPriceAdvice(cropType: String, quantity: Double): Result<AiPriceAdvice> {
        delay(1500)
        
        // Base prices for randomization
        val basePrice = when (cropType.lowercase(Locale.ROOT).trim()) {
            "tomato" -> 28.0
            "onion" -> 42.0
            "potato" -> 20.0
            "green chili" -> 65.0
            else -> 30.0
        }

        // Generate realistic fluctuations
        val currentPrice = basePrice + Random.nextDouble(-2.0, 2.0)
        val predicted24h = currentPrice * (1 + Random.nextDouble(-0.15, 0.15))
        val predicted48h = predicted24h * (1 + Random.nextDouble(-0.10, 0.10))
        val confidence = Random.nextDouble(0.75, 0.98)

        val (recommendation, reason) = when {
            predicted24h > currentPrice * 1.05 -> {
                "HOLD" to "Price is expected to rise by ${( (predicted24h/currentPrice - 1) * 100).toInt()}% in the next 24h due to low arrivals in major Mandis."
            }
            predicted24h < currentPrice * 0.95 -> {
                "SELL NOW" to "Market supply is increasing rapidly. Prices may drop further by ${( (1 - predicted24h/currentPrice) * 100).toInt()}% by tomorrow."
            }
            else -> {
                "SELL" to "Market is stable. Current prices are fair based on quality and seasonal trends."
            }
        }

        return Result.success(
            AiPriceAdvice(
                currentPrice = String.format("%.2f", currentPrice).toDouble(),
                predicted24h = String.format("%.2f", predicted24h).toDouble(),
                predicted48h = String.format("%.2f", predicted48h).toDouble(),
                confidence = String.format("%.2f", confidence).toDouble(),
                recommendation = recommendation,
                reasonText = reason
            )
        )
    }

    override fun getOrdersForFarmer(farmerId: String): Flow<List<Order>> = flow {
        emit(
            listOf(
                Order("o1", "1", "b1", "d1", OrderStatus.ORDER_RECEIVED, 200.0, 5000.0),
                Order("o2", "2", "b2", "d1", OrderStatus.PICKED_UP, 500.0, 17500.0)
            )
        )
    }

    override fun getOrdersForBuyer(buyerId: String): Flow<List<Order>> = flow {
        emit(
            listOf(
                Order("o1", "1", buyerId, "d1", OrderStatus.ORDER_RECEIVED, 200.0, 5000.0)
            )
        )
    }

    override suspend fun placeOrder(order: Order): Result<Unit> {
        delay(1000)
        return Result.success(Unit)
    }

    override fun getTrips(): Flow<List<Trip>> = trips

    override suspend fun updateTripStatus(tripId: String, status: String): Result<Unit> {
        delay(500)
        trips.value = trips.value.map {
            if (it.id == tripId) it.copy(status = TripStatus.valueOf(status)) else it
        }
        return Result.success(Unit)
    }

    override suspend fun updateListingPrice(listingId: String, newPrice: Double): Result<Unit> {
        delay(500)
        listings.value = listings.value.map {
            if (it.id == listingId) it.copy(pricePerKg = newPrice) else it
        }
        return Result.success(Unit)
    }
}
