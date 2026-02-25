package com.riku.kisanmitra.data.api

import retrofit2.http.Body
import retrofit2.http.POST

data class AiPriceRequest(
    val crop_type: String,
    val quantity_kg: Double,
    val location: String,
    val harvest_date: String
)

data class AiPriceResponse(
    val current_price: Double,
    val predicted_24h: Double,
    val predicted_48h: Double,
    val confidence: Double,
    val recommendation: String,
    val reason_text: String
)

interface AiApiService {
    @POST("/api/ai/price-advice")
    suspend fun getPriceAdvice(@Body request: AiPriceRequest): AiPriceResponse
}
