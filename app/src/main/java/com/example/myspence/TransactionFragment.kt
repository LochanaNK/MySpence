package com.example.myspence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TransactionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private val viewModel: TransactionViewModel by activityViewModels()
    private lateinit var categorySpinner: Spinner

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup Spinner
        categorySpinner = view.findViewById(R.id.category_filter)
        val categories = listOf("All", "Food 🍽️️", "Transport 🚌", "Shopping 🛍️", "Entertainment 🍿", "Utilities ⚡", "Others")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = spinnerAdapter

        // RecyclerView + Adapter
        recyclerView = view.findViewById(R.id.transactionRecyclerView)
        transactionAdapter = TransactionAdapter(emptyList()) { transactionToDelete ->
            viewModel.deleteTransaction(transactionToDelete)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = transactionAdapter

        // Handle Category Selection
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                viewModel.filterByCategory(selectedCategory)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                viewModel.filterByCategory("All")
            }
        }

        view.findViewById<ImageButton>(R.id.addTransactionButton).setOnClickListener {
            val dialog = AddTransactionFragment()
            dialog.transactionAddedListener = object : AddTransactionFragment.OnTransactionAddedListener {
                override fun onTransactionAdded() {
                    categorySpinner.setSelection(0) // Reset to "All"
                }
            }
            dialog.show(childFragmentManager, "AddTransactionDialog")
        }

        viewModel.filteredTransactions.observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.updateList(transactions)
        }

    }
}
