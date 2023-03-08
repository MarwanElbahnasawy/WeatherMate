package com.example.weathermate.presentation.alerts
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weathermate.databinding.ItemAlertBinding
import com.example.weathermate.data.model.AlertItem
import kotlinx.coroutines.CoroutineScope

class AlertsAdapter(
    private val lifeCycleScopeInput: CoroutineScope, private val interfaceAlerts: InterfaceAlerts
)
    : ListAdapter<AlertItem, AlertsAdapter.AlertViewHolder>(AlertDiffUtil()){


    class AlertViewHolder(var binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root) {}

class AlertDiffUtil : DiffUtil.ItemCallback<AlertItem>() {
    override fun areItemsTheSame(oldItem: AlertItem, newItem: AlertItem): Boolean {
        return (oldItem.idHashLongFromLonLatStartStringEndStringAlertType == newItem.idHashLongFromLonLatStartStringEndStringAlertType)
    }

    override fun areContentsTheSame(oldItem: AlertItem, newItem: AlertItem): Boolean {
        return oldItem == newItem
    }

}

    lateinit var binding: ItemAlertBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {

        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemAlertBinding.inflate(inflater, parent, false)
        return AlertViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val current = getItem(position)

        holder.binding.tvLocationAlert.text = current.address

        holder.binding.imgDeleteAlert.setOnClickListener {

            interfaceAlerts.onItemClickAlerts(current)


        }

        holder.binding.tvStartTime.text = current.startString

        holder.binding.tvEndTime.text = current.endString

    }

}