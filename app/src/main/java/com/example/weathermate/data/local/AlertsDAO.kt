package com.example.weathermate.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weathermate.data.model.AlertItem

@Dao
interface AlertsDAO {

    @Query("SELECT * FROM AlertsTable")
    fun getAllAlerts(): LiveData<List<AlertItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertItem)

    @Delete
    suspend fun deleteAlert(alert: AlertItem)
}