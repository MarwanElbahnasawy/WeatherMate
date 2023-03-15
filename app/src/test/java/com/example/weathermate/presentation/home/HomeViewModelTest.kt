package com.example.weathermate.presentation.home

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.data.FakeRepository
import com.example.weathermate.data.model.*
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testingContext: Application = ApplicationProvider.getApplicationContext()
    private val fakeRepository = FakeRepository(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(testingContext)
    )
    private val homeViewModel = HomeViewModel(fakeRepository)



    @Test
    fun testGetTemperatureUnit() {
        //Given: HomeViewModel is initialized
        //when: getTemperatureUnit() method is called

        val key = "temperature_unit"
        val stringInput = "Celsius"
        fakeRepository.putStringInSharedPreferences(key, stringInput)

        //then: value of getTemperatureUnit() should be equal to stringInput
        assertEquals(stringInput, homeViewModel.getTemperatureUnit())
    }

    @Test
    fun testGetWindSpeedUnit() {
        //Given: HomeViewModel is initialized
        //when: gtWindSpeedUnit() method is called

        val key = "wind_speed_unit"
        val stringInput = "m/s"
        fakeRepository.putStringInSharedPreferences(key, stringInput)

        //then: value of getWindSpeedUnit() should be equal to stringInput
        assertEquals(stringInput, homeViewModel.getWindSpeedUnit())
    }

    @Test
    fun testGetStringFromSharedPreferences() {
        //Given: HomeViewModel is initialized
        //when: getStringFromSharedPreferences() method is called

        val key = "testKey"
        val stringDefault = "defaultString"

        //then: value of key from shared preferences should be equal to stringDefault
        assertEquals(
            stringDefault,
            homeViewModel.getStringFromSharedPreferences(key, stringDefault)
        )
    }

}