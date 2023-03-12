package com.example.weathermate

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.weathermate.data.Repository
import com.example.weathermate.data.local.LocalDataSource
import com.example.weathermate.data.remote.RemoteDataSource
import com.example.weathermate.util.MyHelper
import com.example.weathermate.util.NetworkManager

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

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate() {
        super.onCreate()
        MyHelper.createSharedPreferencesInstance(this)
        localDataSource = LocalDataSource.getInstance(this)
        remoteDataSource = RemoteDataSource.getInstance(this)
        NetworkManager.init(this)
    }
}