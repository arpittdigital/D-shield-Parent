package com.d_shield_parent.Dashboard.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d_shield_parent.Api.RetrofitClient
import com.d_shield_parent.Dashboard.History
import com.d_shield_parent.Dashboard.HistoryRepository
import com.d_shield_parent.Dashboard.model.RemoveRequest
import com.d_shield_parent.Dashboard.model.getDeviceResponse
import com.d_shield_parent.Dashboard.model.updateRequest
import com.d_shield_parent.Dashboard.model.InstallmentResponse
import com.d_shield_parent.Dashboard.model.installmentRequest
import com.d_shield_parent.SharedPreference.shareprefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CustomerListState {
    object Idle : CustomerListState()
    object Loading : CustomerListState()
    data class Success(val data: getDeviceResponse) : CustomerListState()
    data class Error(val message: String) : CustomerListState()
}

sealed class EMIScheduleState {
    object Idle : EMIScheduleState()
    object Loading : EMIScheduleState()
    data class Success(val data: InstallmentResponse) : EMIScheduleState()
    data class Error(val message: String) : EMIScheduleState()
}

enum class DeviceStatus(val value: String) {
    LOCK("lock"),
    UNLOCK("unlock")
}

class CustomerListViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "CustomerListViewModel"

    private val _customerListState =
        MutableStateFlow<CustomerListState>(CustomerListState.Idle)
    val customerListState: StateFlow<CustomerListState> =
        _customerListState.asStateFlow()

    private val _emiScheduleStates = mutableMapOf<Int, MutableStateFlow<EMIScheduleState>>()

    init {
        fetchCustomerList()
    }
    fun getEMIScheduleState(deviceId: Int): StateFlow<EMIScheduleState> {
        return _emiScheduleStates.getOrPut(deviceId) {
            MutableStateFlow(EMIScheduleState.Idle)
        }.asStateFlow()
    }

    fun fetchCustomerList() {
        viewModelScope.launch {
            _customerListState.value = CustomerListState.Loading
            Log.d(TAG, " Fetching customer list")

            try {
                val token = shareprefManager.getToken()

                if (token.isNullOrEmpty()) {
                    _customerListState.value =
                        CustomerListState.Error("Authentication token missing")
                    return@launch
                }

                val authHeader = "Bearer $token"
                val response = RetrofitClient.instance.getDevices(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d(TAG, "Devices fetched: ${data.devices.size}")
                    _customerListState.value = CustomerListState.Success(data)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, " Fetch failed: $error")
                    _customerListState.value =
                        CustomerListState.Error("Server error ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e(TAG, " Exception", e)
                _customerListState.value =
                    CustomerListState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }

    fun fetchEMISchedule(deviceId: Int) {
        viewModelScope.launch {
            val stateFlow = _emiScheduleStates.getOrPut(deviceId) {
                MutableStateFlow(EMIScheduleState.Idle)
            }

            stateFlow.value = EMIScheduleState.Loading
            Log.d(TAG, "📅 Fetching EMI schedule for device ID: $deviceId")

            try {
                val token = shareprefManager.getToken()

                if (token.isNullOrEmpty()) {
                    stateFlow.value =
                        EMIScheduleState.Error("Authentication token missing")
                    return@launch
                }

                val authHeader = "Bearer $token"
                val response = RetrofitClient.instance.emiSchedule(authHeader, deviceId)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d(TAG, " EMI Schedule fetched: ${data.data.installments.size} installments")
                    stateFlow.value = EMIScheduleState.Success(data)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, " EMI fetch failed: $error")
                    stateFlow.value =
                        EMIScheduleState.Error("Failed to load EMI schedule")
                }

            } catch (e: Exception) {
                Log.e(TAG, " Exception fetching EMI", e)
                stateFlow.value =
                    EMIScheduleState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }

    fun markInstallmentAsPaid(
        deviceId: Int,
        installmentId: Int
    ) {
        viewModelScope.launch {
            Log.d(TAG, " Marking installment as paid: ID=$installmentId")

            try {
                val token = shareprefManager.getToken()
                if (token.isNullOrEmpty()) {
                    Log.e(TAG, " Token missing")
                    return@launch
                }

                val request = installmentRequest(installment_id = installmentId)
                val response = RetrofitClient.instance.installmentPay(
                    "Bearer $token",
                    request
                )

                if (response.isSuccessful) {
                    Log.d(TAG, " Installment marked as paid")
                    // Refresh EMI schedule to get updated data
                    fetchEMISchedule(deviceId)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, " Payment marking failed: $error")
                }

            } catch (e: Exception) {
                Log.e(TAG, " Exception in markInstallmentAsPaid", e)
            }
        }
    }

    fun updateDeviceStatus(
        imei1: String,
        currentStatus: String
    ) {
        viewModelScope.launch {
            _customerListState.value = CustomerListState.Loading

            val token = shareprefManager.getToken()
            if (token.isNullOrEmpty()) {
                _customerListState.value =
                    CustomerListState.Error("Authentication token missing")
                return@launch
            }

            val isCurrentlyUnlocked = currentStatus.equals("active", ignoreCase = true) ||
                    currentStatus.equals("unlocked", ignoreCase = true)

            val newStatus = if (isCurrentlyUnlocked) {
                DeviceStatus.LOCK.value
            } else {
                DeviceStatus.UNLOCK.value
            }

            Log.d(TAG, " Current: $currentStatus → Sending: $newStatus for IMEI1=$imei1")

            try {
                val request = updateRequest(
                    imei1 = imei1,
                    status = newStatus
                )

                val response = RetrofitClient.instance.updateStatus(
                    "Bearer $token",
                    request
                )

                if (response.isSuccessful) {
                    Log.d(TAG, "Status updated successfully")
                    fetchCustomerList()
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, " Status update failed: $error")
                    _customerListState.value =
                        CustomerListState.Error("Failed to update device status")
                }

            } catch (e: Exception) {
                Log.e(TAG, "Exception", e)
                _customerListState.value =
                    CustomerListState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }

    fun removeDevice(imei1: String) {
        viewModelScope.launch {
            val currentState = _customerListState.value
            val deviceToDelete = if (currentState is CustomerListState.Success) {
                currentState.data.devices.find { it.imei1 == imei1 }
            } else null

            _customerListState.value = CustomerListState.Loading
            Log.d(TAG, "🗑️ Removing device IMEI1=$imei1")

            try {
                val token = shareprefManager.getToken()

                if (token.isNullOrEmpty()) {
                    _customerListState.value =
                        CustomerListState.Error("Authentication token missing")
                    return@launch
                }

                val response = RetrofitClient.instance.removeDevice(
                    token = "Bearer $token",
                    removeRequest = RemoveRequest(imei1)
                )

                if (response.isSuccessful) {
                    Log.d(TAG, " Device removed successfully")

                    deviceToDelete?.let { device ->
                        val currentDate = java.text.SimpleDateFormat(
                            "dd/MM/yyyy",
                            java.util.Locale.getDefault()
                        ).format(java.util.Date())

                        val history = History(
                            name = device.customer_name,
                            number = device.customer_phone,
                            date = currentDate,
                            imei1 = device.imei1,
                            action = "Unlocked & Deleted"
                        )

                      HistoryRepository.addHistory(history)
                        Log.d(TAG, "Added to history: ${device.customer_name}")
                    }

                    fetchCustomerList()
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "Remove failed: $error")
                    _customerListState.value =
                        CustomerListState.Error("Failed to remove device")
                }

            } catch (e: Exception) {
                Log.e(TAG, " Exception", e)
                _customerListState.value =
                    CustomerListState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }

    fun refreshList() {
        fetchCustomerList()
    }
}




//package com.d_shield_parent.Dashboard.viewModel
//
//import android.app.Application
//import android.util.Log
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.d_shield_parent.Api.RetrofitClient
//import com.d_shield_parent.Dashboard.model.RemoveRequest
//import com.d_shield_parent.Dashboard.model.getDeviceResponse
//import com.d_shield_parent.Dashboard.model.updateRequest
//import com.d_shield_parent.SharedPreference.shareprefManager
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//
////  UI State
//sealed class CustomerListState {
//    object Idle : CustomerListState()
//    object Loading : CustomerListState()
//    data class Success(val data: getDeviceResponse) : CustomerListState()
//    data class Error(val message: String) : CustomerListState()
//}
//
//enum class DeviceStatus(val value: String) {
//    LOCK("lock"),
//    UNLOCK("unlock")
//}
//
//class CustomerListViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val TAG = "CustomerListViewModel"
//
//    private val _customerListState =
//        MutableStateFlow<CustomerListState>(CustomerListState.Idle)
//    val customerListState: StateFlow<CustomerListState> =
//        _customerListState.asStateFlow()
//
//    init {
//        fetchCustomerList()
//    }
//
//    // 🔹 Fetch Devices
//    fun fetchCustomerList() {
//        viewModelScope.launch {
//            _customerListState.value = CustomerListState.Loading
//            Log.d(TAG, "📡 Fetching customer list")
//
//            try {
//                val token = shareprefManager.getToken()
//
//                if (token.isNullOrEmpty()) {
//                    _customerListState.value =
//                        CustomerListState.Error("Authentication token missing")
//                    return@launch
//                }
//
//                val authHeader = "Bearer $token"
//                val response = RetrofitClient.instance.getDevices(authHeader)
//
//                if (response.isSuccessful && response.body() != null) {
//                    val data = response.body()!!
//                    Log.d(TAG, "✅ Devices fetched: ${data.devices.size}")
//                    _customerListState.value = CustomerListState.Success(data)
//                } else {
//                    val error = response.errorBody()?.string()
//                    Log.e(TAG, "❌ Fetch failed: $error")
//                    _customerListState.value =
//                        CustomerListState.Error("Server error ${response.code()}")
//                }
//
//            } catch (e: Exception) {
//                Log.e(TAG, "❌ Exception", e)
//                _customerListState.value =
//                    CustomerListState.Error(e.localizedMessage ?: "Something went wrong")
//            }
//        }
//    }
//
//    fun updateDeviceStatus(
//        imei1: String,
//        currentStatus: String  // "active", "unlocked", "locked" - jo bhi backend bheje
//    ) {
//        viewModelScope.launch {
//            _customerListState.value = CustomerListState.Loading
//
//            val token = shareprefManager.getToken()
//            if (token.isNullOrEmpty()) {
//                _customerListState.value =
//                    CustomerListState.Error("Authentication token missing")
//                return@launch
//            }
//
//            // FIXED: Check for both "active" AND "unlocked"
//            // Agar device unlocked/active hai → LOCK bhejo
//            // Agar device locked hai → UNLOCK bhejo
//            val isCurrentlyUnlocked = currentStatus.equals("active", ignoreCase = true) ||
//                    currentStatus.equals("unlocked", ignoreCase = true)
//
//            val newStatus = if (isCurrentlyUnlocked) {
//                DeviceStatus.LOCK.value      // Device unlocked hai, LOCK karo
//            } else {
//                DeviceStatus.UNLOCK.value    // Device locked hai, UNLOCK karo
//            }
//
//            Log.d(TAG, "🔐 Current: $currentStatus (unlocked=$isCurrentlyUnlocked) → Sending: $newStatus for IMEI1=$imei1")
//
//            try {
//                val request = updateRequest(
//                    imei1 = imei1,
//                    status = newStatus
//                )
//
//                val response = RetrofitClient.instance.updateStatus(
//                    "Bearer $token",
//                    request
//                )
//
//                if (response.isSuccessful) {
//                    Log.d(TAG, " Status updated successfully")
//                    fetchCustomerList()  // Refresh karke latest status lao
//                } else {
//                    val error = response.errorBody()?.string()
//                    Log.e(TAG, " Status update failed: $error")
//                    _customerListState.value =
//                        CustomerListState.Error("Failed to update device status")
//                }
//
//            } catch (e: Exception) {
//                Log.e(TAG, " Exception", e)
//                _customerListState.value =
//                    CustomerListState.Error(e.localizedMessage ?: "Something went wrong")
//            }
//        }
//    }
//
//
//    fun removeDevice(imei1: String) {
//        viewModelScope.launch {
//            //  Get device details BEFORE deleting
//            val currentState = _customerListState.value
//            val deviceToDelete = if (currentState is CustomerListState.Success) {
//                currentState.data.devices.find { it.imei1 == imei1 }
//            } else null
//
//            _customerListState.value = CustomerListState.Loading
//            Log.d(TAG, "🗑️ Removing device IMEI1=$imei1")
//
//            try {
//                val token = shareprefManager.getToken()
//
//                if (token.isNullOrEmpty()) {
//                    _customerListState.value =
//                        CustomerListState.Error("Authentication token missing")
//                    return@launch
//                }
//
//                val response = RetrofitClient.instance.removeDevice(
//                    token = "Bearer $token",
//                    removeRequest = RemoveRequest(imei1)
//                )
//
//                if (response.isSuccessful) {
//                    Log.d(TAG, "✅ Device removed successfully")
//
//                    // ✅ Add to History AFTER successful deletion
//                    deviceToDelete?.let { device ->
//                        val currentDate = java.text.SimpleDateFormat(
//                            "dd/MM/yyyy",
//                            java.util.Locale.getDefault()
//                        ).format(java.util.Date())
//
//                        val history = com.d_shield_parent.Dashboard.History(
//                            name = device.customer_name,
//                            number = device.customer_phone,
//                            date = currentDate,
//                            imei1 = device.imei1,
//                            action = "Unlocked & Deleted"
//                        )
//
//                        com.d_shield_parent.Dashboard.HistoryRepository.addHistory(history)
//                        Log.d(TAG, "✅ Added to history: ${device.customer_name}")
//                    }
//
//                    fetchCustomerList()
//                } else {
//                    val error = response.errorBody()?.string()
//                    Log.e(TAG, "❌ Remove failed: $error")
//                    _customerListState.value =
//                        CustomerListState.Error("Failed to remove device")
//                }
//
//            } catch (e: Exception) {
//                Log.e(TAG, "❌ Exception", e)
//                _customerListState.value =
//                    CustomerListState.Error(e.localizedMessage ?: "Something went wrong")
//            }
//        }
//    }
//
//
//    fun refreshList() {
//        fetchCustomerList()
//    }
//}