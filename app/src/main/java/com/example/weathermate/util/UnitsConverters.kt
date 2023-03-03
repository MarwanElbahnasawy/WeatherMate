package com.example.weathermate.util



class UnitsConverter {
    companion object {
        fun kelvinToCelsius(temp: Double): Double {
            return temp - 273.15
        }

        fun kelvinToFahrenheit(temp: Double): Double {
            return temp * 9/5 - 459.67
        }

        fun meterPerSecondToKilometerPerHour(speed: Double): Double {
            return speed * 3.6
        }
    }
}