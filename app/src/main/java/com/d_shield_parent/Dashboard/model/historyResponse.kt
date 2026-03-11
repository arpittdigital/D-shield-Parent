package com.d_shield_parent.Dashboard.model

data class historyResponse(
    val success: Boolean,
    val devices: List<Device>

)
data class Device(
    val id: Int,
    val retailer_id: String,
    val customer_name: String,
    val customer_phone: String,
    val customer_email: String,
    val customer_address: String,
    val aadhaar_number: String,
    val pan_number: String,
    val alternate_phone: String,
    val imei1: String,
    val imei2: String,
    val product_name: String,
    val serial_number: String,
    val loan_amount: String,
    val down_payment: String,
    val monthly_installment: String,
    val total_installments: String,
    val rate_of_interest: String,
    val agreement_date: String,
    val billing_invoice_number: String,

    val live_photo: String?,
    val aadhaar_front: String?,
    val aadhaar_back: String?,
    val pan_card: String?,
    val signature: String?,

    val status: String,
    val is_deleted: Boolean,
    val created_at: String,
    val updated_at: String
)
