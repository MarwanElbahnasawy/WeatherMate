package com.example.weathermate.presentation

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.MainViewModel
import com.example.weathermate.data.FakeRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainViewModelTest {
    private val testingContext: Application = ApplicationProvider.getApplicationContext()
    private val fakeRepository = FakeRepository(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(testingContext)
    )
    private val mainViewModel = MainViewModel(fakeRepository)

    @Test
    fun testPutBooleanInSharedPreferences() {
        //Given: mainViewModel is initialized
        //when: putBooleanInSharedPreferences() method is called

        val key = "testKey"
        val booleanInput = true
        mainViewModel.putBooleanInSharedPreferences(key, booleanInput)

        //then: value of key in shared preferences should be equal to booleanInput
        assertEquals(booleanInput, fakeRepository.getBooleanFromSharedPreferences(key, false))
    }

    @Test
    fun testIsArabic() {
        //Given: mainViewModel is initialized
        //when: isArabic() method is called
        //then: it should return false
        assertEquals(false, mainViewModel.isArabic())
    }

    @Test
    fun testIsLayoutChangedBySettings() {
        //Given: mainViewModel is initialized
        //when: isLayoutChangedBySettings() method is called
        //then: it should return false
        assertEquals(false, mainViewModel.isLayoutChangedBySettings())
    }

    @Test
    fun testIsThemeChangedBySettings() {
        //Given: mainViewModel is initialized
        //when: isThemeChangedBySettings() method is called
        //then: it should return false
        assertEquals(false, mainViewModel.isThemeChangedBySettings())
    }

    @Test
    fun testSetIsThemeChangedBySettingsToFalse() {
        //Given: mainViewModel is initialized
        //when: isThemeChangedBySettingsToFalse() method is called

        mainViewModel.setIsThemeChangedBySettingsToFalse()

        //then: value of isThemeChangedBySettings in shared preferences should be false
        assertEquals(false, fakeRepository.getBooleanFromSharedPreferences("isThemeChangedBySettings", true))
    }
}