package com.example.khoitriso.di

import android.content.Context
import com.example.khoitriso.data.api.*
import com.example.khoitriso.data.local.NetworkMonitor
import com.example.khoitriso.data.local.TokenAuthenticator
import com.example.khoitriso.data.local.TokenManager
import com.example.khoitriso.data.signalr.SignalRService
import com.google.gson.Gson
import com.example.khoitriso.utils.Constants
import com.example.khoitriso.utils.debug
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // ----- NETWORK MONITOR -----
    @Provides
    @Singleton
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor =
        NetworkMonitor(context)

    // ----- TOKEN MANAGER -----
    @Provides
    @Singleton
    fun provideTokenManager(@ApplicationContext context: Context): TokenManager =
        TokenManager(context)

    // ----- PUBLIC OkHttp (không có token, dùng cho login/refresh) -----
    @Provides
    @Singleton
    @Named("public")
    fun providePublicOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    // ----- SECURE OkHttp (có token + Authenticator) -----
    @Provides
    @Singleton
    @Named("secure")
    fun provideSecureOkHttpClient(
        tokenAuthenticator: TokenAuthenticator,
        tokenManager: TokenManager
    ): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            // Gắn token vào header
            .addInterceptor { chain ->
                val token = tokenManager.getAccessToken()
                val newReq = token?.let {
                    debug(it,"Call API")
                    chain.request().newBuilder()
                        .header("Authorization", "Bearer $it")
                        .build()
                } ?: chain.request()
                chain.proceed(newReq)
            }
            .authenticator(tokenAuthenticator)
            .build()

    // ----- PUBLIC Retrofit -----
    @Provides
    @Singleton
    @Named("publicRetrofit")
    fun providePublicRetrofit(@Named("public") client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // ----- SECURE Retrofit -----
    @Provides
    @Singleton
    @Named("secureRetrofit")
    fun provideSecureRetrofit(@Named("secure") client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_API_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    // ----- TOKEN AUTHENTICATOR -----
    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenManager: TokenManager,
            @Named("publicRetrofit") publicRetrofit: Retrofit
    ): TokenAuthenticator {
        val authApi = publicRetrofit.create(AuthApi::class.java)
        return TokenAuthenticator(tokenManager, authApi)
    }

    // ----- APIs -----

    @Provides
    @Singleton
    fun providePublicApi(@Named("publicRetrofit") retrofit: Retrofit): PublicApi =
        retrofit.create(PublicApi::class.java)

    // AuthApi dùng public Retrofit (login/refresh)
    @Provides
    @Singleton
    fun provideAuthApi(@Named("secureRetrofit") retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    // CourseApi

    @Provides
    @Singleton
    fun provideCourseApi(@Named("secureRetrofit") retrofit: Retrofit): CourseApi =
        retrofit.create(CourseApi::class.java)

    // AdminApi dùng secure Retrofit (cần token)
    @Provides
    @Singleton
    fun provideAdminApi(@Named("secureRetrofit") retrofit: Retrofit): AdminApi =
        retrofit.create(AdminApi::class.java)

    // Các API khác (secure)
    @Provides
    @Singleton
    fun provideBooksApi(@Named("secureRetrofit") retrofit: Retrofit): BooksApi =
        retrofit.create(BooksApi::class.java)

    @Provides
    @Singleton
    fun provideCartApi(@Named("secureRetrofit") retrofit: Retrofit): CartApi =
        retrofit.create(CartApi::class.java)

    @Provides
    @Singleton
    fun provideCategoriesApi(@Named("secureRetrofit") retrofit: Retrofit): CategoriesApi =
        retrofit.create(CategoriesApi::class.java)

    @Provides
    @Singleton
    fun provideCertificatesApi(@Named("secureRetrofit") retrofit: Retrofit): CertificatesApi =
        retrofit.create(CertificatesApi::class.java)

    @Provides
    @Singleton
    fun provideDiscussionsApi(@Named("secureRetrofit") retrofit: Retrofit): DiscussionsApi =
        retrofit.create(DiscussionsApi::class.java)

    @Provides
    @Singleton
    fun provideLearningPathsApi(@Named("secureRetrofit") retrofit: Retrofit): LearningPathsApi =
        retrofit.create(LearningPathsApi::class.java)

    @Provides
    @Singleton
    fun provideLessonsApi(@Named("secureRetrofit") retrofit: Retrofit): LessonsApi =
        retrofit.create(LessonsApi::class.java)

    @Provides
    @Singleton
    fun provideLiveClassesApi(@Named("secureRetrofit") retrofit: Retrofit): LiveClassesApi =
        retrofit.create(LiveClassesApi::class.java)

    @Provides
    @Singleton
    fun provideNotificationsApi(@Named("secureRetrofit") retrofit: Retrofit): NotificationsApi =
        retrofit.create(NotificationsApi::class.java)

    @Provides
    @Singleton
    fun provideOrdersApi(@Named("secureRetrofit") retrofit: Retrofit): OrdersApi =
        retrofit.create(OrdersApi::class.java)

    @Provides
    @Singleton
    fun provideReviewsApi(@Named("secureRetrofit") retrofit: Retrofit): ReviewsApi =
        retrofit.create(ReviewsApi::class.java)

    @Provides
    @Singleton
    fun provideSystemApi(@Named("secureRetrofit") retrofit: Retrofit): SystemApi =
        retrofit.create(SystemApi::class.java)

    @Provides
    @Singleton
    fun provideUsersApi(@Named("secureRetrofit") retrofit: Retrofit): UsersApi =
        retrofit.create(UsersApi::class.java)

    @Provides
    @Singleton
    fun provideWishlistApi(@Named("secureRetrofit") retrofit: Retrofit): WishlistApi =
        retrofit.create(WishlistApi::class.java)

    @Provides
    @Singleton
    fun provideForumApi(@Named("secureRetrofit") retrofit: Retrofit): ForumApi =
        retrofit.create(ForumApi::class.java)

    @Provides
    @Singleton
    fun provideVNPayApi(@Named("secureRetrofit") retrofit: Retrofit): com.example.khoitriso.data.api.VNPayApi =
        retrofit.create(com.example.khoitriso.data.api.VNPayApi::class.java)

    @Provides
    @Singleton
    fun provideAssignmentApi(@Named("secureRetrofit") retrofit: Retrofit): AssignmentApi =
        retrofit.create(AssignmentApi::class.java)

    // SignalR Service
    @Provides
    @Singleton
    fun provideSignalRService(
        tokenManager: TokenManager,
        gson: Gson
    ): SignalRService = SignalRService(tokenManager, gson)

    // Gson
    @Provides
    @Singleton
    fun provideGson(): Gson = com.google.gson.Gson()
}
