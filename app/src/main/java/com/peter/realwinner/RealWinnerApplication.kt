package com.peter.realwinner

import android.app.Application
import android.content.Context
import com.peter.realwinner.di.appModule
import com.peter.realwinner.di.dbModule
import com.peter.realwinner.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RealWinnerApplication : Application() {

    companion object {
        private lateinit var instance: RealWinnerApplication

        fun getBaseContext() : Context {
            return instance
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RealWinnerApplication)
            modules(listOf(appModule, dbModule, viewModelModule))
        }
    }
}