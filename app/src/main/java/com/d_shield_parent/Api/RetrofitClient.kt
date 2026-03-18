package com.d_shield_parent.Api

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {

    private const val BASE_URL = "https://dshieldpro.com/"

    internal val instance: ApiService by lazy {
        val gson = GsonBuilder().setLenient().create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
    val distributorInstance: ApiService by lazy {
        val gson = GsonBuilder().setLenient().create()
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    private val gson = GsonConverterFactory.create()

    //  Send OTP Retrofit instance
    val sendOtpApi: SendOtpApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dshieldpro.com/")
            .addConverterFactory(gson)
            .build()
            .create(SendOtpApiService::class.java)
    }

    // Verify OTP Retrofit instance
    val verifyOtpApi: VerifyOtpApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://dshieldpro.com/")
            .addConverterFactory(gson)
            .build()
            .create(VerifyOtpApiService::class.java)


    }
}
