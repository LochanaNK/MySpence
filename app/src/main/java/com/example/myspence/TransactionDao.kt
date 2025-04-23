package com.example.myspence

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAll(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE category = :category")
    fun getByCategory(category: String): LiveData<List<Transaction>>

    @Query("SELECT category, SUM(amount) AS total FROM transactions GROUP BY category")
    fun getExpensesByCategory(): LiveData<List<RawAnalysisData>>



    @Query("SELECT SUM(amount) FROM transactions WHERE strftime('%Y-%m', date / 1000, 'unixepoch') = :month")
    fun getTotalExpensesForMonth(month: String): LiveData<Double>
}
