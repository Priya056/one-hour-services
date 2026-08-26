package com.marketplace.onehour.common.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // TODO: Configure this with the actual deployed backend URL
    // For development/testing, this should point to the deployed Laravel instance
    // Do NOT use localhost for physical device testing
    private const val BASE_URL = "https://your-deployed-backend-url.com/"
    
    private var authToken: String? = null
    
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
        
        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        val request = requestBuilder.build()
        chain.proceed(request)
    }
    
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor(authInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    
    val apiService: ApiService = retrofit.create(ApiService::class.java)
    
    /**
     * Set the authentication token for API requests
     */
    fun setAuthToken(token: String) {
        authToken = token
    }
    
    /**
     * Clear the authentication token (e.g., on logout)
     */
    fun clearAuthToken() {
        authToken = null
    }
    
    /**
     * Update the base URL (useful for environment switching)
     */
    fun updateBaseUrl(newBaseUrl: String) {
        // Note: This would require recreating the Retrofit instance
        // For simplicity, this is a placeholder for future implementation
    }
}
