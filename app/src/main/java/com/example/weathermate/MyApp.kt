package com.example.weathermate

import android.app.Application
import com.example.weathermate.repository.local.LocalDataSource
import com.example.weathermate.repository.remote.RemoteDataSource

class MyApp : Application() {

    companion object {
        private var localDataSource: LocalDataSource? = null
        private var remoteDataSource: RemoteDataSource? = null

        @Synchronized
        fun getInstanceLocalDataSource(): LocalDataSource {
            if (localDataSource == null) {
                throw IllegalStateException("LocalDataSource not initialized")
            }
            return localDataSource!!
        }

        @Synchronized
        fun getInstanceRemoteDataSource(): RemoteDataSource {
            if (remoteDataSource == null) {
                throw IllegalStateException("RemoteDataSource not initialized")
            }
            return remoteDataSource!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        localDataSource = LocalDataSource.getInstance(this)
        remoteDataSource = RemoteDataSource.getInstance(this)
    }
}