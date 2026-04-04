package com.d_shield_parent.Dashboard.viewModel
import com.d_shield_parent.Api.RetrofitClient
import com.d_shield_parent.Dashboard.model.TransactionResponse

class TransactionRepository {
    suspend fun getHistory(token: String): Result<TransactionResponse> {
        return try {
            val response = RetrofitClient.instance.getTransactionHistory("Bearer $token")
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}