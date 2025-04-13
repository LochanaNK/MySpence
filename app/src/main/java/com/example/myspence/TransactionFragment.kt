package com.example.myspence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.launch

class TransactionFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var db: AppDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.transactionRecyclerView)

        db = Room.databaseBuilder(requireContext(), AppDatabase::class.java, "myspence-db")
            .fallbackToDestructiveMigration()
            .build()

        // Adapter with delete callback
        transactionAdapter = TransactionAdapter(listOf()) { transactionToDelete ->
            lifecycleScope.launch {
                db.transactionDao().delete(transactionToDelete)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = transactionAdapter

        db.transactionDao().getAll().observe(viewLifecycleOwner) { transactions ->
            transactionAdapter.updateList(transactions)
        }

        view.findViewById<ImageButton>(R.id.addTransactionButton).setOnClickListener {
            AddTransactionFragment().show(childFragmentManager, "AddTransactionDialog")
        }
    }
}
