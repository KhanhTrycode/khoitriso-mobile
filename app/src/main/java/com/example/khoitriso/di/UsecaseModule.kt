package com.example.khoitriso.di

import com.example.khoitriso.data.api.BooksApi
import com.example.khoitriso.data.repository.BookRepositoryImpl
import com.example.khoitriso.domain.repository.AuthRepository
import com.example.khoitriso.domain.repository.BookRepository
import com.example.khoitriso.domain.repository.CategoryRepository
import com.example.khoitriso.domain.repository.CourseRepository
import com.example.khoitriso.domain.repository.ForumRepository
import com.example.khoitriso.domain.repository.NotificationRepository
import com.example.khoitriso.domain.usecase.auth.AuthGoogleSDK
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.auth.GetMe
import com.example.khoitriso.domain.usecase.auth.LoadCurrentUserInfo
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
import com.example.khoitriso.domain.usecase.forum.*
import com.example.khoitriso.domain.usecase.notification.*
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
            refresh = RefreshToken(authRepository),
            getMe = GetMe(authRepository),
            loadCurrentUserInfo = LoadCurrentUserInfo(authRepository)
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

    @Provides
    @Singleton
    fun provideForumUsecase(forumRepository: ForumRepository): ForumUsecase {
        return ForumUsecase(
            getQuestions = GetQuestions(forumRepository),
            getQuestionById = GetQuestionById(forumRepository),
            createQuestion = CreateQuestion(forumRepository),
            updateQuestion = UpdateQuestion(forumRepository),
            deleteQuestion = DeleteQuestion(forumRepository),
            getAnswers = GetAnswers(forumRepository),
            createAnswer = CreateAnswer(forumRepository),
            updateAnswer = UpdateAnswer(forumRepository),
            deleteAnswer = DeleteAnswer(forumRepository),
            acceptAnswer = AcceptAnswer(forumRepository),
            unacceptAnswer = UnacceptAnswer(forumRepository),
            getComments = GetComments(forumRepository),
            createComment = CreateComment(forumRepository),
            vote = Vote(forumRepository),
            getVotes = GetVotes(forumRepository),
            getUserVote = GetUserVote(forumRepository),
            addBookmark = AddBookmark(forumRepository),
            removeBookmark = RemoveBookmark(forumRepository),
            isBookmarked = IsBookmarked(forumRepository),
            getBookmarks = GetBookmarks(forumRepository),
            getCategories = GetCategories(forumRepository),
            getTags = GetTags(forumRepository),
            getStats = GetStats(forumRepository)
        )
    }

    @Provides
    @Singleton
    fun provideNotificationUsecase(notificationRepository: NotificationRepository): NotificationUsecase {
        return NotificationUsecase(
            getUserNotifications = GetUserNotifications(notificationRepository),
            getNotificationById = GetNotificationById(notificationRepository),
            markAsRead = MarkAsRead(notificationRepository),
            markAllAsRead = MarkAllAsRead(notificationRepository)
        )
    }
}

