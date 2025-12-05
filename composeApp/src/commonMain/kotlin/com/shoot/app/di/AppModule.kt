package com.shoot.app.di

import cafe.adriel.voyager.core.model.ScreenModel
import com.shoot.app.data.local.TokenManager
import com.shoot.app.data.local.TokenManagerImpl
import com.shoot.app.data.network.HttpClientFactory
import com.shoot.app.data.remote.api.AuthApiService
import com.shoot.app.data.remote.api.ChatRoomApiService
import com.shoot.app.data.remote.api.FriendApiService
import com.shoot.app.data.remote.api.MessageApiService
import com.shoot.app.data.repository.AuthRepository
import com.shoot.app.data.repository.AuthRepositoryImpl
import com.shoot.app.data.repository.ChatRoomRepository
import com.shoot.app.data.repository.ChatRoomRepositoryImpl
import com.shoot.app.data.repository.FriendRepository
import com.shoot.app.data.repository.FriendRepositoryImpl
import com.shoot.app.data.repository.MessageRepository
import com.shoot.app.data.repository.MessageRepositoryImpl
import com.shoot.app.data.repository.SampleRepository
import com.shoot.app.data.repository.SampleRepositoryImpl
import com.shoot.app.data.websocket.WebSocketClient
import com.shoot.app.presentation.auth.AuthViewModel
import com.shoot.app.presentation.chatroom.ChatRoomViewModel
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
    single { ChatRoomApiService(get()) }
    single { MessageApiService(get()) }

    // WebSocket Client
    single { WebSocketClient(get(), "localhost:8100") }

    // Repositories
    singleOf(::SampleRepositoryImpl) bind SampleRepository::class
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::FriendRepositoryImpl) bind FriendRepository::class
    singleOf(::ChatRoomRepositoryImpl) bind ChatRoomRepository::class
    singleOf(::MessageRepositoryImpl) bind MessageRepository::class

    // ViewModels / ScreenModels
    single<HomeViewModel> { HomeViewModel(get()) }
    single<AuthViewModel> { AuthViewModel(get(), get()) }
    single<FriendViewModel> { FriendViewModel(get(), get()) }
    single<ChatRoomViewModel> { ChatRoomViewModel(get(), get()) }
}

expect val platformModule: Module
