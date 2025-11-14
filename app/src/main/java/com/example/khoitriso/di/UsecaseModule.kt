package com.example.khoitriso.di

import com.example.khoitriso.data.api.BooksApi
import com.example.khoitriso.data.repository.BookRepositoryImpl
import com.example.khoitriso.domain.repository.AuthRepository
import com.example.khoitriso.domain.repository.BookRepository
import com.example.khoitriso.domain.usecase.auth.AuthGoogleSDK
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.auth.RefreshToken
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.book.GetBook
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UsecaseModule {

    @Provides
    @Singleton
    fun provideBookUseCase(bookRepository: BookRepository): BookUsecase {
        return BookUsecase(getBook = GetBook(bookRepository))
    }

    @Provides
    @Singleton
    fun provideAuthUsecase(authRepository: AuthRepository): AuthUsecase {
        return AuthUsecase(
            authGoogleSDK = AuthGoogleSDK(authRepository),
            refresh = RefreshToken(authRepository)
        )
    }
}

