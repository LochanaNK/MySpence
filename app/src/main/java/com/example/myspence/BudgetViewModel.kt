import android.app.Application
import androidx.lifecycle.*
import com.example.myspence.AppDatabase
import com.example.myspence.BudgetData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).budgetDao()
    private val transactionDao = AppDatabase.getDatabase(application).transactionDao()

    private val _currentMonth = MutableLiveData(getCurrentMonth())
    val currentMonth: LiveData<String> get() = _currentMonth

    val currentBudget = _currentMonth.switchMap { dao.getBudgetForMonth(it) }

    val expenseCategories = listOf("Food 🍽️️", "Transport 🚌", "Shopping 🛍️", "Entertainment 🍿", "Utilities ⚡", "Others")

    val monthlyExpenses = _currentMonth.switchMap { month ->
        transactionDao.getTotalExpensesForMonth(month, expenseCategories)
    }

    fun setBudgetForCurrentMonth(amount: Double) {
        val month = _currentMonth.value ?: getCurrentMonth()
        viewModelScope.launch(Dispatchers.IO) {
            dao.insertBudget(BudgetData(month, amount))
        }
    }

    fun setMonth(month: String) {
        _currentMonth.value = month
    }

    fun refreshMonth() {
        _currentMonth.value = getCurrentMonth()
    }


    private fun getCurrentMonth(): String {
        val format = SimpleDateFormat("yyyy-MM", Locale.getDefault())
        return format.format(Date())
    }
}
