package com.example.weathermate.presentation.favorites

import android.app.Application
import androidx.preference.PreferenceManager
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.weathermate.data.FakeRepository
import com.example.weathermate.data.model.FavoriteAddress
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class FavoritesViewModelTest {

    val testingContext: Application = ApplicationProvider.getApplicationContext()
    private val fakeRepository = FakeRepository(sharedPreferences = PreferenceManager.getDefaultSharedPreferences(testingContext))

    private val favoritesViewModel = FavoritesViewModel(fakeRepository)



    @Test
    fun testGetAllFavoriteAddresses() = runBlockingTest {
        //given FavoriteAddress object
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
        //when insert favorite address in database
        fakeRepository.insertFavoriteAddress(favoriteAddress)

        //then list of favorite addresses in database should contain the favorite address
        assertEquals(listOf(favoriteAddress), favoritesViewModel.getAllFavoriteAddresses())
    }

    @Test
    fun testDeleteFavoriteAddress() = runBlockingTest {
        //given FavoriteAddress object
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

        //when insert favorite address in database
        fakeRepository.insertFavoriteAddress(favoriteAddress)
        //when delete favorite address from database
        favoritesViewModel.deleteFavoriteAddress(favoriteAddress)

        //then favorite address list in database should be empty
        assertEquals(emptyList<FavoriteAddress>(), favoritesViewModel.getAllFavoriteAddresses())
    }

    @Test
    fun testPutStringInSharedPreferences() {
        //Given: favoritesViewModel is initialized
        //when: putStringInSharedPreferences() method is called

        val key = "testKey"
        val stringInput = "testString"
        favoritesViewModel.putStringInSharedPreferences(key, stringInput)

        //then: value of key from shared preferences should be equal to stringInput
        assertEquals(stringInput, fakeRepository.getStringFromSharedPreferences(key, ""))
    }
}