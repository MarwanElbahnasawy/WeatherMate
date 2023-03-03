package com.example.weathermate.repository.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.weathermate.model.FavoriteAddress

@Dao
interface FavoriteAddressDAO {
    @Query("SELECT * FROM FavoriteAddressTable")
    fun getAllFavoriteAddresses(): LiveData<List<FavoriteAddress>>

    @Insert
    suspend fun insertFavoriteAddress(address: FavoriteAddress)

    @Delete
    suspend fun deleteFavoriteAddress(address: FavoriteAddress)
}