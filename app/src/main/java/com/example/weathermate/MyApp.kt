package com.example.weathermate

import android.app.Application
import com.example.weathermate.repository.Repository
import com.example.weathermate.repository.local.LocalDataSource
import com.example.weathermate.repository.remote.RemoteDataSource

class MyApp : Application() {

    companion object {
        private var localDataSource: LocalDataSource? = null
        private var remoteDataSource: RemoteDataSource? = null
        private var repository: Repository? = null

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
        localDataSource = LocalDataSource.getInstance(this)
        remoteDataSource = RemoteDataSource.getInstance(this)
        repository = Repository(localDataSource!!, remoteDataSource!!)
    }
}