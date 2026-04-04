package com.d_shield_parent.Dashboard

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d_shield_parent.Api.RetrofitClient
import com.d_shield_parent.SharedPreference.shareprefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileData(
    val username: String = "",
    val phoneNo: String = "",
    val uniqueId: String = "",
    val shop_name: String = "",
    val email: String = "",
    val store: String = "",
    val address: String = "",
    val profileImageUri: Uri? = null,
    val walletBalance: String = "0",
    val points: Int = 0,
    val enrolledDevices: Int = 0,
    val activeDevices: Int = 0,
    val lockedDevices: Int = 0
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "ProfileViewModel"

    private val _profileData = MutableStateFlow(ProfileData())
    val profileData: StateFlow<ProfileData> = _profileData.asStateFlow()

    private val _isEditable = MutableStateFlow(false)

    private val _isLoggingOut = MutableStateFlow(false)
    val isLoggingOut: StateFlow<Boolean> = _isLoggingOut.asStateFlow()

    private val _logoutSuccess = MutableStateFlow(false)
    val logoutSuccess: StateFlow<Boolean> = _logoutSuccess.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    // In ProfileViewModel.kt

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            fetchProfile()
            _isRefreshing.value = false
        }
    }

    private val context = getApplication<Application>().applicationContext

    // Init block - Fetch profile on creation
    init {
        fetchProfile()
    }

    fun updatePhoneNo(phone: String) {
        _profileData.value = _profileData.value.copy(phoneNo = phone)
    }
    fun updateShopName(shopName: String) {
        _profileData.value = _profileData.value.copy(shop_name = shopName)
    }
    fun enableEdit() {
        _isEditable.value = true
    }


    fun updateUniqueId(id: String) {
        _profileData.value = _profileData.value.copy(uniqueId = id)
    }

    fun updateEmail(email: String) {
        _profileData.value = _profileData.value.copy(email = email)
    }

    fun updateStore(store: String) {
        _profileData.value = _profileData.value.copy(store = store)
    }

    fun updateAddress(address: String) {
        _profileData.value = _profileData.value.copy(address = address)
    }

    fun updateProfileImage(uri: Uri?) {
        _profileData.value = _profileData.value.copy(profileImageUri = uri)
    }

    fun setEditable(editable: Boolean) {
        _isEditable.value = editable
    }

    fun saveProfile() {
        _isEditable.value = false
        // TODO: API call to update profile data
        Log.d(TAG, "Profile saved locally")
    }



    // Show logout confirmation dialog
    fun showLogoutConfirmation() {
        _showLogoutDialog.value = true
    }

    // Hide logout dialog
    fun hideLogoutDialog() {
        _showLogoutDialog.value = false
    }

    // Fetch Profile from API with proper error handling

    fun fetchProfile() {
        Log.d(TAG, "fetchProfile called, points from prefs: ${shareprefManager.getPoints()}")
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                Log.d(TAG, "========== FETCHING PROFILE ==========")

                val token = shareprefManager.getToken()

                if (token.isNullOrEmpty()) {
                    Log.e(TAG, " No token found")
                    _errorMessage.value = "Authentication token missing. Please login again."
                    _isLoading.value = false
                    return@launch
                }

                val authHeader = "Bearer $token"
                Log.d(TAG, " Token: ${authHeader.take(50)}...")

                val response = RetrofitClient.instance.ProfileRetailer(authHeader)

                Log.d(TAG, "RAW BODY: ${response.errorBody()?.string() ?: response.body().toString()}")

                Log.d(TAG, " Response Code: ${response.code()}")
                Log.d(TAG, " Response Body: ${response.body()}")

                if (response.isSuccessful) {
                    val profileResponse = response.body()

                    if (profileResponse != null && profileResponse.success) {
                        val data = profileResponse.data

                        Log.d(TAG, " data.points: ${data?.points}")
                        Log.d(TAG, "data.wallet_balance: ${data?.wallet_balance}")
                        Log.d(TAG, "full data: $data")

                        val retailer = data?.retailer

                        if (retailer != null) {

                            Log.d(TAG, " retailer.points: ${retailer.points}")
                            Log.d(TAG, " data.points: ${data.points}")
                            Log.d(TAG, " FULL RAW DATA: $data")
                            Log.d(TAG, " Points from prefs: ${shareprefManager.getPoints()}")
                            _profileData.value = ProfileData(
                                username = retailer.name ?: "",
                                phoneNo = retailer.phone ?: "",
                                uniqueId = retailer.id?.toString() ?: "",
                                email = retailer.email ?: "",
                                shop_name = retailer.shop_name?:"",
                                store = retailer.distributor_id ?: "",
                                address = retailer.address ?: "",
                                profileImageUri = null,

//                                walletBalance   = shareprefManager.getPoints().toString(),
//                                walletBalance = "5",
                                walletBalance   = (retailer.points ?: data.points ?: 0).toString(), //  from retailer
                                points          = retailer.points ?: data.points ?: 0,              //  from retailer
                                enrolledDevices = data.enrolled_devices ?: 0,
                                activeDevices = data.active_devices ?: 0,
                                lockedDevices = data.locked_devices ?: 0
                            )

                            Log.d(TAG, "Profile loaded successfully")
                            Log.d(TAG, "Username: ${retailer.name}")
                            Log.d(TAG,  "Phone: ${retailer.phone}")

                        } else {
                            Log.e(TAG, " Retailer data is NULL")
                            _errorMessage.value = "Profile data not available"
                        }

                    } else {
                        Log.e(TAG, " API Response invalid or success=false")
                        _errorMessage.value = profileResponse?.toString() ?: "Invalid response from server"
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, " API Failed: ${response.code()}")
                    Log.e(TAG, " Error Body: $errorBody")

                    _errorMessage.value = when (response.code()) {
                        401 -> "Session expired. Please login again."
                        404 -> "Profile not found"
                        500 -> "Server error. Please try again later."
                        else -> "Failed to load profile (${response.code()})"
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, " Exception while fetching profile", e)
//                _errorMessage.value = "Network error: ${e.message}"
            } finally {
                _isLoading.value = false
                Log.d(TAG, "========== PROFILE FETCH COMPLETED ==========\n")
            }
        }
    }

    //Logout with API call
    fun logout() {
        viewModelScope.launch {
            _isLoggingOut.value = true
            _showLogoutDialog.value = false

            try {
                Log.d(TAG, " Starting logout process...")
                val token = shareprefManager.getToken()

                if (token.isNullOrEmpty()) {
                    Log.w(TAG, "⚠ No token found, clearing local data only")
                    clearUserData()
                    _logoutSuccess.value = true
                    return@launch
                }

                val authHeader = "Bearer $token"


                val response = RetrofitClient.instance.LogoutRetailer(authHeader)

                if (response.isSuccessful) {
                    val body = response.body()
                    Log.d(TAG, " Logout API Success: ${body?.message}")
                    clearUserData()
                    _logoutSuccess.value = true

                } else {
                    val errorBody = response.errorBody()?.string()
                    clearUserData()
                    _logoutSuccess.value = true
                }

            } catch (e: Exception) {
                Log.e(TAG, " Logout Exception: ${e.message}", e)
                // Clear local data even on exception
                clearUserData()
                _logoutSuccess.value = true

            } finally {
                _isLoggingOut.value = false
            }
        }
    }

    private fun clearUserData() {
        shareprefManager.logout()
        Log.d(TAG, "🗑️ User data cleared from SharedPreferences")
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }
}