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

@Singleton
class FakeMarketRepository @Inject constructor() : MarketRepository {

    private val listings = MutableStateFlow<List<Listing>>(
        listOf(
            Listing("1", "f1", "Tomato", 500.0, 25.0, Date(), listOf("https://cdn.pixabay.com/photo/2011/03/16/16/01/tomatoes-5356_1280.jpg")),
            Listing("2", "f2", "Onion", 1200.0, 35.0, Date(), listOf("https://cdn.pixabay.com/photo/2016/05/04/13/46/onion-1371434_1280.jpg")),
            Listing("3", "f3", "Potato", 800.0, 18.0, Date(), listOf("https://cdn.pixabay.com/photo/2016/08/11/08/04/potatoes-1585060_1280.jpg"))
        )
    )

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
        return Result.success(
            AiPriceAdvice(
                currentPrice = 25.0,
                predicted24h = 27.5,
                predicted48h = 24.0,
                confidence = 0.92,
                recommendation = "SELL NOW",
                reasonText = "Price is expected to peak in 24h due to high demand in nearby Mandi, but will drop as new supply arrives from neighboring districts."
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
}
