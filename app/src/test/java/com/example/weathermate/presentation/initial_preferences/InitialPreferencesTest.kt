package com.example.weathermate.presentation.initial_preferences

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.data.FakeRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class InitialPreferencesViewModelTest {
    private val testingContext: Application = ApplicationProvider.getApplicationContext()
    private val fakeRepository = FakeRepository(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(testingContext)
    )

    private val initialPreferencesViewModel = InitialPreferencesViewModel(fakeRepository)


    @Test
    fun testPutStringInSharedPreferences() {
        //Given: InitialPreferencesViewModel is initialized
        //when: putStringInSharedPreferences() method is called

        val key = "testKey"
        val stringInput = "testString"
        initialPreferencesViewModel.putStringInSharedPreferences(key, stringInput)

        //then: value of key from shared preferences should be equal to stringInput
        assertEquals(stringInput, fakeRepository.getStringFromSharedPreferences(key, ""))
    }

    @Test
    fun testPutBooleanInSharedPreferences() {
        //Given: InitialPreferencesViewModel is initialized
        //when: putBooleanInSharedPreferences() method is called

        val key = "testKey"
        val booleanInput = true
        initialPreferencesViewModel.putBooleanInSharedPreferences(key, booleanInput)

        //then: value of key from shared preferences should be equal to booleanInput
        assertEquals(booleanInput, fakeRepository.getBooleanFromSharedPreferences(key, false))
    }


    @Test
    fun testSetIsDarkTrue() {
        //Given: InitialPreferencesViewModel is initialized
        //when: setIsDarkTrue() method is called

        initialPreferencesViewModel.setIsDarkTrue()

        //then: value of isDarkTheme from shared preferences should be equal to true
        assertEquals(true, fakeRepository.getBooleanFromSharedPreferences("isDarkTheme", false))
    }
}