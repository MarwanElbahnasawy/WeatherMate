package com.example.weathermate.initial_preferences

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.R

class SearchSuggestionAdapter(
    private val suggestions: MutableList<String>,
    private val onItemClickPersonal: OnItemClickPersonal
) : RecyclerView.Adapter<SearchSuggestionAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val suggestionTextView: TextView = itemView.findViewById(R.id.tv_search_suggestion)
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.search_suggestion_item, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val suggestion = suggestions[position]
        holder.suggestionTextView.text = suggestion
        holder.itemView.setOnClickListener {
            onItemClickPersonal.onItemClickPersonal(suggestion)
            suggestions.clear()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return suggestions.size
    }

    fun clearRecyclerView(){
        suggestions.clear()
        notifyDataSetChanged()
    }
}