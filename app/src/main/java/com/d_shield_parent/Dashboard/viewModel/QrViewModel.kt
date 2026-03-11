//package com.d_shield_parent.Dashboard.viewModel
//
//import android.app.Application
//import android.graphics.Bitmap
//import android.util.Log
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.d_shield_parent.Api.RetrofitClient
//import com.d_shield_parent.SharedPreference.shareprefManager
//import com.d_shield_parent.presentation.auth.generateQRCode
//import com.google.gson.Gson
//import com.google.gson.GsonBuilder  // ✅ Add this import
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//
//data class QrState(
//    val isLoading: Boolean = false,
//    val qrBitmap: Bitmap? = null,
//    val error: String? = null,
//    val provisioningJson: String? = null
//)
//
//class QrViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val _state = MutableStateFlow(QrState())
//    val state: StateFlow<QrState> = _state
//
//    private val gson = GsonBuilder()
//        .disableHtmlEscaping()
//        .create()
//
//    fun fetchApkLink(deviceId: Int) {
//        if (_state.value.qrBitmap != null) {
//            Log.d("QrViewModel", "QR already generated, skipping fetch")
//            return
//        }
//        viewModelScope.launch {
//            try {
//                _state.value = QrState(isLoading = true)
//
//                Log.d("QrViewModel", "========================================")
//                Log.d("QrViewModel", "Device ID received: $deviceId")
//                Log.d("QrViewModel", "========================================")
//
//                val token = shareprefManager.getToken()
//                Log.d("QrViewModel", "Token: ${if (token.isNullOrEmpty()) "EMPTY" else "Present (${token.take(20)}...)"}")
//
//                if (token.isNullOrEmpty()) {
//                    Log.e("QrViewModel", "Token is null or empty")
//                    _state.value = QrState(error = "Token not found. Please login again.")
//                    return@launch
//                }
//
//                val authHeader = "Bearer $token"
//                val fullUrl = "https://bmdublog.com/dshield/api/device/$deviceId/provisioning-json"
//                Log.d("QrViewModel", "Full API URL: $fullUrl")
//
//                val response = withContext(Dispatchers.IO) {
//                    RetrofitClient.instance.getProvisioningJson(authHeader, deviceId)
//                }
//
//                Log.d("QrViewModel", "Response code: ${response.code()}")
//                Log.d("QrViewModel", "Response message: ${response.message()}")
//
//                if (response.isSuccessful) {
//                    val provisioningData = response.body()
//                    Log.d("QrViewModel", "📦 Provisioning Data: $provisioningData")
//
//                    if (provisioningData == null) {
//                        Log.e("QrViewModel", "❌ Provisioning data is null")
//                        _state.value = QrState(error = "Provisioning data not found")
//                        return@launch
//                    }
//
//                    if (provisioningData.deviceAdminPackageDownloadLocation.isNullOrEmpty()) {
//                        Log.e("QrViewModel", "❌ APK URL is missing")
//                        _state.value = QrState(error = "APK download link missing")
//                        return@launch
//                    }
//
//                    val provisioningJson = gson.toJson(provisioningData)
//
//                    // ✅ Add verification log
//                    Log.d("QrViewModel", "📄 Provisioning JSON: $provisioningJson")
//                    Log.d("QrViewModel", "📄 JSON Length: ${provisioningJson.length}")
//
//                    // ✅ Check for escaped characters
//                    if (provisioningJson.contains("\\u003d") || provisioningJson.contains("\\u0026")) {
//                        Log.w("QrViewModel", "⚠️ JSON still contains escaped characters!")
//                    } else {
//                        Log.d("QrViewModel", "✅ JSON is clean (no escape sequences)")
//                    }
//
//                    Log.d("QrViewModel", "📲 Generating QR code...")
//                    val qrBitmap = withContext(Dispatchers.Default) {
//                        generateQRCode(provisioningJson)
//                    }
//
//                    Log.d("QrViewModel", "✅ QR code generated: ${qrBitmap.width}x${qrBitmap.height}")
//                    withContext(Dispatchers.Main) {
//                        _state.value = QrState(
//                            isLoading = false,
//                            qrBitmap = qrBitmap,
//                            provisioningJson = provisioningJson,
//                            error = null
//                        )
//                        Log.d("QrViewModel", "✅ State updated with QR bitmap")
//                    }
//                } else {
//                    val errorBody = response.errorBody()?.string()
//                    Log.e("QrViewModel", "========================================")
//                    Log.e("QrViewModel", "API Error Code: ${response.code()}")
//                    Log.e("QrViewModel", "Error Body: $errorBody")
//                    Log.e("QrViewModel", "========================================")
//
//                    val errorMsg = when (response.code()) {
//                        401 -> "Unauthorized. Please login again."
//                        404 -> "Device #$deviceId not found."
//                        403 -> "Access denied for this device"
//                        500 -> "Server error. Please try again later."
//                        else -> "Error: ${response.code()}"
//                    }
//                    withContext(Dispatchers.Main) {
//                        _state.value = QrState(error = errorMsg)
//                    }
//                }
//
//            } catch (e: Exception) {
//                Log.e("QrViewModel", "========================================")
//                Log.e("QrViewModel", "Exception: ${e.message}", e)
//                Log.e("QrViewModel", "Stack trace:", e)
//                Log.e("QrViewModel", "========================================")
//
//                withContext(Dispatchers.Main) {
//                    _state.value = QrState(
//                        error = "Failed to load QR: ${e.localizedMessage ?: "Unknown error"}"
//                    )
//                }
//            }
//        }
//    }
//
//    fun retry(deviceId: Int) {
//        Log.d("QrViewModel", "🔄 Retry requested for device: $deviceId")
//        fetchApkLink(deviceId)
//    }
//}