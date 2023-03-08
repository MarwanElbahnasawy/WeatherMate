package com.example.weathermate.presentation.home
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weathermate.databinding.ItemDailyForecastBinding
import com.example.weathermate.data.model.Daily
import com.example.weathermate.util.Constants
import com.example.weathermate.util.HelperObject
import com.example.weathermate.util.MyConverters
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter (private val tempUnit: String)
    : ListAdapter<Daily, DailyAdapter.DailyViewHolder>(DailyDiffUtil()){


    class DailyViewHolder(val binding: ItemDailyForecastBinding) : RecyclerView.ViewHolder(binding.root) {}

class DailyDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }

}

    lateinit var binding: ItemDailyForecastBinding
    lateinit var sharedPreferences : SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        sharedPreferences = HelperObject.getSharedPreferencesInstance()
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDailyForecastBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val current = getItem(position)

        val date = Date(current.dt * 1000L)
        val format = SimpleDateFormat("EEEE", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+2")
        }
        holder.binding.dayNameTextView.text = format.format(date)

        Glide.with(holder.binding.root)
            .load(Constants.weatherConditionImageBaseURL + current.weather[0].icon + "@2x.png")
            .into(holder.binding.dayStatusImageView)

        val tempInKelvinByDefaultMin = current.temp.min
        val tempInKelvinByDefaultMax = current.temp.max
        holder.binding.dayLowestTemperatureTextView.text = when (tempUnit) {
            "celsius" -> "${MyConverters.kelvinToCelsius(tempInKelvinByDefaultMin).toInt()} °C"
            "fahrenheit" -> "${MyConverters.kelvinToFahrenheit(tempInKelvinByDefaultMin).toInt()} °F"
            else -> "${tempInKelvinByDefaultMin.toInt()} °K"
        }
        holder.binding.dayHighestTemperatureTextView.text = when (tempUnit) {
            "celsius" -> "${MyConverters.kelvinToCelsius(tempInKelvinByDefaultMax).toInt()} °C"
            "fahrenheit" -> "${MyConverters.kelvinToFahrenheit(tempInKelvinByDefaultMax).toInt()} °F"
            else -> "${tempInKelvinByDefaultMax.toInt()} °K"
        }

    }

}