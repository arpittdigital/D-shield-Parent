package com.d_shield_parent.otp

import com.google.gson.annotations.SerializedName

data class SendOtpRequest(
    @SerializedName("customer_phone") val customerPhone: String
)

data class VerifyOtpRequest(
    @SerializedName("customer_phone") val customerPhone: String,
    @SerializedName("otp") val otp: String
)