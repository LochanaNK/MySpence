package com.example.myspence

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application.applicationContext)
    private val dao = db.transactionDao()

    val allTransactions: LiveData<List<Transaction>> = dao.getAll()

    private val _filteredTransactions = MediatorLiveData<List<Transaction>>()
    val filteredTransactions: LiveData<List<Transaction>> get() = _filteredTransactions

    init{
        _filteredTransactions.addSource(allTransactions){
            _filteredTransactions.value=it
        }
    }

    fun filterByCategory(category: String) {
        _filteredTransactions.removeSource(allTransactions)

        if(category == "All"){
            _filteredTransactions.addSource(allTransactions){
                _filteredTransactions.value=it
            }
        }else{
            val filteredSource = dao.getByCategory(category)
            _filteredTransactions.addSource(filteredSource){
                _filteredTransactions.value=it
            }
        }
    }

    fun addTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            db.transactionDao().insert(transaction)
        }
    }
    fun getTransactionsByCategory(category: String): LiveData<List<Transaction>> {
        return db.transactionDao().getByCategory(category)
    }
    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch(Dispatchers.IO) {
            db.transactionDao().delete(transaction)
        }
    }
}
