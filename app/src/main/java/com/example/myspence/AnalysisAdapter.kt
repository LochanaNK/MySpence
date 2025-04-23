package com.example.myspence

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AnalysisAdapter(
    private var analysisList: List<AnalysisData>
): RecyclerView.Adapter<AnalysisAdapter.AnalysisViewHolder>() {

    class AnalysisViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val categoryText: TextView = itemView.findViewById(R.id.textCategory)
        val percentageText: TextView = itemView.findViewById(R.id.textPercentage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalysisViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category_analysis,parent,false)
        return AnalysisViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnalysisViewHolder, position: Int) {
        val item = analysisList[position]
        holder.categoryText.text = item.category
        holder.percentageText.text = "${item.percentage}%"
    }

    override fun getItemCount(): Int=analysisList.size

    fun updateData(newList: List<AnalysisData>){
        analysisList = newList
        notifyDataSetChanged()
    }
}