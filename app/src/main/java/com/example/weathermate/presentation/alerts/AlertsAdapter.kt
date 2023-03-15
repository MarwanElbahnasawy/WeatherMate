package com.example.weathermate.presentation.alerts
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.weathermate.R
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.databinding.ItemAlarmsBinding
import com.example.weathermate.databinding.ItemNotificationsBinding
import kotlinx.coroutines.CoroutineScope

class AlertsAdapter(
    private val lifeCycleScopeInput: CoroutineScope, private val interfaceAlerts: InterfaceAlerts
)
    : ListAdapter<AlertItem, RecyclerView.ViewHolder>(AlertDiffUtil()){

    companion object {
        const val NOTIFICATION = 1
        const val ALARM = 2
    }


    inner class NotificationsViewHolder(var binding: ItemNotificationsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(current: AlertItem){
            binding.tvLocation.text = current.address

            binding.imgDeleteAlert.setOnClickListener {
                interfaceAlerts.onItemClickAlerts(current)
            }


            binding.tvStartTime.text = "${binding.root.context.getString(R.string.startsAt)}\n${current.startString}"
            binding.tvEndTime.text = "${binding.root.context.getString(R.string.endsAt)}\n${current.endString}"

            startLottieAnimation(binding.imgNotification , "notification.json")
            startLottieAnimation(binding.imgDeleteAlert , "delete.json")
        }
    }

    inner class AlarmsViewHolder(var binding: ItemAlarmsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(current: AlertItem){
            binding.tvLocation.text = current.address

            binding.imgDeleteAlert.setOnClickListener {

                interfaceAlerts.onItemClickAlerts(current)


            }

            binding.tvStartTime.text = "${binding.root.context.getString(R.string.startsAt)}\n${current.startString}"
            binding.tvEndTime.text = "${binding.root.context.getString(R.string.endsAt)}\n${current.endString}"

            startLottieAnimation(binding.imgNotification , "alarm.json")
            startLottieAnimation(binding.imgDeleteAlert , "delete.json")
        }
    }

class AlertDiffUtil : DiffUtil.ItemCallback<AlertItem>() {
    override fun areItemsTheSame(oldItem: AlertItem, newItem: AlertItem): Boolean {
        return (oldItem.idHashLongFromLonLatStartStringEndStringAlertType == newItem.idHashLongFromLonLatStartStringEndStringAlertType)
    }

    override fun areContentsTheSame(oldItem: AlertItem, newItem: AlertItem): Boolean {
        return oldItem == newItem
    }

}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when (viewType) {
            NOTIFICATION -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemNotificationsBinding.inflate(inflater, parent, false)
                NotificationsViewHolder(binding)
            }
            else -> {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemAlarmsBinding.inflate(inflater, parent, false)
                AlarmsViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val current = getItem(position)

        when (holder) {
            is NotificationsViewHolder -> holder.bind(current)
            is AlarmsViewHolder -> holder.bind(current)
        }


    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).alertType.equals("notification")) NOTIFICATION else ALARM
    }

    private fun startLottieAnimation(animationView: LottieAnimationView, animationName: String) {
        animationView.setAnimation(animationName)
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.repeatMode = LottieDrawable.RESTART
        animationView.playAnimation()
    }

}