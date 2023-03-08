package com.example.weathermate

import android.app.Application
import com.example.weathermate.data.Repository
import com.example.weathermate.data.local.LocalDataSource
import com.example.weathermate.data.remote.RemoteDataSource
import com.example.weathermate.util.HelperObject

class MyApp : Application() {
    companion object {

        private var localDataSource: LocalDataSource? = null
        private var remoteDataSource: RemoteDataSource? = null
        private val repository by lazy { Repository(localDataSource!!, remoteDataSource!!) }

        @Synchronized
        fun getInstanceRepository(): Repository {
            if (repository == null) {
                throw IllegalStateException("Repository not initialized")
            }
            return repository!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        HelperObject.createSharedPreferencesInstance(this)
        localDataSource = LocalDataSource.getInstance(this)
        remoteDataSource = RemoteDataSource.getInstance(this)
    }
}