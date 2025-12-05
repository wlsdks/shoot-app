package com.shoot.app.di

import cafe.adriel.voyager.core.model.ScreenModel
import com.shoot.app.data.local.TokenManager
import com.shoot.app.data.local.TokenManagerImpl
import com.shoot.app.data.network.HttpClientFactory
import com.shoot.app.data.remote.api.AuthApiService
import com.shoot.app.data.remote.api.FriendApiService
import com.shoot.app.data.repository.AuthRepository
import com.shoot.app.data.repository.AuthRepositoryImpl
import com.shoot.app.data.repository.FriendRepository
import com.shoot.app.data.repository.FriendRepositoryImpl
import com.shoot.app.data.repository.SampleRepository
import com.shoot.app.data.repository.SampleRepositoryImpl
import com.shoot.app.presentation.auth.AuthViewModel
import com.shoot.app.presentation.friend.FriendViewModel
import com.shoot.app.presentation.viewmodel.HomeViewModel
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val commonModule = module {
    // Token Manager
    single<TokenManager> { TokenManagerImpl() }

    // HTTP Client with Auth Interceptor
    single<HttpClient> { HttpClientFactory.create(get()) }

    // API Services
    single { AuthApiService(get()) }
    single { FriendApiService(get()) }

    // Repositories
    singleOf(::SampleRepositoryImpl) bind SampleRepository::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::FriendRepositoryImpl) bind FriendRepository::class

    // ViewModels / ScreenModels
    single<HomeViewModel> { HomeViewModel(get()) }
    single<AuthViewModel> { AuthViewModel(get(), get()) }
    single<FriendViewModel> { FriendViewModel(get(), get()) }
}

expect val platformModule: Module
