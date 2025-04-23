package com.example.myspence

import BudgetViewModel
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.progressindicator.CircularProgressIndicator

class BudgetFragment : Fragment() {

    private val budgetViewModel: BudgetViewModel by activityViewModels()

    private lateinit var editTextBudget: EditText
    private lateinit var btnSetBudget: Button
    private lateinit var textCurrentBudget: TextView
    private lateinit var textTotalExpenses: TextView
    private lateinit var progressBar: CircularProgressIndicator

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_budget, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editTextBudget = view.findViewById(R.id.editTextBudget)
        btnSetBudget = view.findViewById(R.id.btnSetBudget)
        textCurrentBudget = view.findViewById(R.id.textCurrentBudget)
        textTotalExpenses = view.findViewById(R.id.textTotalExpenses)
        progressBar = view.findViewById(R.id.progressBar)

        // Set budget button click listener
        btnSetBudget.setOnClickListener {
            val input = editTextBudget.text.toString()
            if (input.isNotBlank()) {
                val amount = input.toDoubleOrNull()
                if (amount != null && amount > 0) {
                    budgetViewModel.setBudgetForCurrentMonth(amount)
                    Toast.makeText(requireContext(), "✅ Budget set", Toast.LENGTH_SHORT).show()
                    editTextBudget.text.clear()
                } else {
                    Toast.makeText(requireContext(), "⚠️ Enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "⚠️ Budget field is empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Observe budget and expenses
        budgetViewModel.currentBudget.observe(viewLifecycleOwner) { budget ->
            budget?.let {
                textCurrentBudget.text = "Current Budget: Rs.${"%.2f".format(it.amount)}"

                budgetViewModel.monthlyExpenses.observe(viewLifecycleOwner) { totalSpent ->
                    val spent = totalSpent ?: 0.0
                    textTotalExpenses.text = "Total Spent: RS.${"%.2f".format(spent)}"

                    val percent = (spent / it.amount) * 100
                    val warningText = view.findViewById<TextView>(R.id.textBudgetWarning)

                    when {
                        percent >= 100 -> {
                            warningText.text = "🚨 Budget exceeded!"
                            warningText.visibility = View.VISIBLE
                        }
                        percent >= 80 -> {
                            warningText.text = "⚠️ Almost at your budget limit!"
                            warningText.visibility = View.VISIBLE
                        }
                        else -> {
                            warningText.visibility = View.GONE
                        }
                    }

                    // ProgressBar update
                    progressBar.progress = percent.toInt().coerceAtMost(100)

                    progressBar.setIndicatorColor(
                        when {
                            percent >= 100 -> Color.RED
                            percent >= 80 -> Color.YELLOW
                            else -> Color.GREEN
                        }
                    )
                }
            }
        }
    }
}
