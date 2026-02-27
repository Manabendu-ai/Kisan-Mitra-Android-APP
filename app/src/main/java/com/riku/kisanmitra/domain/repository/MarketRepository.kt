package com.riku.kisanmitra.domain.repository

import com.riku.kisanmitra.domain.model.AiPriceAdvice
import com.riku.kisanmitra.domain.model.Crop
import com.riku.kisanmitra.domain.model.Listing
import com.riku.kisanmitra.domain.model.Order
import com.riku.kisanmitra.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface MarketRepository {
    fun getLivePrices(): Flow<List<Crop>>
    fun getListings(): Flow<List<Listing>>
    suspend fun createListing(listing: Listing): Result<Unit>
    suspend fun getAiPriceAdvice(cropType: String, quantity: Double): Result<AiPriceAdvice>
    fun getOrdersForFarmer(farmerId: String): Flow<List<Order>>
    fun getOrdersForBuyer(buyerId: String): Flow<List<Order>>
    suspend fun placeOrder(order: Order): Result<Unit>
    fun getTrips(): Flow<List<Trip>>
    suspend fun updateTripStatus(tripId: String, status: String): Result<Unit>
    suspend fun updateListingPrice(listingId: String, newPrice: Double): Result<Unit>
}
