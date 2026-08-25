package com.marketplace.onehour.common.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Backend serves unversioned /api/... routes with snake_case JSON (standard
 * Laravel Resource output) — LOWER_CASE_WITH_UNDERSCORES maps that onto our
 * camelCase Kotlin fields without renaming every property.
 */
object ApiClient {
    private const val BASE_URL = "https://one-hour-services-backend-staging.onrender.com/"

    private val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    private val authInterceptor = okhttp3.Interceptor { chain ->
        val original = chain.request()
        val token = TokenStore.getToken()
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else {
            original
        }
        chain.proceed(request)
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        // Render's free tier spins down after inactivity and can take 50s+
        // to cold-start — OkHttp's 10s default timeout fails that every time.
        .connectTimeout(90, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(90, TimeUnit.SECONDS)
        .build()

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
