package com.d_shield_parent.presentation.auth

data class DeviceAddResponse(
    val message: String?,
    val device: Device?
)

data class Device(
    val customer_name: String?,
    val customer_phone: String?,
    val customer_email: String?,
    val customer_address: String?,
    val aadhaar_number: String?,
    val pan_number: String?,
    val alternate_phone: String?,
    val imei1: String?,
    val imei2: String?,
    val product_name: String?,
    val serial_number: String?,
    val loan_amount: String?,
    val down_payment: String?,
    val monthly_installment: String?,
    val total_installments: String?,
    val rate_of_interest: String?,
    val loan_start_date: String?,
    val emi_day: String?,
    val deviceId: Int?,
    val total_amount: String?,
    val agreement_date: String?,
    val billing_invoice_number: String?,
    val retailer_id: Int?,
    val updated_at: String?,
    val created_at: String?,
    val id: Int?
)
