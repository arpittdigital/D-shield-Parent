package com.d_shield_parent.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d_shield_parent.Api.RetrofitClient
import com.d_shield_parent.SharedPreference.shareprefManager
import com.d_shield_parent.auth.Model.DistributorLoginRequest
import com.d_shield_parent.auth.Model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    private val context = getApplication<Application>().applicationContext

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.OnUserTypeChanged -> {
                _uiState.update { it.copy(selectedUserType = event.userType) }
            }
            is LoginEvent.OnPhoneNumberChanged -> {
                if (event.phoneNumber.length <= 10 && event.phoneNumber.all { it.isDigit() }) {
                    _uiState.update { it.copy(phoneNumber = event.phoneNumber, phoneError = "") }
                }
            }
            is LoginEvent.OnLoginMethodChanged -> {
                _uiState.update {
                    it.copy(
                        loginMethod = event.loginMethod,
                        password = if (event.loginMethod == LoginMethod.MPIN) "" else it.password,
                        mpin = if (event.loginMethod == LoginMethod.PASSWORD) "" else it.mpin,
                        passwordError = if (event.loginMethod == LoginMethod.MPIN) "" else it.passwordError,
                        mpinError = if (event.loginMethod == LoginMethod.PASSWORD) "" else it.mpinError
                    )
                }
            }
            is LoginEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password, passwordError = "") }
            }
            is LoginEvent.OnMpinChanged -> {
                if (event.mpin.length <= 4 && event.mpin.all { it.isDigit() }) {
                    _uiState.update { it.copy(mpin = event.mpin, mpinError = "") }
                }
            }
            is LoginEvent.OnPasswordVisibilityToggled -> {
                _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
            }
            is LoginEvent.OnLoginClicked -> {
                validateAndLogin()
            }
            is LoginEvent.OnSetMpinClicked -> {
                println("Navigate to Set M-PIN screen")
            }
        }
    }

    private fun validateAndLogin() {
        val currentState = _uiState.value
        var isValid = true
        var phoneError = ""
        var passwordError = ""
        var mpinError = ""

        if (currentState.phoneNumber.length != 10) {
            phoneError = "Please enter a valid 10-digit phone number"
            isValid = false
        }

        if (currentState.loginMethod == LoginMethod.MPIN) {
            if (currentState.mpin.length != 4) {
                mpinError = "M-PIN must be exactly 4 digits"
                isValid = false
            }
        } else {
            if (currentState.password.length < 8) {
                passwordError = "Password must be at least 8 characters"
                isValid = false
            }
        }

        _uiState.update {
            it.copy(phoneError = phoneError, passwordError = passwordError, mpinError = mpinError)
        }

        if (isValid) {
            performLogin()
        }
    }

    private fun performLogin() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val currentState = _uiState.value
                val loginType = if (currentState.loginMethod == LoginMethod.MPIN) "mpin" else "password"
                val value = if (currentState.loginMethod == LoginMethod.MPIN) currentState.mpin else currentState.password

                Log.d("LoginViewModel", "▶ UserType: ${currentState.selectedUserType}")
                Log.d("LoginViewModel", "▶ Phone: ${currentState.phoneNumber}")
                Log.d("LoginViewModel", "▶ LoginType: $loginType")

                when (currentState.selectedUserType) {

                    UserType.RETAILER -> {
                        val request = LoginRequest(
                            phone = currentState.phoneNumber,
                            value = value,
                            login_type = loginType
                        )
                        Log.d("LoginViewModel", "▶ Calling Retailer API...")
                        val response = RetrofitClient.instance.LoginRetailer(request)
                        Log.d("LoginViewModel", "▶ Response code: ${response.code()}")
                        val body = response.body()
                        Log.d("LoginViewModel", "▶ Body: $body")

                        if (response.isSuccessful && body?.success == true) {
                            shareprefManager.saveLogin(
                                token = body.token ?: "",
                                role = body.role,
                                userType = "RETAILER",
                                phone = uiState.value.phoneNumber
                            )
                            Log.d("LoginViewModel", "✅ Retailer login success → dashboard_screen")
                            _uiState.update {
                                it.copy(loginSuccess = true, navigateTo = "dashboard_screen")
                            }
                        } else {
                            Log.e("LoginViewModel", "❌ Retailer login failed: ${response.code()} ${response.errorBody()?.string()}")
                            _uiState.update { it.copy(errorMessage = "Login failed. Check your credentials.") }
                        }
                    }

                    UserType.DISTRIBUTOR -> {
                        val request = DistributorLoginRequest(
                            phone_no = currentState.phoneNumber,
                            value = value,
                            login_type = loginType
                        )
                        Log.d("LoginViewModel", "▶ Calling Distributor API...")
                        val response = RetrofitClient.distributorInstance.LoginDistributor(request)
                        Log.d("LoginViewModel", "▶ Response code: ${response.code()}")
                        val body = response.body()
                        Log.d("LoginViewModel", "▶ Body: $body")

                        if (response.isSuccessful && body?.success == true) {
                            shareprefManager.saveLogin(
                                token = body.token ?: "",
                                role = body.role,
                                userType = "DISTRIBUTOR",
                                phone = uiState.value.phoneNumber
                            )
                            Log.d("LoginViewModel", "✅ Distributor login success → distributor_dashboard_screen")
                            _uiState.update {
                                it.copy(loginSuccess = true, navigateTo = "distributor_dashboard_screen")
                            }
                        } else {
                            Log.e("LoginViewModel", "❌ Distributor login failed: ${response.code()} ${response.errorBody()?.string()}")
                            _uiState.update { it.copy(errorMessage = "Login failed. Check your credentials.") }
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("LoginViewModel", "❌ Exception: ${e.message}", e)
                _uiState.update { it.copy(errorMessage = "Network error: ${e.message}") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

