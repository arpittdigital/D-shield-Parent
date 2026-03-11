package com.d_shield_parent.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

data class EMICalculation(
    val monthlyEMI: Double = 0.0,
    val totalAmount: Double = 0.0,
    val totalInterest: Double = 0.0,
    val emiDates: List<String> = emptyList()
)

class ProductViewModel : ViewModel() {
    private val _emiCalculation = MutableStateFlow(EMICalculation())
    val emiCalculation: StateFlow<EMICalculation> = _emiCalculation

    fun calculateEMI(
        loanAmount: String,
        downPayment: String,
        rateOfInterest: String,
        loanFrequency: String,
        totalInstallments: String,
        agreementDate: String
    ) {
        viewModelScope.launch {
            try {
                val principal = (loanAmount.toDoubleOrNull() ?: 0.0) - (downPayment.toDoubleOrNull() ?: 0.0)
                val rate = (rateOfInterest.toDoubleOrNull() ?: 0.0) / 100
                val tenure = totalInstallments.toIntOrNull() ?: 0

                if (principal <= 0 || rate <= 0 || tenure <= 0) {
                    _emiCalculation.value = EMICalculation()
                    return@launch
                }

                // Calculate EMI based on frequency
                val emi = when (loanFrequency.lowercase()) {
                    "monthly" -> calculateMonthlyEMI(principal, rate, tenure)
                    "weekly" -> calculateWeeklyEMI(principal, rate, tenure)
                    "daily" -> calculateDailyEMI(principal, rate, tenure)
                    else -> calculateMonthlyEMI(principal, rate, tenure)
                }

                val totalAmount = emi * tenure
                val totalInterest = totalAmount - principal

                // Generate EMI dates
                val dates = generateEMIDates(agreementDate, tenure, loanFrequency)

                _emiCalculation.value = EMICalculation(
                    monthlyEMI = emi,
                    totalAmount = totalAmount,
                    totalInterest = totalInterest,
                    emiDates = dates
                )
            } catch (e: Exception) {
                _emiCalculation.value = EMICalculation()
            }
        }
    }

    private fun calculateMonthlyEMI(principal: Double, annualRate: Double, months: Int): Double {
        val monthlyRate = annualRate / 12
        if (monthlyRate == 0.0) return principal / months

        return principal * monthlyRate * (1 + monthlyRate).pow(months) /
                ((1 + monthlyRate).pow(months) - 1)
    }

    private fun calculateWeeklyEMI(principal: Double, annualRate: Double, weeks: Int): Double {
        val weeklyRate = annualRate / 52
        if (weeklyRate == 0.0) return principal / weeks

        return principal * weeklyRate * (1 + weeklyRate).pow(weeks) /
                ((1 + weeklyRate).pow(weeks) - 1)
    }

    private fun calculateDailyEMI(principal: Double, annualRate: Double, days: Int): Double {
        val dailyRate = annualRate / 365
        if (dailyRate == 0.0) return principal / days

        return principal * dailyRate * (1 + dailyRate).pow(days) /
                ((1 + dailyRate).pow(days) - 1)
    }

    private fun generateEMIDates(startDate: String, installments: Int, frequency: String): List<String> {
        val dates = mutableListOf<String>()
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        try {
            val calendar = Calendar.getInstance()
            calendar.time = dateFormat.parse(startDate) ?: Date()

            for (i in 0 until installments) {
                dates.add(dateFormat.format(calendar.time))

                when (frequency.lowercase()) {
                    "monthly" -> calendar.add(Calendar.MONTH, 1)
                    "weekly" -> calendar.add(Calendar.WEEK_OF_YEAR, 1)
                    "daily" -> calendar.add(Calendar.DAY_OF_MONTH, 1)
                    else -> calendar.add(Calendar.MONTH, 1)
                }
            }
        } catch (e: Exception) {
            // If date parsing fails, return empty list
            return emptyList()
        }

        return dates
    }

    fun clearCalculation() {
        _emiCalculation.value = EMICalculation()
    }
}