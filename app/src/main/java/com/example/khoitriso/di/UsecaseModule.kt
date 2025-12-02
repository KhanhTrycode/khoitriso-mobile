package com.example.khoitriso.di

import com.example.khoitriso.data.api.BooksApi
import com.example.khoitriso.data.repository.BookRepositoryImpl
import com.example.khoitriso.domain.repository.AuthRepository
import com.example.khoitriso.domain.repository.BookRepository
import com.example.khoitriso.domain.repository.CartRepository
import com.example.khoitriso.domain.repository.CategoryRepository
import com.example.khoitriso.domain.repository.CourseRepository
import com.example.khoitriso.domain.repository.ForumRepository
import com.example.khoitriso.domain.repository.NotificationRepository
import com.example.khoitriso.domain.repository.OrderRepository
import com.example.khoitriso.domain.repository.VNPayRepository
import com.example.khoitriso.domain.usecase.auth.AuthGoogleSDK
import com.example.khoitriso.domain.usecase.auth.AuthUsecase
import com.example.khoitriso.domain.usecase.auth.GetMe
import com.example.khoitriso.domain.usecase.auth.LoadCurrentUserInfo
import com.example.khoitriso.domain.usecase.auth.RefreshToken
import com.example.khoitriso.domain.usecase.book.BookUsecase
import com.example.khoitriso.domain.usecase.book.GetBook
import com.example.khoitriso.domain.usecase.book.GetBookById
import com.example.khoitriso.domain.usecase.book.GetChapterOfBook
import com.example.khoitriso.domain.usecase.book.GetMyBook
import com.example.khoitriso.domain.usecase.category.CategoryUsecase
import com.example.khoitriso.domain.usecase.category.GetCategory
import com.example.khoitriso.domain.usecase.course.CourseUsecase
import com.example.khoitriso.domain.usecase.course.GetAssignmentById
import com.example.khoitriso.domain.usecase.course.GetCourse
import com.example.khoitriso.domain.usecase.course.GetCourseById
import com.example.khoitriso.domain.usecase.course.GetMyCourse
import com.example.khoitriso.domain.usecase.forum.*
import com.example.khoitriso.domain.usecase.notification.*
import com.example.khoitriso.domain.usecase.order.AddToCart
import com.example.khoitriso.domain.usecase.order.CartUsecase
import com.example.khoitriso.domain.usecase.order.ClearCart
import com.example.khoitriso.domain.usecase.order.CreateOrder
import com.example.khoitriso.domain.usecase.order.GetCart
import com.example.khoitriso.domain.usecase.order.GetOrder
import com.example.khoitriso.domain.usecase.order.GetOrderById
import com.example.khoitriso.domain.usecase.order.OrderUsecase
import com.example.khoitriso.domain.usecase.order.PaymentFreeOrder
import com.example.khoitriso.domain.usecase.order.PaymentProgress
import com.example.khoitriso.domain.usecase.order.QueryTransaction
import com.example.khoitriso.domain.usecase.order.RemoveFromCart
import com.example.khoitriso.domain.usecase.order.VNPayCreatePaymentUrl
import com.example.khoitriso.domain.usecase.user.GetCurrentUser
import com.example.khoitriso.domain.usecase.user.GetProfile
import com.example.khoitriso.domain.usecase.user.UpdateProfile
import com.example.khoitriso.domain.usecase.user.UploadAvatar
import com.example.khoitriso.domain.usecase.user.UserUsecase
import com.example.khoitriso.domain.repository.UserRepository
import com.example.khoitriso.domain.repository.LessonDiscussionRepository
import com.example.khoitriso.domain.repository.WishlistRepository
import com.example.khoitriso.domain.usecase.discussion.*
import com.example.khoitriso.domain.usecase.wishlist.*
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
            getMyBook = GetMyBook(bookRepository),
            getChapterOfBook = GetChapterOfBook(bookRepository)
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
            getMyCourse = GetMyCourse(courseRepository),
            getAssignmentById = GetAssignmentById(courseRepository)
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
            getComments = GetComments(forumRepository),
            createComment = CreateComment(forumRepository),
            updateComment = UpdateComment(forumRepository),
            deleteComment = DeleteComment(forumRepository),
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

    @Provides
    @Singleton
    fun provideCartUsecase(cartRepository: CartRepository): CartUsecase {
        return CartUsecase(
            getCart = GetCart(cartRepository),
            addToCart = AddToCart(cartRepository),
            removeFromCart = RemoveFromCart(cartRepository),
            clearCart = ClearCart(cartRepository)
        )
    }

    @Provides
    @Singleton
    fun provideOrderUsecase(orderRepository: OrderRepository, vnPayRepository: VNPayRepository): OrderUsecase {
        return OrderUsecase(
            createOrder = CreateOrder(orderRepository),
            paymentProgress = PaymentProgress(orderRepository),
            getOrderById = GetOrderById(orderRepository),
            getOrder = GetOrder(orderRepository),
            paymentFreeOrder = PaymentFreeOrder(orderRepository),
            vnpayCreatePaymentUrl = VNPayCreatePaymentUrl(vnPayRepository),
            queryTransaction = QueryTransaction(vnPayRepository)
        )
    }

    @Provides
    @Singleton
    fun provideUserUsecase(userRepository: UserRepository): UserUsecase {
        return UserUsecase(
            getCurrentUser = GetCurrentUser(userRepository),
            getProfile = GetProfile(userRepository),
            updateProfile = UpdateProfile(userRepository),
            uploadAvatar = UploadAvatar(userRepository)
        )
    }

    @Provides
    @Singleton
    fun provideLessonDiscussionUsecase(
        lessonDiscussionRepository: LessonDiscussionRepository
    ): LessonDiscussionUsecase {
        return LessonDiscussionUsecase(
            getLessonDiscussions = GetLessonDiscussions(lessonDiscussionRepository),
            getLessonDiscussionById = GetLessonDiscussionById(lessonDiscussionRepository),
            createLessonDiscussion = CreateLessonDiscussion(lessonDiscussionRepository),
            updateLessonDiscussion = UpdateLessonDiscussion(lessonDiscussionRepository),
            deleteLessonDiscussion = DeleteLessonDiscussion(lessonDiscussionRepository),
            pinLessonDiscussion = PinLessonDiscussion(lessonDiscussionRepository),
            unpinLessonDiscussion = UnpinLessonDiscussion(lessonDiscussionRepository),
            resolveLessonDiscussion = ResolveLessonDiscussion(lessonDiscussionRepository),
            unresolveLessonDiscussion = UnresolveLessonDiscussion(lessonDiscussionRepository),
            getLessonDiscussionReplies = GetLessonDiscussionReplies(lessonDiscussionRepository),
            createLessonDiscussionReply = CreateLessonDiscussionReply(lessonDiscussionRepository),
            updateLessonDiscussionReply = UpdateLessonDiscussionReply(lessonDiscussionRepository),
            deleteLessonDiscussionReply = DeleteLessonDiscussionReply(lessonDiscussionRepository),
            acceptLessonDiscussionReply = AcceptLessonDiscussionReply(lessonDiscussionRepository),
            voteLessonDiscussion = VoteLessonDiscussion(lessonDiscussionRepository),
            voteLessonDiscussionReply = VoteLessonDiscussionReply(lessonDiscussionRepository)
        )
    }

    @Provides
    @Singleton
    fun provideWishlistUsecase(
        wishlistRepository: WishlistRepository
    ): WishlistUsecase {
        return WishlistUsecase(
            getWishlist = GetWishlist(wishlistRepository),
            addToWishlist = AddToWishlist(wishlistRepository),
            removeFromWishlist = RemoveFromWishlist(wishlistRepository),
            removeItemFromWishlist = RemoveItemFromWishlist(wishlistRepository),
            isInWishlist = IsInWishlist(wishlistRepository),
            clearWishlist = ClearWishlist(wishlistRepository)
        )
    }
}

