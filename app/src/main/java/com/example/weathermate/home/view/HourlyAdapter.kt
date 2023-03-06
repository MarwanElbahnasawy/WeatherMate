package com.example.weathermate.home.view

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weathermate.databinding.ItemHourlyForecastBinding
import com.example.weathermate.model.Hourly
import com.example.weathermate.util.Constants
import com.example.weathermate.util.UnitsConverter
import java.text.SimpleDateFormat
import java.util.*

class HourlyAdapter() : ListAdapter<Hourly, HourlyAdapter.HourlyViewHolder>(HourlyDiffUtil()) {

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

    private var context: Context? = null
    lateinit var binding: ItemHourlyForecastBinding
    lateinit var sharedPreferences: SharedPreferences




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyViewHolder {
        context = parent.context
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemHourlyForecastBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val current = getItem(position)

        val date = Date(current.dt * 1000L)
        val format = SimpleDateFormat("HH:mm", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("GMT+2")
        holder.binding.hourTextView.text = format.format(date).toString()

        Glide.with(context!!)
            .load(Constants.weatherConditionImageBaseURL + current.weather[0].icon + "@2x.png")
            .into(holder.binding.hourIconImageView)

        var tempInKelvinByDefault = current.temp
        if (sharedPreferences.getString("temperature_unit", null).equals("celsius")) {
            tempInKelvinByDefault = UnitsConverter.kelvinToCelsius(tempInKelvinByDefault)
            holder.binding.hourTemperatureTextView.text =
                tempInKelvinByDefault.toInt().toString() + " C"
        } else if (sharedPreferences.getString("temperature_unit", null).equals("fahrenheit")) {
            tempInKelvinByDefault = UnitsConverter.kelvinToFahrenheit(tempInKelvinByDefault)
            holder.binding.hourTemperatureTextView.text =
                tempInKelvinByDefault.toInt().toString() + " F"
        } else {
            holder.binding.hourTemperatureTextView.text =
                tempInKelvinByDefault.toInt().toString() + " K"
        }


    }



}