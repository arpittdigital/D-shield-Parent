package com.d_shield_parent.otp

import com.d_shield_parent.Api.RetrofitClient

class OtpRepository {

    private val sendOtpApi = RetrofitClient.sendOtpApi
    private val verifyOtpApi = RetrofitClient.verifyOtpApi

    suspend fun sendOtp(mobile: String): Result<SendOtpResponse> {
        return try {
            val response = sendOtpApi.sendOtp(SendOtpRequest(customerPhone = mobile))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Failed to send OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun verifyOtp(mobile: String, otp: String): Result<VerifyOtpResponse> {
        return try {
            val response = verifyOtpApi.verifyOtp(VerifyOtpRequest(customerPhone = mobile, otp = otp))
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "Invalid OTP"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}