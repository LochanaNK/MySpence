package com.example.myspence

import BudgetViewModel
import android.app.DatePickerDialog
import java.text.SimpleDateFormat
import android.icu.util.*
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import java.util.Date
import java.util.Locale


class AddTransactionFragment : DialogFragment() {
    interface OnTransactionAddedListener {
        fun onTransactionAdded()

    }
    var transactionAddedListener: OnTransactionAddedListener? = null
    // ✅ Use shared ViewModel from the Activity
    private val viewModel: TransactionViewModel by activityViewModels()
    private val budgetViewModel: BudgetViewModel by activityViewModels()


    private var selectedDateInMillis: Long = System.currentTimeMillis()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_add_transaction, container, false)


        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val titleInput = view.findViewById<EditText>(R.id.titleInput)
        val amountInput = view.findViewById<EditText>(R.id.amountInput)
        val spinner: Spinner = view.findViewById(R.id.categorySpinner)
        val dateTextView = view.findViewById<TextView>(R.id.dateTextView)
        updateDateTextView(dateTextView, selectedDateInMillis)

        dateTextView.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDateInMillis

            val datePicker = DatePickerDialog(
                requireContext(),
                {_, year, month, dayOfMonth ->
                    calendar.set(year,month,dayOfMonth)
                    selectedDateInMillis = calendar.timeInMillis
                    updateDateTextView(dateTextView, selectedDateInMillis)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        val categories = listOf("Food 🍽️️", "Transport 🚌", "Shopping 🛍️", "Entertainment 🍿", "Utilities ⚡", "Others")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        saveButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val categoryInput = spinner.selectedItem.toString()
            val amountText = amountInput.text.toString().trim()
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0

            if(title.isEmpty()){
                titleInput.error = "Title is required"
                return@setOnClickListener
            }
            if(amountText.isEmpty()){
                amountInput.error = "Amount is required"
                return@setOnClickListener
            }
            if(amount == null||amount<=0.0){
                amountInput.error = "Invalid amount"
                return@setOnClickListener
            }

            val transaction = Transaction(
                title = title,
                category = categoryInput,
                amount = amount,
                date = selectedDateInMillis
            )

            // ✅ Insert using ViewModel
            viewModel.addTransaction(transaction)
            budgetViewModel.refreshMonth()
            transactionAddedListener?.onTransactionAdded()
            dismiss()
        }

        return view
    }
    private fun updateDateTextView(textView: TextView, millis: Long) {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        textView.text = sdf.format(Date(millis))
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
