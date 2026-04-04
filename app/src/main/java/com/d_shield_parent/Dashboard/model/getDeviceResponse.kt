package com.d_shield_parent.Dashboard.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class getDeviceResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("devices")
    val devices: List<DeviceModel>
)

data class DeviceModel(
    @SerializedName("id")
    val id: Int,
    @SerializedName("firebase_device_id")
    val firebaseDeviceId: String?,
    @SerializedName("retailer_id")
    val retailerId: Int,
    @SerializedName("customer_name")
    val customerName: String?,
    @SerializedName("customer_phone")
    val customerPhone: String?,
    @SerializedName("phone_number")
    val phoneNumber: String?,
    @SerializedName("customer_email")
    val customerEmail: String?,
    @SerializedName("customer_address")
    val customerAddress: String?,
    @SerializedName("aadhaar_number")
    val aadhaarNumber: String?,
    @SerializedName("pan_number")
    val panNumber: String?,
    @SerializedName("alternate_phone")
    val alternatePhone: String?,
    @SerializedName("imei1")
    val imei1: String?,
    @SerializedName("pair_token")
    val pairToken: String?,
    @SerializedName("pair_token_expires_at")
    val pairTokenExpiresAt: String?,
    @SerializedName("is_paired")
    val isPaired: Int,
    @SerializedName("android_device_id")
    val androidDeviceId: String?,
    @SerializedName("mdm_status")
    val mdmStatus: String?,
    @SerializedName("paired_at")
    val pairedAt: String?,
    @SerializedName("imei2")
    val imei2: String?,
    @SerializedName("product_name")
    val productName: String?,
    @SerializedName("serial_number")
    val serialNumber: String?,
    @SerializedName("device_info")
    val deviceInfo: String?,
    @SerializedName("loan_amount")
    val loanAmount: String?,
    @SerializedName("down_payment")
    val downPayment: String?,
    @SerializedName("monthly_installment")
    val monthlyInstallment: String?,
    @SerializedName("total_installments")
    val totalInstallments: Int?,
    @SerializedName("rate_of_interest")
    val rateOfInterest: String?,
    @SerializedName("agreement_date")
    val agreementDate: String?,
    @SerializedName("activated_at")
    val activatedAt: String?,
    @SerializedName("billing_invoice_number")
    val billingInvoiceNumber: String?,
    @SerializedName("live_photo")
    val livePhoto: String?,
    @SerializedName("aadhaar_front")
    val aadhaarFront: String?,
    @SerializedName("aadhaar_back")
    val aadhaarBack: String?,
    @SerializedName("pan_card")
    val panCard: String?,
    @SerializedName("signature")
    val signature: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("is_deleted")
    val isDeleted: Boolean,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("fcm_id")
    val fcmId: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("total_amount")
    val totalAmount: String?,
    @SerializedName("emi_day")
    val emiDay: String?,
    @SerializedName("loan_start_date")
    val loanStartDate: String?,
    @SerializedName("firebase")
    val firebase: FirebaseStatus?
)

data class FirebaseStatus(
    @SerializedName("status")
    val status: String?,
    @SerializedName("last_seen")
    val lastSeen: String?,
    @SerializedName("lock_status")
    val lockStatus: String?
)