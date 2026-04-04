package com.d_shield_parent.Api

import com.d_shield_parent.Dashboard.model.DeviceUpdateResponse
import com.d_shield_parent.Dashboard.model.InstallmentPayResponse
import com.d_shield_parent.Dashboard.model.InstallmentResponse
import com.d_shield_parent.Dashboard.model.MpinRequest
import com.d_shield_parent.Dashboard.model.MpinResponse
import com.d_shield_parent.Dashboard.model.RemoveRequest
import com.d_shield_parent.Dashboard.model.RemoveResponse
import com.d_shield_parent.Dashboard.model.TransactionResponse
import com.d_shield_parent.Dashboard.model.getDeviceResponse
import com.d_shield_parent.Dashboard.model.historyResponse
import com.d_shield_parent.Dashboard.model.installmentRequest
import com.d_shield_parent.Dashboard.model.updateRequest
import com.d_shield_parent.Profile.model.LogoutResponse
import com.d_shield_parent.Profile.model.ProfileResponse
import com.d_shield_parent.Profile.model.ProfileUpdateResponse
import com.d_shield_parent.auth.Model.DistributorLoginRequest
import com.d_shield_parent.auth.Model.DistributorResponse
import com.d_shield_parent.auth.Model.LoginRequest
import com.d_shield_parent.auth.Model.RetailerResponse
import com.d_shield_parent.otp.SendOtpRequest
import com.d_shield_parent.otp.SendOtpResponse
import com.d_shield_parent.otp.VerifyOtpRequest
import com.d_shield_parent.otp.VerifyOtpResponse
import com.d_shield_parent.presentation.auth.DeviceAddResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/distributor/login")
    suspend fun LoginDistributor(
        @Body request: DistributorLoginRequest
    ): Response<DistributorResponse>

    @POST("api/login")
    suspend fun LoginRetailer(
        @Body request: LoginRequest
    ): Response<RetailerResponse>



    @POST("api/retailer/set-mpin")
    suspend fun SetMpin(
        @Header("Authorization") token: String,
        @Body request: MpinRequest
    ): Response<MpinResponse>

    @GET("api/retailer/logout")
    suspend fun LogoutRetailer(
        @Header("Authorization") token: String
    ): Response<LogoutResponse>

    @GET("api/retailer/profile")
    suspend fun ProfileRetailer(
        @Header("Authorization") token: String
    ): Response<ProfileResponse>



    @GET("api/get-devices")
    suspend fun getDevices(
        @Header("Authorization") token: String
    ): Response<getDeviceResponse>

    @POST("api/remove-device")
    suspend fun removeDevice(
        @Header("Authorization") token: String,
    @Body removeRequest: RemoveRequest
    ): Response<RemoveResponse>


    @PUT("api/retailer/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("name") name: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("phone") phone: RequestBody?,
        @Part("address") address: RequestBody?,
        @Part profile_pic: MultipartBody.Part?
    ): Response<ProfileUpdateResponse>
    @GET("api/deleted-devices")
    suspend fun deleteHistory(
        @Header("Authorization") token: String
    ): Response<historyResponse>

    @POST("api/device/change-status")
    suspend fun updateStatus(
        @Header("Authorization") token: String,
        @Body request: updateRequest
    ): Response<DeviceUpdateResponse>

//    @GET("api/device/{device_id}/provisioning-json")
//    suspend fun getProvisioningJson(
//        @Header("Authorization") token: String,
//        @Path("device_id") deviceId: Int
//    ): Response<ProvisioningData>


    @POST("api/installment/pay")
    suspend fun installmentPay(
        @Header("Authorization") token: String,
        @Body request: installmentRequest
    ): Response<InstallmentPayResponse>
    @GET("api/device/{device_id}/installments")
    suspend fun emiSchedule(
        @Header("Authorization") token: String,
        @Path("device_id") deviceId: Int
    ): Response<InstallmentResponse>

    @Multipart
    @POST("api/add-device")
    suspend fun AddDevice(
        @Header("Authorization") token: String,
        @Part("customer_name") customerName: RequestBody,
        @Part("customer_phone") customerPhone: RequestBody,
        @Part("customer_email") customerEmail: RequestBody,
        @Part("customer_address") customerAddress: RequestBody,
        @Part("aadhaar_number") aadhaarNumber: RequestBody,
        @Part("pan_number") panNumber: RequestBody,
        @Part("alternate_phone") alternatePhone: RequestBody,
        @Part("imei1") imei1: RequestBody,
        @Part("imei2") imei2: RequestBody,
        @Part("product_name") productName: RequestBody,
        @Part("serial_number") serialNumber: RequestBody,
        @Part("total_amount") totalAmount: RequestBody,
        @Part("emi_day") emiDayBody : RequestBody,
        @Part("loan_start_date") loanStartDateBody: RequestBody,
        @Part("loan_amount") loanAmount: RequestBody,
        @Part("down_payment") downPayment: RequestBody,
        @Part("monthly_installment") monthlyInstallment: RequestBody,
        @Part("total_installments") totalInstallmentsBody: RequestBody,
        @Part("rate_of_interest") rateOfInterest: RequestBody,
        @Part("agreement_date") agreementDate: RequestBody,
        @Part("billing_invoice_number") billingInvoice: RequestBody,
        @Part("retailer_id") retailerId: RequestBody,
        @Part live_photo: MultipartBody.Part?,
        @Part aadhaar_front: MultipartBody.Part?,
        @Part aadhaar_back: MultipartBody.Part?,
        @Part pan_card: MultipartBody.Part?,
        @Part signature: MultipartBody.Part?
    ): Response<DeviceAddResponse>

    @GET("api/retailer/history")
    suspend fun getTransactionHistory(
        @Header("Authorization") token: String
    ): TransactionResponse
}
// Send OTP API
interface SendOtpApiService {
    @POST("api/send-otp")
    suspend fun sendOtp(
        @Body request: SendOtpRequest
    ): Response<SendOtpResponse>
}

// Verify OTP API
interface VerifyOtpApiService {
    @POST("api/verify-otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): Response<VerifyOtpResponse>
}








