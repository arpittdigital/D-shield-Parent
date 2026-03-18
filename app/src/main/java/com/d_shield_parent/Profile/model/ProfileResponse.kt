package com.d_shield_parent.Profile.model

data class ProfileResponse(
    val success: Boolean,
    val data: ProfileDataResponse?
)

data class ProfileDataResponse(
    val retailer: Retailer?,
    val wallet_balance: String?,
    val enrolled_devices: Int?,
    val active_devices: Int?,
    val locked_devices: Int?,
    val points: Int?
)

data class Retailer(
    val id: Int?,
    val name: String?,
    val phone: String?,
    val email: String?,
    val alternate_number: String?,
    val dob: String?,
    val gender: String?,
    val father_or_husband_name: String?,
    val mother_name: String?,
    val nationality: String?,
    val address: String?,
    val shop_name: String?,
    val adhar_number: String?,
    val pancard_number: String?,
    val profile_pic: String?,
    val signature: String?,
    val status: String?,
    val points: Int?,
    val performed_by_id: String?,
    val performed_by_class: String?,
    val distributor_id: String?,
    val created_at: String?,
    val updated_at: String?
)