package com.example.myspence

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: BudgetData)

    @Query("SELECT * FROM budget_table WHERE month = :month LIMIT 1")
    fun getBudgetForMonth(month:String): LiveData<BudgetData>

}