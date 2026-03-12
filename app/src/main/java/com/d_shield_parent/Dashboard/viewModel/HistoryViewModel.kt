package com.d_shield_parent.Dashboard.viewModel

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.d_shield_parent.Api.RetrofitClient
import com.d_shield_parent.Dashboard.model.historyResponse
import com.d_shield_parent.SharedPreference.shareprefManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


sealed class HistoryListState {
    object Idle : HistoryListState()
    object Loading : HistoryListState()
    data class Success(val data: historyResponse) : HistoryListState()
    data class Error(val message: String) : HistoryListState()
}
class HistoryViewModel(application: Application): AndroidViewModel(application) {
    private val _historyListState =
        MutableStateFlow<HistoryListState>(HistoryListState.Idle)
    val historyListState: StateFlow<HistoryListState> =
        _historyListState.asStateFlow()


    fun fetchHistoryList() {
        viewModelScope.launch {

            _historyListState.value = historyListState.value
            Log.d(TAG, "📡 Fetching customer list")

            try {
                val token = shareprefManager.getToken()

                if (token.isNullOrEmpty()) {
                    _historyListState.value =
                        HistoryListState.Error("Authentication token missing")
                    return@launch
                }

                val authHeader = "Bearer $token"
                val response = RetrofitClient.instance.deleteHistory(authHeader)

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    Log.d(TAG, "✅ Devices fetched: ${data.devices.size}")
                    _historyListState.value = HistoryListState.Success(data)
                } else {
                    val error = response.errorBody()?.string()
                    Log.e(TAG, "❌ Fetch failed: $error")
                    _historyListState.value =
                        HistoryListState.Error("Server error ${response.code()}")
                }

            } catch (e: Exception) {
                Log.e(TAG, "❌ Exception", e)
                _historyListState.value =
                    HistoryListState.Error(e.localizedMessage ?: "Something went wrong")
            }
        }
        }
    }




