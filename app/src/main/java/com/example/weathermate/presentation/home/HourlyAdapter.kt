package com.example.weathermate.presentation.home

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weathermate.databinding.ItemHourlyForecastBinding
import com.example.weathermate.data.model.Hourly
import com.example.weathermate.util.Constants
import com.example.weathermate.util.HelperObject
import com.example.weathermate.util.MyConverters
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter(private val tempUnit: String) : ListAdapter<Hourly, HourlyAdapter.HourlyViewHolder>(
    HourlyDiffUtil()
) {

    class HourlyViewHolder(val binding: ItemHourlyForecastBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    class HourlyDiffUtil : DiffUtil.ItemCallback<Hourly>() {
        override fun areItemsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem.dt == newItem.dt
        }

        override fun areContentsTheSame(oldItem: Hourly, newItem: Hourly): Boolean {
            return oldItem == newItem
        }

    }

    lateinit var binding: ItemHourlyForecastBinding
    lateinit var sharedPreferences: SharedPreferences


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        sharedPreferences = HelperObject.getSharedPreferencesInstance()
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemHourlyForecastBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val current = getItem(position)

        val date = Date(current.dt * 1000L)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+2")
        }
        holder.binding.hourTextView.text = format.format(date)

        Glide.with(holder.binding.root)
            .load(Constants.weatherConditionImageBaseURL + current.weather[0].icon + "@2x.png")
            .into(holder.binding.hourIconImageView)

        val tempInKelvinByDefault = current.temp
        holder.binding.hourTemperatureTextView.text = when (tempUnit) {
            "celsius" -> "${MyConverters.kelvinToCelsius(tempInKelvinByDefault).toInt()} °C"
            "fahrenheit" -> "${MyConverters.kelvinToFahrenheit(tempInKelvinByDefault).toInt()} °F"
            else -> "${tempInKelvinByDefault.toInt()} °K"
        }


    }



}