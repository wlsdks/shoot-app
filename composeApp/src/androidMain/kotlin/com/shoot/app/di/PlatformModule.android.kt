package com.shoot.app.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.shoot.app.database.ShootDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = ShootDatabase.Schema,
            context = androidContext(),
            name = "shoot.db"
        )
    }
    single { ShootDatabase(get()) }
}
