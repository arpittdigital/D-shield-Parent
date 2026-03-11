package com.d_shield_parent.Dashboard.model

import com.google.gson.annotations.SerializedName

data class RemoveRequest(
    @SerializedName("IMEI1")
    val imei1: String
)

data class RemoveResponse(
    val Success: Boolean,
    val message: String,
)