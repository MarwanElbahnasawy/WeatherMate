package com.example.weathermate.presentation.home

import android.animation.ValueAnimator
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.weathermate.R
import com.example.weathermate.databinding.ItemHourlyForecastBinding
import com.example.weathermate.data.model.Hourly
import com.example.weathermate.util.MyHelper
import com.example.weathermate.util.MyConverters
import java.text.SimpleDateFormat
import java.util.*


class HourlyAdapter(
    private val tempUnit: String,
    private val highestTemp: Double,
    private val lowestTemp: Double
) : ListAdapter<Hourly, HourlyAdapter.HourlyViewHolder>(
    HourlyDiffUtil()
) {

    private val TAG = "commonnn"

    inner class HourlyViewHolder(val binding: ItemHourlyForecastBinding) :
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
        sharedPreferences = MyHelper.getSharedPreferencesInstance()
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        binding = ItemHourlyForecastBinding.inflate(inflater, parent, false)
        return HourlyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HourlyViewHolder, position: Int) {
        val current = getItem(position)

        val date = Date(current.dt * 1000L)
        val format = SimpleDateFormat("h:mm a", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+2")
        }
        holder.binding.tvHourTime.text = format.format(date)

       Glide.with(holder.binding.root)
            .load(MyHelper.getWeatherImage(current.weather[0].icon))
            .into(holder.binding.imgHourIcon)

         animateWeatherImage(holder.binding.imgHourIcon)

        val tempInKelvinByDefault = current.temp
        holder.binding.tvTempHourly.text = when (tempUnit) {
            "celsius" -> {

                setUpConstraintLayoutHeight(holder.binding.constraintLayoutInside, current, holder.binding.root.context)

                MyConverters.convertTemperature(tempInKelvinByDefault, holder.binding.root.context)

            }
            "fahrenheit" -> {

                setUpConstraintLayoutHeight(holder.binding.constraintLayoutInside, current, holder.binding.root.context)

                MyConverters.convertTemperature(tempInKelvinByDefault, holder.binding.root.context)
            }
            else -> {

                setUpConstraintLayoutHeight(holder.binding.constraintLayoutInside, current, holder.binding.root.context)

                MyConverters.convertTemperature(tempInKelvinByDefault, holder.binding.root.context)
            }
        }

        holder.binding.imgHourIcon.scaleX = 0f
        holder.binding.imgHourIcon.scaleY = 0f
        holder.binding.imgHourIcon.animate().scaleX(1f).scaleY(1f).setDuration(750).start()

        val gradientDrawable = GradientDrawable(
            GradientDrawable.Orientation.BOTTOM_TOP,
            intArrayOf(
                ContextCompat.getColor(holder.binding.root.context, R.color.gradient_start),
                ContextCompat.getColor(holder.binding.root.context, R.color.gradient_end)
            )
        )
        holder.binding.constraintLayoutInside.background = gradientDrawable

    }

    private fun setUpConstraintLayoutHeight(
        constraintLayout: ConstraintLayout,
        current: Hourly,
        context: Context
    ) {
        val layoutParams = constraintLayout.layoutParams
        val heightInDp = (150 * ((MyConverters.kelvinToCelsius(current.temp)-MyConverters.kelvinToCelsius(lowestTemp))/(MyConverters.kelvinToCelsius(highestTemp)-MyConverters.kelvinToCelsius(lowestTemp)))).toInt().coerceAtLeast(1)
        val heightInPx = dpToPx(heightInDp+10, context)
        layoutParams.height = heightInPx
        constraintLayout.layoutParams = layoutParams
    }

    fun dpToPx(dp: Int , context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

    private fun animateWeatherImage(imageViewWeatherIcon: ImageView) {
        val randomNumber1 = (0 until 360).random()
        val randomNumber2 = 360-randomNumber1
        val circularAnimator = ValueAnimator.ofFloat(randomNumber1.toFloat(), randomNumber2.toFloat()).apply {
            duration = 3000
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.REVERSE
            interpolator = LinearInterpolator()
        }

        circularAnimator.addUpdateListener {
            val angle = it.animatedValue as Float
            val radius = 5f
            val x = radius * Math.cos(Math.toRadians(angle.toDouble())).toFloat()
            val y = radius * Math.sin(Math.toRadians(angle.toDouble())).toFloat()
            imageViewWeatherIcon.translationX = x
            imageViewWeatherIcon.translationY = y
        }
        circularAnimator.start()
    }

}
