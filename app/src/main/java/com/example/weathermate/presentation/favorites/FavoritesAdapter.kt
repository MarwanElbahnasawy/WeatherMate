package com.example.weathermate.presentation.favorites
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.example.weathermate.databinding.ItemFavoriteBinding
import com.example.weathermate.data.model.FavoriteAddress
import com.example.weathermate.util.MyHelper
import com.example.weathermate.util.MyConverters
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*

class FavoritesAdapter (private val interfaceFavorites: InterfaceFavorites,
                        private val lifeCycleScopeInput: CoroutineScope
)
    : ListAdapter<FavoriteAddress, FavoritesAdapter.FavoriteViewHolder>(FavoriteDiffUtil()){


    class FavoriteViewHolder(var binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root) {}

class FavoriteDiffUtil : DiffUtil.ItemCallback<FavoriteAddress>() {
    override fun areItemsTheSame(oldItem: FavoriteAddress, newItem: FavoriteAddress): Boolean {
        return oldItem.latlngString == newItem.latlngString
    }

    override fun areContentsTheSame(oldItem: FavoriteAddress, newItem: FavoriteAddress): Boolean {
        return oldItem == newItem
    }

}

    lateinit var binding: ItemFavoriteBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {

        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemFavoriteBinding.inflate(inflater, parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val current = getItem(position)

        holder.binding.tvAddressName.text = current.address

        holder.binding.imgDeleteFavorite.setOnClickListener {
            interfaceFavorites.onDeleteClickFavorites(current)
        }
        holder.binding.tvTemp.text = MyConverters.convertTemperature(current.currentTemp,holder.binding.root.context)

        holder.binding.tvDescription.text = current.currentDescription

       /* formatDateTime(current.lastCheckedTime, holder.binding.tvDate, holder.binding.tvTime)*/
        formatDate(current.lastCheckedTime, holder.binding.tvDate)

        startLottieAnimation(holder.binding.imgWeather, MyHelper.getWeatherLottie(current.icon))

        startLottieAnimation(binding.imgDeleteFavorite , "delete.json")

        holder.binding.root.setOnClickListener {
            interfaceFavorites.onItemClickFavorites(current)
        }

    }

     private fun formatDate(dateTime: Long, tvDate: TextView) {
        val formatDate = SimpleDateFormat("MMM dd, yyyy - EEE hh:mm a", Locale.getDefault())
        formatDate.timeZone = TimeZone.getTimeZone("GMT+2")
        tvDate.text = formatDate.format(dateTime)
    }

    private fun startLottieAnimation(animationView: LottieAnimationView, animationName: String) {
        animationView.setAnimation(animationName)
        animationView.repeatCount = LottieDrawable.INFINITE
        animationView.repeatMode = LottieDrawable.RESTART
        animationView.playAnimation()
    }

}