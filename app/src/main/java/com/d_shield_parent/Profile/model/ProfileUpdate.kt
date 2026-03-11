package com.d_shield_parent.Profile.model

import android.net.Uri
import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val profilePicUri: Uri? = null
)

data class ProfileUpdateResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("retailer")
    val retailer: UpdateRetailer?
)

data class UpdateRetailer(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("phone")
    val phone: String?,

    @SerializedName("alternate_number")
    val alternateNumber: String?,

    @SerializedName("dob")
    val dob: String?,

    @SerializedName("gender")
    val gender: String?,

    @SerializedName("father_or_husband_name")
    val fatherOrHusbandName: String?,

    @SerializedName("mother_name")
    val motherName: String?,

    @SerializedName("nationality")
    val nationality: String?,

    @SerializedName("address")
    val address: String?,

    @SerializedName("shop_name")
    val shopName: String?,

    @SerializedName("mpin")
    val mpin: String?,

    @SerializedName("adhar_number")
    val adharNumber: String?,

    @SerializedName("pancard_number")
    val pancardNumber: String?,

    @SerializedName("profile_pic")
    val profilePic: String?,

    @SerializedName("signature")
    val signature: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("points")
    val points: Int?,

    @SerializedName("performed_by_id")
    val performedById: Int?,

    @SerializedName("performed_by_class")
    val performedByClass: String?,

    @SerializedName("distributor_id")
    val distributorId: Int?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?
)