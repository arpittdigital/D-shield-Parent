package com.d_shield_parent.Dashboard

import androidx.compose.runtime.mutableStateListOf

//  Singleton Repository for History
object HistoryRepository {
    val historyList = mutableStateListOf<navItem.History>()

    fun addHistory(history: navItem.History) {
        // Add to beginning so latest appears first
        historyList.add(0, history)
    }

    fun clearHistory() {
        historyList.clear()
    }

    fun getHistoryCount(): Int {
        return historyList.size
    }
}