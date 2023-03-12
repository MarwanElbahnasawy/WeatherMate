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
import com.example.weathermate.databinding.ItemDailyForecastBinding
import com.example.weathermate.data.model.Daily
import com.example.weathermate.util.MyHelper
import com.example.weathermate.util.MyConverters
import java.text.SimpleDateFormat
import java.util.*

class DailyAdapter(
    private val tempUnit: String,
    private val highestLowestTemp: Double,
    private val highestTemp: Double,
    private val lowestTempMin: Double,
    private val lowestTempMax: Double
)
    : ListAdapter<Daily, DailyAdapter.DailyViewHolder>(DailyDiffUtil()){

    private val TAG = "commonnn"

    inner class DailyViewHolder(val binding: ItemDailyForecastBinding) : RecyclerView.ViewHolder(binding.root) {}

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
        sharedPreferences = MyHelper.getSharedPreferencesInstance()
        val inflater : LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemDailyForecastBinding.inflate(inflater, parent, false)
        return DailyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DailyViewHolder, position: Int) {
        val current = getItem(position)

        val date = Date(current.dt * 1000L)
        val format = SimpleDateFormat("EEE", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("GMT+2")
        }
        holder.binding.tvDayName.text = format.format(date)

        Glide.with(holder.binding.root)
            .load(MyHelper.getWeatherImage(current.weather[0].icon))
            .into(holder.binding.imgIcon)

         animateWeatherImage(holder.binding.imgIcon)

        val tempInKelvinByDefaultMin = current.temp.min
        val tempInKelvinByDefaultMax = current.temp.max
        holder.binding.tvDayLow.text = when (tempUnit) {
            "celsius" -> {

                setUpConstraintLayoutLeftWidth(holder.binding.constraintLayoutDailyInsideLeft, current, holder.binding.root.context)

                MyConverters.convertTemperature(tempInKelvinByDefaultMin, holder.binding.root.context)
            }
            "fahrenheit" -> {

                setUpConstraintLayoutLeftWidth(holder.binding.constraintLayoutDailyInsideLeft, current, holder.binding.root.context)

                MyConverters.convertTemperature(tempInKelvinByDefaultMin, holder.binding.root.context)
            }
            else -> {

                setUpConstraintLayoutLeftWidth(holder.binding.constraintLayoutDailyInsideLeft, current, holder.binding.root.context)

                MyConverters.convertTemperature(tempInKelvinByDefaultMin, holder.binding.root.context)
            }
        }
        holder.binding.tvDayHigh.text = when (tempUnit) {
            "celsius" -> {

                setUpConstraintLayoutRightWidth(holder.binding.constraintLayoutDailyInsideRight, current, holder.binding.root.context)

                MyConverters.convertTemperature(tempInKelvinByDefaultMax, holder.binding.root.context)
            }
            "fahrenheit" -> {
                setUpConstraintLayoutRightWidth(holder.binding.constraintLayoutDailyInsideRight, current, holder.binding.root.context)

                MyConverters.convertTemperature(tempInKelvinByDefaultMax, holder.binding.root.context)
            }
            else -> {
                setUpConstraintLayoutRightWidth(holder.binding.constraintLayoutDailyInsideRight, current, holder.binding.root.context)

                MyConverters.convertTemperature(tempInKelvinByDefaultMax, holder.binding.root.context)
            }
        }

        holder.binding.imgIcon.scaleX = 0f
        holder.binding.imgIcon.scaleY = 0f
        holder.binding.imgIcon.animate().scaleX(1f).scaleY(1f).setDuration(750).start()

        if (checkLayout(holder.binding.root.context)){
            val gradientDrawableLR = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    ContextCompat.getColor(holder.binding.root.context, R.color.gradient_start),
                    ContextCompat.getColor(holder.binding.root.context, R.color.gradient_middle)
                )
            )
            val gradientDrawableRL = GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT,
                intArrayOf(
                    ContextCompat.getColor(holder.binding.root.context, R.color.gradient_end),
                    ContextCompat.getColor(holder.binding.root.context, R.color.gradient_middle)
                )
            )

            holder.binding.constraintLayoutDailyInsideLeft.background = gradientDrawableLR
            holder.binding.constraintLayoutDailyInsideRight.background = gradientDrawableRL
        } else{
            val gradientDrawableLR = GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                intArrayOf(
                    ContextCompat.getColor(holder.binding.root.context, R.color.gradient_middle),
                    ContextCompat.getColor(holder.binding.root.context, R.color.gradient_start)
                )
            )
            val gradientDrawableRL = GradientDrawable(
                GradientDrawable.Orientation.RIGHT_LEFT,
                intArrayOf(
                    ContextCompat.getColor(holder.binding.root.context, R.color.gradient_middle),
                    ContextCompat.getColor(holder.binding.root.context, R.color.gradient_end)
                )
            )

            holder.binding.constraintLayoutDailyInsideLeft.background = gradientDrawableLR
            holder.binding.constraintLayoutDailyInsideRight.background = gradientDrawableRL
        }



    }

    private fun setUpConstraintLayoutLeftWidth(constraintLayout: ConstraintLayout, current: Daily, context: Context) {
        val layoutParams = constraintLayout.layoutParams
        val widthInDp = (80 * ((MyConverters.kelvinToCelsius(current.temp.min)-MyConverters.kelvinToCelsius(lowestTempMin))/(MyConverters.kelvinToCelsius(highestLowestTemp)-MyConverters.kelvinToCelsius(lowestTempMin)))).toInt().coerceAtLeast(1)
        val widthInPx = dpToPx(80-widthInDp+10, context)
        layoutParams.width = widthInPx
        constraintLayout.layoutParams = layoutParams
    }

    private fun setUpConstraintLayoutRightWidth(constraintLayout: ConstraintLayout, current: Daily, context: Context) {
        val layoutParams = constraintLayout.layoutParams
        val widthInDp = (80 * ((current.temp.max-lowestTempMax)/(highestTemp-lowestTempMax))).toInt().coerceAtLeast(1)
        val widthInPx = dpToPx(widthInDp+10, context)
        layoutParams.width = widthInPx
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

    private fun checkLayout(context: Context): Boolean {
        val currentLocale = context.resources.configuration.locale
        val languageCode = currentLocale.language
        return languageCode != "ar"
    }


}
