package com.example.myspence

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import kotlinx.coroutines.launch

class AddTransactionFragment : DialogFragment() {

    private lateinit var db: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_add_transaction, container, false)

        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val titleInput = view.findViewById<EditText>(R.id.titleInput)
        val categoryInput = view.findViewById<EditText>(R.id.categoryInput)
        val amountInput = view.findViewById<EditText>(R.id.amountInput)

        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "myspence-db").build()

        saveButton.setOnClickListener {
            val title = titleInput.text.toString()
            val category = categoryInput.text.toString()
            val amount = amountInput.text.toString().toDoubleOrNull() ?: 0.0

            val transaction = Transaction(
                title = title,
                category = category,
                amount = amount,
                date = System.currentTimeMillis()
            )

            lifecycleScope.launch {
                db.transactionDao().insert(transaction)
                dismiss() // This triggers LiveData observer in TransactionFragment
            }
        }

        return view
    }
}
