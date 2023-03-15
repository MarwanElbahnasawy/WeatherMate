package com.example.weathermate.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weathermate.data.model.FavoriteAddress
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class FavoriteAddressDAOTest {
    lateinit var myDatabase: MyDatabase
    lateinit var favoriteAddressDAO: FavoriteAddressDAO

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        myDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MyDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        favoriteAddressDAO = myDatabase.getFavoriteAddressDAO()

    }

    @After
    fun tearDown(){
        myDatabase.close()
    }

    @Test
    fun testInsertFavoriteAddress() = runBlockingTest {
        //Given: favorite address
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

        //When: insert favorite address to database
        favoriteAddressDAO.insertFavoriteAddress(favoriteAddress)

        val list = favoriteAddressDAO.getAllFavoriteAddresses()

        //then: assert that favoriteAddress is in the list of all favoriteAddresses
        assertThat(list).contains(favoriteAddress)
    }

    @Test
    fun testDeleteFavoriteAddress() = runBlockingTest {
        //Given: favorite address
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

        //When: insert favorite address to database
        favoriteAddressDAO.insertFavoriteAddress(favoriteAddress)

        //When: delete favorite address to database
        favoriteAddressDAO.deleteFavoriteAddress(favoriteAddress)

        val allAddresses = favoriteAddressDAO.getAllFavoriteAddresses()

        //then: assert that favoriteAddress is not in the list of all favoriteAddresses
        assertThat(allAddresses).doesNotContain(favoriteAddress)
    }

}