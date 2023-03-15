package com.example.weathermate.data

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.data.local.FakeLocalDataSource
import com.example.weathermate.data.model.*
import com.example.weathermate.data.remote.FakeRemoteDataSource
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class RepositoryTest {

    @Test
    fun testGetWeatherDataOnline() = runBlockingTest {
        //Given: WeatherData object
        val fakeWeatherData = WeatherData(
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
                    weather = listOf(
                        Weather(
                        description = "des",
                        icon = "ico",
                        id = 1
                    )
                    )
                )
            ) ,
            hourly = listOf(
                Hourly(
                    dt = 1,
                    weather = listOf(
                        Weather(
                        description = "des",
                        icon = "ico",
                        id = 1
                    )
                    ),
                    temp = 1.1
                )
            ),
            lat = 1.1,
            lon = 2.2,
            alerts = null,
            addressName = null
        )

        val testingContext: Application = ApplicationProvider.getApplicationContext()

        //When: insert weatherData to database
        val fakeRemoteDataSource = FakeRemoteDataSource(weatherData = fakeWeatherData)

        val fakeLocalDataSource = FakeLocalDataSource()

        val repository = Repository(fakeLocalDataSource, fakeRemoteDataSource, PreferenceManager.getDefaultSharedPreferences(testingContext))

        val result = repository.getWeatherDataOnline(1.1, 2.2, "en").single()

        //then: assert that weatherData is inserted in the database
        assertThat(fakeWeatherData).isEqualTo(result)
    }

    @Test
    fun testGetWeatherDataFromDB() = runBlockingTest {
        //Given: WeatherData object
        val fakeWeatherData = WeatherData(
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
                    weather = listOf(
                        Weather(
                            description = "des",
                            icon = "ico",
                            id = 1
                        )
                    )
                )
            ) ,
            hourly = listOf(
                Hourly(
                    dt = 1,
                    weather = listOf(
                        Weather(
                            description = "des",
                            icon = "ico",
                            id = 1
                        )
                    ),
                    temp = 1.1
                )
            ),
            lat = 1.1,
            lon = 2.2,
            alerts = null,
            addressName = null
        )

        val testingContext: Application = ApplicationProvider.getApplicationContext()

        val fakeRemoteDataSource = FakeRemoteDataSource()

        //When: insert weatherData to database
        val fakeLocalDataSource = FakeLocalDataSource(weatherData = fakeWeatherData)

        val repository = Repository(fakeLocalDataSource, fakeRemoteDataSource, PreferenceManager.getDefaultSharedPreferences(testingContext))

        val result = repository.getWeatherDataFromDB()

        //then: assert that weatherData is inserted in the database
        assertThat(result).isEqualTo(fakeWeatherData)
    }

    @Test
    fun testGetAllFavoriteAddresses() = runBlockingTest {
        //Given: fakeAddresses
        val fakeAddresses = listOf(FavoriteAddress(
            "address",
            1.1,
            2.2,
            "1.12.2",
            50.0,
            "weather is nice",
            3,
            "icon"
        ))

        val testingContext: Application = ApplicationProvider.getApplicationContext()

        val fakeRemoteDataSource = FakeRemoteDataSource()

        //When: insert fakeAddresses to database
        val fakeLocalDataSource = FakeLocalDataSource(favoriteAddresses = fakeAddresses.toMutableList())

        val repository = Repository(fakeLocalDataSource, fakeRemoteDataSource, PreferenceManager.getDefaultSharedPreferences(testingContext))

        val result = repository.getAllFavoriteAddresses()

        //then: assert that fakeAddresses is inserted in the database
        assertThat(result).isEqualTo(fakeAddresses)
    }

    @Test
    fun testInsertFavoriteAddress() = runBlockingTest {
        //Given: fakeAddresses
        val fakeAddresses = FavoriteAddress(
            "address",
            1.1,
            2.2,
            "1.12.2",
            50.0,
            "weather is nice",
            3,
            "icon"
        )

        val testingContext: Application = ApplicationProvider.getApplicationContext()

        val fakeRemoteDataSource = FakeRemoteDataSource()

        val fakeLocalDataSource = FakeLocalDataSource()

        val repository = Repository(fakeLocalDataSource, fakeRemoteDataSource, PreferenceManager.getDefaultSharedPreferences(testingContext))

        //When: insert fakeAddresses to database
        repository.insertFavoriteAddress(fakeAddresses)

        //then: assert that fakeAddresses is inserted in the database
        assertThat(repository.getAllFavoriteAddresses()).contains(fakeAddresses)
    }

    @Test
    fun deleteFavoriteAddress() = runBlockingTest {
        //Given: fakeAddresses
        val fakeAddresses = FavoriteAddress(
            "address",
            1.1,
            2.2,
            "1.12.2",
            50.0,
            "weather is nice",
            3,
            "icon"
        )

        val testingContext: Application = ApplicationProvider.getApplicationContext()

        val fakeRemoteDataSource = FakeRemoteDataSource()

        val fakeLocalDataSource = FakeLocalDataSource()

        val repository = Repository(fakeLocalDataSource, fakeRemoteDataSource, PreferenceManager.getDefaultSharedPreferences(testingContext))

        //When: insert fakeAddresses to database
        repository.insertFavoriteAddress(fakeAddresses)

        //When: delete fakeAddresses to database
        repository.deleteFavoriteAddress(fakeAddresses)

        //then: assert that fakeAddresses is inserted in the database
        assertThat(repository.getAllFavoriteAddresses()).doesNotContain(fakeAddresses)
    }
}