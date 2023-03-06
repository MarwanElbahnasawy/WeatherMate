package com.example.weathermate.repository.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.weathermate.model.Alert
import com.example.weathermate.model.AlertItem
import com.example.weathermate.model.FavoriteAddress
import com.example.weathermate.model.WeatherData

@Dao
interface AlertsDAO {

    @Query("SELECT * FROM AlertsTable")
    fun getAllAlerts(): LiveData<List<AlertItem>>

    @Query("SELECT * FROM AlertsTable WHERE idHashLongFromLonLatStartStringEndStringAlertType LIKE :idInputLong")
    suspend fun findAlert(idInputLong : Long): AlertItem

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: AlertItem)

    @Delete
    suspend fun deleteAlert(alert: AlertItem)
}