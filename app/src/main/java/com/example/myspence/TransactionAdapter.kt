package com.example.myspence

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var transactions: List<Transaction>,
    private val onDeleteClick: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.titleTextView)
        val categoryTextView: TextView = view.findViewById(R.id.categoryTextView)
        val amountTextView: TextView = view.findViewById(R.id.amountTextView)
        val deleteButton: ImageButton = view.findViewById(R.id.deleteTransactionButton) // Actually delete btn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.titleTextView.text = transaction.title
        holder.categoryTextView.text = transaction.category
        holder.amountTextView.text = "Rs.%.2f".format(transaction.amount)

        holder.deleteButton.setOnClickListener {
            AlertDialog.Builder(holder.itemView.context)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete \"${transaction.title}\"?")
                .setPositiveButton("Delete") { _, _ ->
                    onDeleteClick(transaction)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    override fun getItemCount(): Int = transactions.size

    fun updateList(newList: List<Transaction>) {
        transactions = newList
        notifyDataSetChanged()
    }
}
