package com.d_shield_parent.Dashboard.viewModel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d_shield_parent.Api.RetrofitClient
import com.d_shield_parent.RoomDatabase.AppDatabase
import com.d_shield_parent.RoomDatabase.addCustomerList
import com.d_shield_parent.SharedPreference.shareprefManager
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class CustomerViewModel(application: Application) : AndroidViewModel(application) {
    private val customerDao = AppDatabase.Companion.getDatabase(application).customerDao()
    private val TAG = "CustomerViewModel_Debug"

    var isLoading by mutableStateOf(false)
        private set

    var apiSuccess by mutableStateOf(false)
        private set

    var apiError by mutableStateOf<String?>(null)
        private set

    fun addCustomer(customer: addCustomerList) {
        viewModelScope.launch {
            customerDao.insertCustomer(customer)
        }
    }

    suspend fun getAllCustomers(): List<addCustomerList> {
        return customerDao.getAllCustomers()
    }

    fun addCustomerToApi(
        name: String,
        mobile: String,
        email: String,
        address: String,
        aadharCard: String,
        pancard: String,
        alternate: String,
        imei1: String,
        imei2: String,
        productName: String,
        serialNumber: String = "",
        totalAmount: String,
        loanAmount: String,
        downPayment: String,
        monthlyInstallment: String,
        totalInstallment: String,
        emiday: String,
        rateOfInterest: String,
        agreementDate: String,
        loanstartdate: String,
        billingInvoice: String,
        retailerId: Int,
        photoUri: Uri?,
        aadhaarFrontUri: Uri?,
        aadhaarBackUri: Uri?,
        panCardUri: Uri?,
        signatureFilePath: String?,
        onSuccess: (Int) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                isLoading = true
                apiError = null
                apiSuccess = false

                Log.d(TAG, "========== API CALL STARTED ==========")
                Log.d(TAG, " CUSTOMER: $name ($mobile)")
                Log.d(TAG, " PRODUCT: $productName (IMEI1: $imei1)")
                Log.d(TAG, " LOAN: ₹$loanAmount ($totalInstallment installments)")
                Log.d(TAG, " EMI DAY: $emiday")
                Log.d(TAG, " LOAN START DATE: $loanstartdate")

                val context = getApplication<Application>().applicationContext

                // FIXED: Create all RequestBody variables with UNIQUE names
                val customerNameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val customerPhoneBody = mobile.toRequestBody("text/plain".toMediaTypeOrNull())
                val customerEmailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val customerAddressBody = address.toRequestBody("text/plain".toMediaTypeOrNull())
                val aadhaarNumberBody = aadharCard.toRequestBody("text/plain".toMediaTypeOrNull())
                val panNumberBody = pancard.toRequestBody("text/plain".toMediaTypeOrNull())
                val alternatePhoneBody = alternate.toRequestBody("text/plain".toMediaTypeOrNull())
                val imei1Body = imei1.toRequestBody("text/plain".toMediaTypeOrNull())
                val imei2Body = imei2.toRequestBody("text/plain".toMediaTypeOrNull())
                val productNameBody = productName.toRequestBody("text/plain".toMediaTypeOrNull())
                val serialNumberBody = serialNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                val totalAmountBody = totalAmount.toRequestBody("text/plain".toMediaTypeOrNull())

                val loanStartDateBody = loanstartdate.toRequestBody("text/plain".toMediaTypeOrNull())

                val loanAmountBody = loanAmount.toRequestBody("text/plain".toMediaTypeOrNull())
                val downPaymentBody = downPayment.toRequestBody("text/plain".toMediaTypeOrNull())
                val monthlyInstallmentBody = monthlyInstallment.toRequestBody("text/plain".toMediaTypeOrNull())

                //  CRITICAL FIX: Use different variable name (totalInstallmentsBody, not totalInstallment)
                val totalInstallmentsBody = totalInstallment.toRequestBody("text/plain".toMediaTypeOrNull())

                val rateOfInterestBody = rateOfInterest.toRequestBody("text/plain".toMediaTypeOrNull())
                val agreementDateBody = agreementDate.toRequestBody("text/plain".toMediaTypeOrNull())
                val billingInvoiceBody = billingInvoice.toRequestBody("text/plain".toMediaTypeOrNull())
                val retailerIdBody = retailerId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val emiDayBody = emiday.toRequestBody("text/plain".toMediaTypeOrNull())

                Log.d(TAG, "\n REQUEST BODY VALUES:")
                Log.d(TAG, "   emi_day: $emiday")
                Log.d(TAG, "   loan_start_date: $loanstartdate")
                Log.d(TAG, "   total_installments: $totalInstallment")
                Log.d(TAG, "   loan_amount: $loanAmount")
                Log.d(TAG, "   monthly_installment: $monthlyInstallment")

                Log.d(TAG, "\n Converting images to MultipartBody.Part...")

                //  Images ko MultipartBody.Part mein convert karo
                val livePhotoPart = photoUri?.let { uri ->
                    createImagePart("live_photo", uri, context)
                }

                val aadhaarFrontPart = aadhaarFrontUri?.let { uri ->
                    createImagePart("aadhaar_front", uri, context)
                }

                val aadhaarBackPart = aadhaarBackUri?.let { uri ->
                    createImagePart("aadhaar_back", uri, context)
                }

                val panCardPart = panCardUri?.let { uri ->
                    createImagePart("pan_card", uri, context)
                }

                //  Signature
                val signaturePart = signatureFilePath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        Log.d(TAG, "   Signature file found: ${file.length()} bytes")
                        val requestBody = file.asRequestBody("image/png".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData("signature", file.name, requestBody)
                    } else {
                        Log.e(TAG, "   Signature file NOT found: $path")
                        null
                    }
                }

                Log.d(TAG, "\n Files prepared:")
                Log.d(TAG, "   Photo: ${livePhotoPart != null}")
                Log.d(TAG, "   Aadhaar Front: ${aadhaarFrontPart != null}")
                Log.d(TAG, "   Aadhaar Back: ${aadhaarBackPart != null}")
                Log.d(TAG, "   PAN Card: ${panCardPart != null}")
                Log.d(TAG, "   Signature: ${signaturePart != null}")

                val token = "Bearer " + shareprefManager.getToken()
                Log.d(TAG, "\n Token: ${token.take(50)}...")
                Log.d(TAG, "\n Making API call to AddDevice endpoint...")

                //  FIXED: Use the pre-created RequestBody variables
                val response = RetrofitClient.instance.AddDevice(
                    token = token,
                    customerName = customerNameBody,
                    customerPhone = customerPhoneBody,
                    customerEmail = customerEmailBody,
                    customerAddress = customerAddressBody,
                    aadhaarNumber = aadhaarNumberBody,
                    panNumber = panNumberBody,
                    alternatePhone = alternatePhoneBody,
                    imei1 = imei1Body,
                    imei2 = imei2Body,
                    productName = productNameBody,
                    serialNumber = serialNumberBody,
                    totalAmount = totalAmountBody,

                    emiDayBody = emiDayBody,
                    loanStartDateBody = loanStartDateBody,

                    loanAmount = loanAmountBody,
                    downPayment = downPaymentBody,
                    monthlyInstallment = monthlyInstallmentBody,

                    //  FIXED: Use pre-created variable directly
                    totalInstallmentsBody = totalInstallmentsBody,

                    rateOfInterest = rateOfInterestBody,
                    agreementDate = agreementDateBody,
                    billingInvoice = billingInvoiceBody,
                    retailerId = retailerIdBody,
                    live_photo = livePhotoPart,
                    aadhaar_front = aadhaarFrontPart,
                    aadhaar_back = aadhaarBackPart,
                    pan_card = panCardPart,
                    signature = signaturePart
                )

                Log.d(TAG, "\nAPI RESPONSE:")
                Log.d(TAG, "   Response Code: ${response.code()}")
                Log.d(TAG, "   Response Message: ${response.message()}")
                Log.d(TAG, "   Is Successful: ${response.isSuccessful}")

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    Log.d(TAG, "========== DEVICE ID EXTRACTION ==========")
                    Log.d(TAG, "device.id: ${responseBody?.device?.id}")
                    Log.d(TAG, "device.deviceId: ${responseBody?.device?.deviceId}")


                    val deviceId = responseBody?.device?.id
                        ?: responseBody?.device?.deviceId
                        ?: 0

                    Log.d(TAG, "Final Device ID to use: $deviceId")
                    Log.d(TAG, "==========================================")

                    if (deviceId == 0) {
                        isLoading = false
                        val errorMsg = "Device ID not found in response"
                        Log.e(TAG, " $errorMsg")
                        apiError = errorMsg
                        onError(errorMsg)
                        return@launch
                    }

                    Log.d(TAG, "SUCCESS!")
                    Log.d(TAG, "   Device ID: $deviceId")
                    apiSuccess = true
                    isLoading = false
                    onSuccess(deviceId)
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMsg = "Error ${response.code()}: $errorBody"
                    Log.e(TAG, " FAILED: $errorMsg")
                    apiError = errorMsg
                    isLoading = false
                    onError(errorMsg)
                }

                Log.d(TAG, "========== API CALL COMPLETED ==========\n")

            } catch (e: Exception) {
                val errorMsg = "Exception: ${e.message}"
                apiError = errorMsg
                isLoading = false
                Log.e(TAG, " EXCEPTION OCCURRED!")
                Log.e(TAG, "   Exception Type: ${e.javaClass.simpleName}")
                Log.e(TAG, "   Exception Message: ${e.message}")
                Log.e(TAG, "   Stack Trace:", e)
                Log.e(TAG, "========== API CALL FAILED ==========\n")
                onError(errorMsg)
            }
        }
    }

    // Helper function to create MultipartBody.Part from URI
    private fun createImagePart(
        partName: String,
        uri: Uri,
        context: Context
    ): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val tempFile = File(context.cacheDir, "temp_${partName}_${System.currentTimeMillis()}.jpg")

            tempFile.outputStream().use { output ->
                inputStream?.copyTo(output)
            }
            inputStream?.close()

            if (tempFile.exists() && tempFile.length() > 0) {
                Log.d(TAG, "    $partName created: ${tempFile.length()} bytes")
                val requestBody = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
            } else {
                Log.e(TAG, "    $partName file is empty or doesn't exist")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "    Error creating $partName", e)
            null
        }
    }

    //  Function to reset API state if needed
    fun resetApiState() {
        apiSuccess = false
        apiError = null
        isLoading = false
        Log.d(TAG, "🔄 API State Reset")
    }
}