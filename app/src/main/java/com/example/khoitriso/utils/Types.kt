package com.example.khoitriso.utils

enum class QuestionType(val value: Int) {
    MultipleChoice(0),
    TrueFalse(1),
    ShortAnswer(2),
    GroupType(3),
    Lesson(4),
    Book(5);

    companion object {
        fun fromInt(value: Int): QuestionType? = entries.find { it.value == value }
    }
}

enum class ContextType(val value: Int) {
    Lesson(1),
    Book(2);

    companion object {
        fun fromInt(value: Int): ContextType? = entries.find { it.value == value }
    }
}

enum class OrderStatus(val value: Int, val displayName: String) {
    Pending(0, "Đang chờ"),
    Paid(1, "Đã thanh toán"),
    Cancelled(2, "Đã hủy"),
    Refunded(3, "Đã hoàn tiền");

    companion object {
        fun fromInt(value: Int): OrderStatus? = entries.find { it.value == value }
    }
}

enum class ShowAnswersAfter(val value: Int) {
    Immediately(0),
    AfterDue(1),
    Never(2);

    companion object {
        fun fromInt(value: Int): ShowAnswersAfter? = entries.find { it.value == value }
    }
}

enum class LiveStatus(val value: Int) {
    Scheduled(0),
    Live(1),
    Completed(2),
    Cancelled(3);

    companion object {
        fun fromInt(value: Int): LiveStatus? = entries.find { it.value == value }
    }
}

enum class NotificationType(val value: Int, val displayName: String) {
    System(1, "Thông báo hệ thống"),
    Course(2, "Khóa học"),
    Lesson(3, "Bài học"),
    Assignment(4, "Bài tập"),
    Order(5, "Đơn hàng"),
    Payment(6, "Thanh toán"),
    Certificate(7, "Chứng chỉ"),
    Forum(8, "Diễn đàn"),
    Review(9, "Đánh giá"),
    Announcement(10, "Thông báo"),
    LiveClass(11, "Lớp học trực tiếp"),
    LearningPath(12, "Lộ trình học tập"),
    Book(13, "Sách"),
    Wishlist(14, "Yêu thích"),
    Coupon(15, "Mã giảm giá"),
    LessonDiscussion(16, "Thảo luận bài học"),
    ForumAnswer(17, "Câu trả lời Forum");

    companion object {
        fun fromInt(value: Int): NotificationType? = entries.find { it.value == value }
    }
}

val Int.asQuestionType: QuestionType?
    get() = QuestionType.fromInt(this)

val Int.asContextType: ContextType?
    get() = ContextType.fromInt(this)

val Int.asOrderStatus: OrderStatus?
    get() = OrderStatus.fromInt(this)

val Int.asShowAnswersAfter: ShowAnswersAfter?
    get() = ShowAnswersAfter.fromInt(this)

val Int.asLiveStatus: LiveStatus?
    get() = LiveStatus.fromInt(this)

val Int?.asNotificationType: NotificationType?
    get() = this?.let { NotificationType.fromInt(it) }

