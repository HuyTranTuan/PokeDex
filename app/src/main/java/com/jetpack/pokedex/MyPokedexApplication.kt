package com.jetpack.pokedex

import android.app.Application
import com.jetpack.pokedex.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyPokedexApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            // Log Koin activity (Level.ERROR or Level.NONE in production)
            androidLogger(Level.DEBUG)
            // Declare Android context
            androidContext(this@MyPokedexApplication)
            // Declare modules to use
            modules(appModule)
        }
    }
}