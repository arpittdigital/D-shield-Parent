package com.d_shield_parent.Dashboard.viewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d_shield_parent.otp.OtpRepository
//import com.d_shield_parent.Api.RetrofitClient.api
import kotlinx.coroutines.launch

class AddCustomerViewModel : ViewModel() {

    private val repository = OtpRepository()

    var mobile by mutableStateOf("")
    var otp by mutableStateOf("")
    var isOtpSent by mutableStateOf(false)
    var isOtpVerified by mutableStateOf(false)
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun sendOtp() {
        if (mobile.length != 10) {
            errorMessage = "Enter a valid 10-digit mobile number"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            repository.sendOtp(mobile).fold(
                onSuccess = { isOtpSent = true },
                onFailure = { errorMessage = it.message }
            )

            isLoading = false
        }
    }

    fun verifyOtp() {
        if (otp.length != 6) {
            errorMessage = "Enter a valid 6-digit OTP"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null

            repository.verifyOtp(mobile, otp).fold(
                onSuccess = { isOtpVerified = true },
                onFailure = { errorMessage = it.message }
            )

            isLoading = false
        }
    }
}