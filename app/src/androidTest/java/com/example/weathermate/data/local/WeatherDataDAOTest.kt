package com.example.weathermate.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weathermate.data.model.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class WeatherDataDAOTest {

    private lateinit var myDatabase: MyDatabase
    private lateinit var weatherDataDAO: WeatherDataDAO

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        myDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MyDatabase::class.java
        ).allowMainThreadQueries().build()
        weatherDataDAO = myDatabase.getWeatherDataDAO()
    }

    @After
    fun teardown() {
        myDatabase.close()
    }

    @Test
    fun testInsertWeatherData() = runBlockingTest {
        //Given: WeatherData object
        val weatherData = WeatherData(
            current = Current(
                clouds = 1,
                humidity = 2,
                pressure = 3,
                dt = 4,
                temp = 5.5,
                wind_speed = 6.6,
                weather = listOf(
                    Weather(
                        description = "des",
                        icon = "ico",
                        id = 1
                    )
                )
            ),
            daily = listOf(
                Daily(
                dt = 1,
                    temp = Temp(
                        max = 1.1,
                        min = 2.2
                    ) ,
                    weather = listOf(Weather(
                        description = "des",
                        icon = "ico",
                        id = 1
                    ))
            )
            ) ,
            hourly = listOf(
                Hourly(
                dt = 1,
                    weather = listOf(Weather(
                        description = "des",
                        icon = "ico",
                        id = 1
                    )),
                    temp = 1.1
            )
            ),
            lat = 1.1,
            lon = 2.2,
            alerts = null,
            addressName = null
        )

        //When: insert weatherData to database
        weatherDataDAO.insertWeatherData(weatherData)

        val result = weatherDataDAO.getWeatherDataFromDB()

        //then: assert that weatherData is inserted in the database
        assertThat(result).isEqualTo(weatherData)
    }

    @Test
    fun testDeleteWeatherData() = runBlockingTest {

        //Given: WeatherData object
        val weatherData = WeatherData(
            current = Current(
                clouds = 1,
                humidity = 2,
                pressure = 3,
                dt = 4,
                temp = 5.5,
                wind_speed = 6.6,
                weather = listOf(
                    Weather(
                        description = "des",
                        icon = "ico",
                        id = 1
                    )
                )
            ),
            daily = listOf(
                Daily(
                    dt = 1,
                    temp = Temp(
                        max = 1.1,
                        min = 2.2
                    ) ,
                    weather = listOf(Weather(
                        description = "des",
                        icon = "ico",
                        id = 1
                    ))
                )
            ) ,
            hourly = listOf(
                Hourly(
                    dt = 1,
                    weather = listOf(Weather(
                        description = "des",
                        icon = "ico",
                        id = 1
                    )),
                    temp = 1.1
                )
            ),
            lat = 1.1,
            lon = 2.2,
            alerts = null,
            addressName = null
        )

        //When: insert weatherData to database
        weatherDataDAO.insertWeatherData(weatherData)

        //When: delete weatherData to database
        weatherDataDAO.deleteWeatherData(weatherData)

        val firstWeatherData = weatherDataDAO.getWeatherDataFromDB()

        //then: assert that weatherData is not in the database
        assertThat(firstWeatherData).isNotEqualTo(weatherData)
    }
}