package com.example.weathermate.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.getOrAwaitValue
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AlertsDAOTest {
    lateinit var myDatabase: MyDatabase
    lateinit var alertsDAO: AlertsDAO

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        myDatabase = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MyDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        alertsDAO = myDatabase.getAlertsDAO()
    }

    @After
    fun tearDown() {
        myDatabase.close()
    }

    @Test
    fun testInsertAlert() = runBlockingTest {
        //Given: alert item
        val alert = AlertItem(
            "address",
         "longitudeString",
         "latitudeString",
         "startString",
         "endString",
        1,
        2,
        3,
         "alertType",
        4
        )

        // when: insert alert to database
        alertsDAO.insertAlert(alert)

        val alerts = alertsDAO.getAllAlerts().getOrAwaitValue()

        // then: assert that alert is in the list of all alerts
        assertThat(alerts).contains(alert)
    }

    @Test
    fun testDeleteAlert() = runBlockingTest {
        //Given: alert item
        val alert = AlertItem(
            "address",
            "longitudeString",
            "latitudeString",
            "startString",
            "endString",
            1,
            2,
            3,
            "alertType",
            4
        )

        // when: insert alert to database
        alertsDAO.insertAlert(alert)

        // when: delete alert to database
        alertsDAO.deleteAlert(alert)

        val alerts = alertsDAO.getAllAlerts().getOrAwaitValue()

        // then: assert that alert is not in the list of all alerts
        assertThat(alerts).doesNotContain(alert)
    }
}