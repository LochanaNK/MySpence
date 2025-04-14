package com.example.myspence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels



class AddTransactionFragment : DialogFragment() {
    interface OnTransactionAddedListener {
        fun onTransactionAdded()

    }
    var transactionAddedListener: OnTransactionAddedListener? = null
    // ✅ Use shared ViewModel from the Activity
    private val viewModel: TransactionViewModel by activityViewModels()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_add_transaction, container, false)


        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val titleInput = view.findViewById<EditText>(R.id.titleInput)
        val amountInput = view.findViewById<EditText>(R.id.amountInput)
        val spinner: Spinner = view.findViewById(R.id.categorySpinner)

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
                date = System.currentTimeMillis()
            )

            // ✅ Insert using ViewModel
            viewModel.addTransaction(transaction)
            transactionAddedListener?.onTransactionAdded()
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
