package com.d_shield_parent.presentation.auth

//data class addDeviceRequest(
//    val customer_name: String,
//    val customer_phone: String,
//    val customer_email: String,
//    val customer_address: String,
//    val aadhaar_number: String,
//    val pan_number: String,
//    val alternate_phone: String,
//    val imei1: String,
//    val imei2: String,
//    val product_name: String,
//    val serial_number: String,
//    val loan_amount: String,
//    val down_payment: String,
//    val monthly_installment: String,
//    val total_installments: String,
//    val rate_of_interest: String,
//    val agreement_date: String,
//    val billing_invoice_number: String,
//    val retailer_id: Int,
//    val live_photo: String,
//    val aadhaar_front: String,
//    val aadhaar_back: String,
//    val pan_card: String,
//    val signature: String
//)
//gender,nationality,fatherhusband,emi day loan stat date



data class AddDeviceRequest(
    // Customer
    val customer_name: String,
    val customer_phone: String,
    val customer_email: String,
    val customer_address: String,
    val aadhaar_number: String,
    val pan_number: String,
    val alternate_phone: String,

    // Device
    val imei1: String,
    val imei2: String,
    val product_name: String,
    val serial_number: String,

    // Loan / EMI
    val total_amount: String,
    val down_payment: String,
    val loan_amount: String,
    val rate_of_interest: String,
    val total_installments: Int,
    val monthly_installment: String,

    // EMI Control (offline lock)
    val emi_day: Int,
    val loan_start_date: String,
    val agreement_date: String,

    // Meta
    val billing_invoice_number: String,
    val retailer_id: Int,

    // Documents
    val live_photo: String,
    val aadhaar_front: String,
    val aadhaar_back: String,
    val pan_card: String,
    val signature: String
)
