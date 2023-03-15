package com.example.weathermate.presentation.map

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.data.FakeRepository
import com.example.weathermate.data.model.FavoriteAddress
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MapViewModelTest {
    private val testingContext: Application = ApplicationProvider.getApplicationContext()
    private val fakeRepository = FakeRepository(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(testingContext)
    )
    private val mapViewModel = MapViewModel(fakeRepository)


    @Test
    fun testPutStringInSharedPreferences() {
        //Given: MapViewModel is initialized
        //when: putStringInSharedPreferences() method is called

        val key = "testKey"
        val stringInput = "testString"
        mapViewModel.putStringInSharedPreferences(key, stringInput)

        //then: value of key from shared preferences should be equal to stringInput
        assertEquals(stringInput, fakeRepository.getStringFromSharedPreferences(key, ""))
    }

    @Test
    fun testGetStringFromSharedPreferences() {
        //Given: MapViewModel is initialized
        //when: getStringFromSharedPreferences() method is called

        val key = "testKey"
        val stringDefault = "defaultString"

        //then: value of key from shared preferences should be equal to stringDefault
        assertEquals(stringDefault, mapViewModel.getStringFromSharedPreferences(key, stringDefault))
    }

    @Test
    fun testPutBooleanInSharedPreferences() {
        //Given: MapViewModel is initialized
        //when: putBooleanInSharedPreferences() method is called

        val key = "testKey"
        val booleanInput = true
        mapViewModel.putBooleanInSharedPreferences(key, booleanInput)

        //then: value of key from shared preferences should be equal to booleanInput
        assertEquals(booleanInput, fakeRepository.getBooleanFromSharedPreferences(key, false))
    }

    @Test
    fun testGetBooleanFromSharedPreferences() {
        //Given: MapViewModel is initialized
        //when: getBooleanFromSharedPreferences() method is called

        val key = "testKey"
        val booleanDefault = false

        //then: value of key from shared preferences should be equal to booleanDefault
        assertEquals(booleanDefault, mapViewModel.getBooleanFromSharedPreferences(key, booleanDefault))
    }

    @Test
    fun testInsertToFavorites() = runBlockingTest {
        //Given: FavoriteAddress object
        val favoriteAddress = FavoriteAddress(
            "address",
            1.1,
            2.2,
            "1.12.2",
            50.0,
            "weather is nice",
            3,
            "icon"
        )

        //When: insert to database
        mapViewModel.insertToFavorites(favoriteAddress)

        //then: database list should be equal to list of favorite address inserted
        assertEquals(listOf(favoriteAddress), fakeRepository.getAllFavoriteAddresses())
    }
}