package com.mediscan.app.data.remote

import com.mediscan.app.data.model.ExtractionResult
import com.mediscan.app.data.model.QualityCheck
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * FastApiService — Retrofit interface for the AI prescription extraction backend.
 * Base URL: http://10.0.2.2:8000/ (emulator) or http://192.168.x.x:8000/ (physical device)
 */
interface FastApiService {

    /** Health check — verify server is running and models are loaded */
    @GET("health")
    suspend fun healthCheck(): Map<String, Any>

    /** Quick quality check (~50ms) — reject bad images before extraction */
    @POST("check-quality-base64")
    suspend fun checkQuality(
        @Body request: Map<String, String> // {"image": "base64..."}
    ): QualityCheck

    /** Main AI extraction endpoint (2-8s) — returns structured medication data */
    @POST("extract-base64")
    suspend fun extractPrescription(
        @Body request: Map<String, String> // {"image": "base64..."}
    ): ExtractionResult
}
