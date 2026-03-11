package com.d_shield_parent.Dashboard.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d_shield_parent.Api.RetrofitClient
import com.d_shield_parent.Dashboard.model.MpinRequest
import com.d_shield_parent.Dashboard.model.MpinResponse
import com.d_shield_parent.SharedPreference.shareprefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// ✅ State Class
sealed class MpinState {
    object Idle : MpinState()
    object Loading : MpinState()
    data class Success(val data: MpinResponse) : MpinState()
    data class Error(val message: String) : MpinState()
}

class MpinViewmodel(application: Application) : AndroidViewModel(application) {

    private val _mpinState = MutableStateFlow<MpinState>(MpinState.Idle)
    val mpinState = _mpinState.asStateFlow()

    fun setMpin(mpin: String) {
        viewModelScope.launch {
            _mpinState.value = MpinState.Loading

            try {
                val token = "Bearer ${shareprefManager.getToken()}"
                val request = MpinRequest(mpin = mpin)
                val response = RetrofitClient.instance.SetMpin(token, request)
                if (response.isSuccessful && response.body() != null) {
                    _mpinState.value = MpinState.Success(response.body()!!)
                    Log.d("MpinViewmodel", "Mpin set successfully")
                } else {
                    _mpinState.value = MpinState.Error("Server Error: ${response.code()}")
                }

            } catch (e: Exception) {
                _mpinState.value = MpinState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
    }
}
