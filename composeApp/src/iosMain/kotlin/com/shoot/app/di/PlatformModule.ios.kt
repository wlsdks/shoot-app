package com.shoot.app.di

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.shoot.app.database.ShootDatabase
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<SqlDriver> {
        NativeSqliteDriver(
            schema = ShootDatabase.Schema,
            name = "shoot.db"
        )
    }
    single { ShootDatabase(get()) }
}
