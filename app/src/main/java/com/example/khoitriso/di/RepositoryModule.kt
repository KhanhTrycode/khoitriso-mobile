package com.example.khoitriso.di

import com.example.khoitriso.data.api.AdminApi
import com.example.khoitriso.data.api.BooksApi
import com.example.khoitriso.data.repository.AdminRepository
import com.example.khoitriso.data.repository.AuthRepositoryImpl
import com.example.khoitriso.data.repository.BookRepositoryImpl
import com.example.khoitriso.domain.repository.AuthRepository
import com.example.khoitriso.domain.repository.BookRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideBookRepository(bookRepositoryImpl: BookRepositoryImpl): BookRepository

    @Binds
    @Singleton
    abstract fun provideAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository
}

