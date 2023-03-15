package com.example.weathermate.presentation.settings

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.data.FakeRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsViewModelTest {

    private val testingContext: Application = ApplicationProvider.getApplicationContext()
    private val fakeRepository = FakeRepository(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(testingContext)
    )
    private val settingsViewModel = SettingsViewModel(fakeRepository)

    @Test
    fun testPutStringInSharedPreferences() {
        //Given: SettingsViewModel is initialized
        //when: putStringInSharedPreferences() method is called

        val key = "testKey"
        val stringInput = "testString"
        settingsViewModel.putStringInSharedPreferences(key, stringInput)

        //then: value of key from shared preferences should be equal to stringInput
        assertEquals(stringInput, fakeRepository.getStringFromSharedPreferences(key, ""))
    }

    @Test
    fun testPutBooleanInSharedPreferences() {
        //Given: SettingsViewModel is initialized
        //when: putBooleanInSharedPreferences() method is called

        val key = "testKey"
        val booleanInput = true
        settingsViewModel.putBooleanInSharedPreferences(key, booleanInput)

        //then: value of key from shared preferences should be equal to booleanInput
        assertEquals(booleanInput, fakeRepository.getBooleanFromSharedPreferences(key, false))
    }

    @Test
    fun testGetStringFromSharedPreferences() {
        //Given: SettingsViewModel is initialized
        //when: getStringFromSharedPreferences() method is called

        val key = "testKey"
        val stringDefault = "defaultString"

        //then: value of key from shared preferences should be equal to stringDefault
        assertEquals(stringDefault, settingsViewModel.getStringFromSharedPreferences(key, stringDefault))
    }

    @Test
    fun testCheckIsDarkSharedPreferences() {
        //Given: SettingsViewModel is initialized
        //when: checkIsDarkSharedPreferences() method is called

        //then: value in shared preferences should be false
        assertEquals(false, settingsViewModel.checkIsDarkSharedPreferences())
    }
}