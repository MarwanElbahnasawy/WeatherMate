package com.example.weathermate.presentation.splash

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.data.FakeRepository
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SplashViewModelTest {
    private val testingContext: Application = ApplicationProvider.getApplicationContext()
    private val fakeRepository = FakeRepository(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(testingContext)
    )
    private val splashViewModel = SplashViewModel(fakeRepository)

    @Test
    fun testIsPreferencesSet() {
        //Given: SplashViewModel is initialized
        //when: isPreferencesSet() method called

        //then: value in shared preferences should be false
        assertEquals(false, splashViewModel.isPreferencesSet())
    }

    @Test
    fun testIsDark() {
        //Given: SplashViewModel is initialized
        //when: tsDark() method is called

        //then: value of key from shared preferences should be false
        assertEquals(false, splashViewModel.isDark())
    }
}