package com.barinov.simpleplayer.ui

import android.app.Application
import com.barinov.simpleplayer.modul.module
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class SimplePlayerApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SimplePlayerApp)
            androidLogger(Level.DEBUG)
            modules(module)
        }
    }
}