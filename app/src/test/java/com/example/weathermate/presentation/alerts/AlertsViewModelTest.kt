package com.example.weathermate.presentation.alerts

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.data.FakeRepository
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AlertsViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    val testingContext: Application = ApplicationProvider.getApplicationContext()

    private val fakeRepository = FakeRepository(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(testingContext))
    private val alertsViewModel = AlertsViewModel(fakeRepository)



    @Test
    fun testInsertAlert() = runBlockingTest {
        //given alertItem object
        val alertItem = AlertItem(
            "address",
            "longitudeString",
            "latitudeString",
            "startString",
            "endString",
            1,
            2,
            3,
            "alertType",
            4
        )
        //when insert alert item to database
        alertsViewModel.insertAlert(alertItem)
        //then alert item should be in database
        assertEquals(listOf(alertItem), alertsViewModel.getAllAlerts().getOrAwaitValue())
    }

    @Test
    fun testDeleteAlert() = runBlockingTest {
        //given alertItem object
        val alertItem = AlertItem(
            "address",
            "longitudeString",
            "latitudeString",
            "startString",
            "endString",
            1,
            2,
            3,
            "alertType",
            4
        )
        //when insert alert item to database
        alertsViewModel.insertAlert(alertItem)
        //when delete alert item to database
        alertsViewModel.deleteAlert(alertItem)

        //then alert item should not be in database
        assertEquals(emptyList<AlertItem>(), alertsViewModel.getAllAlerts().getOrAwaitValue())

    }

    @Test
    fun testPutStringInSharedPreferences() {
        //Given: alertsViewModel is initialized
        //when: putStringInSharedPreferences() method is called

        val key = "testKey"
        val stringInput = "testString"
        alertsViewModel.putStringInSharedPreferences(key, stringInput)
//then: value of testKey from shared preferences should be equal to stringInput
        assertEquals(stringInput, fakeRepository.getStringFromSharedPreferences(key, ""))
    }

    @Test
    fun testGetStringFromSharedPreferences() {
        //Given: alertsViewModel is initialized
        //when: getStringFromSharedPreferences() method is called

        val key = "testKey"
        val stringDefault = "defaultString"
        //then: value of testKey from shared preferences should be equal to stringDefault
        assertEquals(stringDefault, alertsViewModel.getStringFromSharedPreferences(key, stringDefault))
    }

    @Test
    fun testPutBooleanInSharedPreferences() {
        //Given: alertsViewModel is initialized
        //when: putBooleanInSharedPreferences() method is called

        val key = "testKey"
        val booleanInput = true
        alertsViewModel.putBooleanInSharedPreferences(key, booleanInput)
        //then: value of testKey from shared preferences should be equal to booleanInput
        assertEquals(booleanInput, fakeRepository.getBooleanFromSharedPreferences(key, false))
    }

    @Test
    fun testGetBooleanFromSharedPreferences() {
        //Given: alertsViewModel is initialized
        //when: gGetBooleanFromSharedPreferences() method is called

        val key = "testKey"
        val booleanDefault = false
        //then: value of testKey from shared preferences should be equal to booleanDefault
        assertEquals(booleanDefault, alertsViewModel.getBooleanFromSharedPreferences(key, booleanDefault))
    }
}