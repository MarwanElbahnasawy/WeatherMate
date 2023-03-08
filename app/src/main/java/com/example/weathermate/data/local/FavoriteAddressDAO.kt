package com.example.weathermate.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weathermate.data.model.FavoriteAddress

@Dao
interface FavoriteAddressDAO {
    @Query("SELECT * FROM FavoriteAddressTable")
    fun getAllFavoriteAddresses(): LiveData<List<FavoriteAddress>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteAddress(address: FavoriteAddress)

    @Delete
    suspend fun deleteFavoriteAddress(address: FavoriteAddress)
}