package com.example.myspence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budget_table")
data class BudgetData(
    @PrimaryKey val month: String,
    val amount: Double
)
