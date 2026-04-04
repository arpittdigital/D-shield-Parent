package com.d_shield_parent.otp

import com.google.gson.annotations.SerializedName

data class SendOtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)

data class VerifyOtpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String
)