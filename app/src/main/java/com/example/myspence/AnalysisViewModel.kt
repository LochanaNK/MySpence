package com.example.myspence

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class AnalysisViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()

    val expensesByCategory = MediatorLiveData<List<AnalysisData>>()

    private val rawData = transactionDao.getExpensesByCategory()

    init {
        expensesByCategory.addSource(rawData) { rawList ->
            val totalSpent = rawList.sumOf { it.total }

            val processedList = rawList.map { item ->
                AnalysisData(
                    category = item.category,
                    total = item.total,
                    percentage = if (totalSpent > 0) ((item.total / totalSpent) * 100).toInt() else 0
                )
            }

            expensesByCategory.value = processedList
        }
    }


}