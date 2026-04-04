package com.d_shield_parent.Dashboard.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.d_shield_parent.Dashboard.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class TransactionUiState {
    object Loading : TransactionUiState()
    data class Success(val transactions: List<Transaction>) : TransactionUiState()
    data class Error(val message: String) : TransactionUiState()
}

class TransactionViewModel : ViewModel() {

    private val repository = TransactionRepository()

    private val _uiState = MutableStateFlow<TransactionUiState>(TransactionUiState.Loading)
    val uiState: StateFlow<TransactionUiState> = _uiState

    fun fetchHistory(token: String) {
        viewModelScope.launch {
            _uiState.value = TransactionUiState.Loading
            val result = repository.getHistory(token)
            _uiState.value = if (result.isSuccess) {
                val transactions = result.getOrNull()?.transactions ?: emptyList()
                TransactionUiState.Success(transactions)
            } else {
                TransactionUiState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}