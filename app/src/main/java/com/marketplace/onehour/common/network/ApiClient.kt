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

    // The emulator's TLS stack intermittently mis-detects a genuine HTTPS
    // response as cleartext HTTP (java.net.UnknownServiceException:
    // CLEARTEXT communication ... not permitted), even on a request that
    // never left port 443. Retrying on the *same* pooled connection
    // reproduces it again immediately, so force a brand new connection by
    // evicting the pool before each retry. Scoped to just this exception
    // (not all IOExceptions) so a genuine slow-server timeout on a
    // non-idempotent POST never gets silently retried.
    private val connectionPool = okhttp3.ConnectionPool()

    private val retryInterceptor = okhttp3.Interceptor { chain ->
        val request = chain.request()
        var attempt = 0
        var response: okhttp3.Response? = null
        while (response == null) {
            try {
                response = chain.proceed(request)
            } catch (e: java.net.UnknownServiceException) {
                attempt++
                connectionPool.evictAll()
                if (attempt >= 3) throw e
            }
        }
        response
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectionPool(connectionPool)
        .addInterceptor(authInterceptor)
        .addInterceptor(retryInterceptor)
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
