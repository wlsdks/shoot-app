package com.shoot.app

import android.app.Application
import com.shoot.app.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.logger.Level

class ShootApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ShootApplication)
        }
    }
}
