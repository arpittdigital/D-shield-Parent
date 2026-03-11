package com.d_shield_parent.auth.Model

import com.google.gson.annotations.SerializedName


interface BaseLoginResponse {
    val success: Boolean
    val token: String?
    val role: String
}

data class RetailerUser(
    val id: Int,
    val name: String,
    val email: String?,
    val phone: String?,
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
    val distributor_id: Int?,
    val created_at: String?,
    val updated_at: String?
)
data class RetailerResponse(
    override val success: Boolean,
    override val token: String?,
    override val role: String,
    val user: RetailerUser?
) : BaseLoginResponse
data class DistributorUser(
    val id: Int,
    val name: String,
    val company_name: String?,
    val email: String?,
    val phone_no: String?,
    val alternative_no: String?,
    val address: String?,
    val district: String?,
    val code: String?,
    val points: Int?,
    val gst_no: String?,
    val aadhar_card_no: String?,
    val pan_card_no: String?,
    val super_distributor_id: Int?,
    val distributor_id: Int?,
    val status: Boolean?,
    val photo: String?,
    val company_photo: String?,
    val created_at: String?,
    val updated_at: String?
)

data class DistributorResponse(
    override val success: Boolean,
    override val token: String?,
    override val role: String,
    val user: DistributorUser?
) : BaseLoginResponse

