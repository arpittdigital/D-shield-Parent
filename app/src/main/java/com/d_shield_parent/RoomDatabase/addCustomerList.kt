package com.d_shield_parent.RoomDatabase

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "customer_table")
data class addCustomerList(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val mobileno: String,
    val email: String?,
 //   val gender: String?,
 //   val nationality: String?,
  //  val fatherhusband: String?,
    val aadharcard: String?,
    val address: String?,
    val pancard: String?,
    val alternate: String?,
    val productname: String?,
    val imei1: String?,
    val imei2: String?,
    val loanamount: String?,
    val loanfrequency: String?,
    val rateofinterest: String?,
    val agreementdate: String?,
    val firstinstallment: String?,
    val downpayment: String?,
    val billinginvoice: String?,
    val totalinstallment: String?,
    val emiDates: String = "", // Example: "15/01/2025,15/02/2025,15/03/2025"

    // NEW: Payment Status for each EMI (comma-separated true/false)
    val emiPaymentStatus: String = "", // Example: "false,false,false"

    val photo: String?,
    val aadhaarfront: String?,
    val aadhaarback: String?,
    val pancard1: String?,
    val signature: String?,
    val createdAt: Long = System.currentTimeMillis()
)