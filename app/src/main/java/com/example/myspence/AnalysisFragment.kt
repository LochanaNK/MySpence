package com.example.myspence

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

class AnalysisFragment : Fragment() {

    private val analysisViewModel: AnalysisViewModel by viewModels()
    private lateinit var analysisAdapter: AnalysisAdapter
    private lateinit var pieChart: PieChart

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_analysis, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerViewAnalysis)
        pieChart = view.findViewById(R.id.pieChart)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        analysisAdapter = AnalysisAdapter(emptyList())
        recyclerView.adapter = analysisAdapter

        analysisViewModel.expensesByCategory.observe(viewLifecycleOwner) { analysisData ->
            // Update the RecyclerView
            analysisAdapter.updateData(analysisData)

            // Update the PieChart
            val entries = analysisData.map {
                PieEntry(it.total.toFloat(), it.category)
            }

            val dataSet = PieDataSet(entries, "Expense Categories")
            dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
            dataSet.valueTextColor = Color.WHITE
            dataSet.valueTextSize = 16f

            val pieData = PieData(dataSet)
            pieChart.data = pieData
            pieChart.description.isEnabled = false
            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.setEntryLabelTextSize(15f)
            pieChart.legend.isEnabled = false
            pieChart.invalidate()
        }
    }
}
