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
import com.example.weathermate.databinding.ItemDailyForecastBinding
import com.example.weathermate.model.Daily
import com.example.weathermate.util.Constants
import com.example.weathermate.util.UnitsConverter
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter ()
    : ListAdapter<Daily, DailyAdapter.DailyViewHolder>(DailyDiffUtil()){


    class DailyViewHolder(var binding: ItemDailyForecastBinding) : RecyclerView.ViewHolder(binding.root) {}

class DailyDiffUtil : DiffUtil.ItemCallback<Daily>() {
    override fun areItemsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem.dt == newItem.dt
    }

    override fun areContentsTheSame(oldItem: Daily, newItem: Daily): Boolean {
        return oldItem == newItem
    }

}

    private var context : Context? = null
    lateinit var binding: ItemDailyForecastBinding
    lateinit var sharedPreferences : SharedPreferences

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyViewHolder {
        context = parent.context
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context!!)
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDailyForecastBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val current = getItem(position)

        val date = Date(current.dt * 1000L)
        val format = SimpleDateFormat("EEEE", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone("GMT+2")
        holder.binding.dayNameTextView.text = format.format(date).toString()

        Glide.with(context!!)
            .load(Constants.weatherConditionImageBaseURL + current.weather[0].icon + "@2x.png")
            .into(holder.binding.dayStatusImageView)

        var tempInKelvinByDefaultMin = current.temp.min
        var tempInKelvinByDefaultMax = current.temp.max
        if (sharedPreferences.getString("temperature_unit",null).equals("celsius")){
            tempInKelvinByDefaultMin = UnitsConverter.kelvinToCelsius(tempInKelvinByDefaultMin)
            tempInKelvinByDefaultMax = UnitsConverter.kelvinToCelsius(tempInKelvinByDefaultMax)
            holder.binding.dayLowestTemperatureTextView.text = tempInKelvinByDefaultMin.toInt().toString() + " C"
            holder.binding.dayHighestTemperatureTextView.text = tempInKelvinByDefaultMax.toInt().toString() + " C"
        }
        else if (sharedPreferences.getString("temperature_unit",null).equals("fahrenheit")) {
            tempInKelvinByDefaultMin = UnitsConverter.kelvinToFahrenheit(tempInKelvinByDefaultMin)
            tempInKelvinByDefaultMax = UnitsConverter.kelvinToFahrenheit(tempInKelvinByDefaultMax)
            holder.binding.dayLowestTemperatureTextView.text = tempInKelvinByDefaultMin.toInt().toString() + " F"
            holder.binding.dayHighestTemperatureTextView.text = tempInKelvinByDefaultMax.toInt().toString() + " F"
        } else{
            holder.binding.dayLowestTemperatureTextView.text = tempInKelvinByDefaultMin.toInt().toString() + " K"
            holder.binding.dayHighestTemperatureTextView.text = tempInKelvinByDefaultMax.toInt().toString() + " K"
        }

    }

}