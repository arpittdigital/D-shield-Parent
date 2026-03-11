package com.d_shield_parent.Dashboard.model

data class InstallmentResponse(
    val success: Boolean,
    val data: InstallmentData
)
data class InstallmentData(
    val total_installments: String,
    val paid_installments: Int,
    val pending_installments: Int,
    val overdue_installments: Int,
    val total_paid_amount: String,
    val total_pending_amount: String,
    val installments: List<InstallmentItem>
)
data class InstallmentItem(
    val id: Int,
    val device_id: String,
    val installment_number: String,
    val amount: String,
    val due_date: String,
    val paid_date: String?,
    val status: String,
    val created_at: String,
    val updated_at: String
)
