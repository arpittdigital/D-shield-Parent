package com.d_shield_parent.Dashboard.model

import com.google.gson.JsonElement

data class getDeviceResponse(
    val success: Boolean,
    val devices: List<DeviceModel>
)

data class DeviceModel(
    val id: Int,
    val firebase_device_id: String?,
    val retailer_id: String,

    // Customer
    val customer_name: String,
    val customer_phone: String,
    val phone_number: String?,
    val customer_email: String?,
    val customer_address: String?,
    val aadhaar_number: String?,
    val pan_number: String?,
    val alternate_phone: String?,

    // Pairing & MDM
    val pair_token: String?,
    val pair_token_expires_at: String?,
    val is_paired: String?,
    val android_device_id: String?,
    val mdm_status: String?,
    val paired_at: String?,
    val activated_at: String?,

    // Device Info
    val imei1: String,
    val imei2: String?,
    val product_name: String?,
    val serial_number: String?,
    val device_info: JsonElement?,

    // Loan Details
    val loan_amount: String,
    val down_payment: String,
    val monthly_installment: String,
    val total_installments: String,
    val rate_of_interest: String,
    val total_amount: String,
    val emi_day: String,
    val loan_start_date: String,
    val agreement_date: String,

    // Status
    val status: String,
    val is_deleted: Boolean,

    // Documents
    val live_photo: String?,
    val aadhaar_front: String?,
    val aadhaar_back: String?,
    val pan_card: String?,
    val signature: String?,

    // Billing & Timestamps
    val billing_invoice_number: String?,
    val created_at: String,
    val updated_at: String,

    // Firebase Status
    val firebase: FirebaseStatus?
)

data class FirebaseStatus(
    val status: String?,
    val last_seen: String?,
    val lock_status: String?
)