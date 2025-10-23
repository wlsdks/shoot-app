package com.shoot.app.di

import cafe.adriel.voyager.core.model.ScreenModel
import com.shoot.app.data.network.HttpClientFactory
import com.shoot.app.data.repository.SampleRepository
import com.shoot.app.data.repository.SampleRepositoryImpl
import com.shoot.app.presentation.viewmodel.HomeViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    single<HttpClient> { HttpClientFactory.create() }
    singleOf(::SampleRepositoryImpl) bind SampleRepository::class
    single<HomeViewModel> { HomeViewModel(get()) }
}

expect val platformModule: Module
