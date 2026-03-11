package com.d_shield_parent.auth.Model

import kotlinx.serialization.Serializable


@Serializable
data class LoginRequest(
    val phone: String,
    val value: String,
    val login_type: String
)
data class DistributorLoginRequest(
    val phone_no: String,
    val value: String,
    val login_type: String
)

