package com.example.khoitriso.di

import com.example.khoitriso.data.api.BooksApi
import com.example.khoitriso.data.repository.BookRepositoryImpl
import com.example.khoitriso.domain.repository.AuthRepository
import com.example.khoitriso.domain.repository.BookRepository
import com.example.khoitriso.domain.repository.CategoryRepository
import com.example.khoitriso.domain.repository.CourseRepository
import com.example.khoitriso.domain.usecase.auth.AuthGoogleSDK
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.auth.RefreshToken
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.book.GetBook
import com.example.khoitriso.domain.usecase.book.GetBookById
import com.example.khoitriso.domain.usecase.book.SearchBook
import com.example.khoitriso.domain.usecase.category.CategoryUsecase
import com.example.khoitriso.domain.usecase.category.GetCategory
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.domain.usecase.course.GetCourse
import com.example.khoitriso.domain.usecase.course.GetCourseById
import com.example.khoitriso.domain.usecase.course.GetMyCourse
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
        return BookUsecase(
            getBook = GetBook(bookRepository),
            getBookById = GetBookById(bookRepository),
            searckBook = SearchBook(bookRepository),
        )
    }

    @Provides
    @Singleton
    fun provideAuthUsecase(authRepository: AuthRepository): AuthUsecase {
        return AuthUsecase(
            authGoogleSDK = AuthGoogleSDK(authRepository),
            refresh = RefreshToken(authRepository)
        )
    }

    @Provides
    @Singleton
    fun provideCourseUsecase(courseRepository: CourseRepository): CourseUsecase {
        return CourseUsecase(
            getCourse = GetCourse(courseRepository),
            getCourseById = GetCourseById(courseRepository),
            getMyCourse = GetMyCourse(courseRepository)
        )
    }

    @Provides
    @Singleton
    fun provideCategoryUsecase(categoryRepository: CategoryRepository): CategoryUsecase {
        return CategoryUsecase(
            getCategory = GetCategory(categoryRepository)
        )
    }
}

