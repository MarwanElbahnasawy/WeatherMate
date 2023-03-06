package com.example.weathermate.repository.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weathermate.model.FavoriteAddress

@Dao
interface FavoriteAddressDAO {
    @Query("SELECT * FROM FavoriteAddressTable")
    fun getAllFavoriteAddresses(): LiveData<List<FavoriteAddress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteAddress(address: FavoriteAddress)

    @Delete
    suspend fun deleteFavoriteAddress(address: FavoriteAddress)
}