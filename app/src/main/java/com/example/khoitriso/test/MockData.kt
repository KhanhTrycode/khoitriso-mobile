package com.example.khoitriso.test

import com.example.khoitriso.domain.models.Assignment
import com.example.khoitriso.domain.models.Author
import com.example.khoitriso.domain.models.Authorization
import com.example.khoitriso.domain.models.Book
import com.example.khoitriso.domain.models.BookDetail
import com.example.khoitriso.domain.models.CartItem
import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.models.Chapter
import com.example.khoitriso.domain.models.Coupon
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.CourseDetail
import com.example.khoitriso.domain.models.ForumAnswer
import com.example.khoitriso.domain.models.ForumAttachment
import com.example.khoitriso.domain.models.ForumCategory
import com.example.khoitriso.domain.models.ForumComment
import com.example.khoitriso.domain.models.ForumQuestion
import com.example.khoitriso.domain.models.ForumStats
import com.example.khoitriso.domain.models.ForumTag
import com.example.khoitriso.domain.models.Instructor
import com.example.khoitriso.domain.models.OrderItem
import com.example.khoitriso.domain.models.LearningPath
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.models.Material
import com.example.khoitriso.domain.models.MyBook
import com.example.khoitriso.domain.models.MyCart
import com.example.khoitriso.domain.models.MyCourse
import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Notification
import com.example.khoitriso.domain.models.Option
import com.example.khoitriso.domain.models.Order
import com.example.khoitriso.domain.models.Question
import com.example.khoitriso.domain.models.User
import com.example.khoitriso.utils.ItemType
import com.example.khoitriso.utils.QuestionType
import java.time.LocalDateTime
import kotlin.text.format

object MockData {

    // 2. Mock Data
    val mockQuestionList: List<Question> = listOf(
        // Question 1: Câu hỏi hướng dẫn (ID 295)
        Question(
            id = 295,
            contextType = 6,
            contextId = 19,
            questionContent = "Phần 1: Thí sinh trả lời từ câu 1 đến câu 12. Mỗi câu hỏi thí sinh chỉ chọn một phương án.",
            questionType = 3,
            difficultyLevel = 0,
            defaultPoints = 0.25,
            orderIndex = 0,
            isActive = true,
            options = emptyList(),
            createdAt = "2025-11-18T17:20:59.017291Z",
            updatedAt = "" // JSON ghi nhận là null, dùng ""
        ),

        // Question 2: Câu hỏi trắc nghiệm có công thức (ID 296)
        Question(
            id = 296,
            contextType = 6,
            contextId = 19,
            questionContent = """Cho hàm số <math xmlns="http://www.w3.org/1998/Math/MathML"><mi>y</mi><mo>=</mo><mfrac><mrow><mstyle><msup><mrow><mstyle><mi>x</mi></mstyle></mrow><mrow><mstyle><mn>2</mn></mstyle></mrow></msup><mo>−</mo><mn>2</mn><mi>x</mi><mo>+</mo><mn>4</mn></mstyle></mrow><mrow><mstyle><mi>x</mi><mo>−</mo><mn>2</mn></mstyle></mrow></mfrac></math>. Hàm số đã cho đồng biến trên khoảng nào sau đây?""",
            questionType = 0,
            difficultyLevel = 0,
            defaultPoints = 0.5,
            orderIndex = 0,
            isActive = true,
            options = listOf(
                Option(
                    id = 850,
                    questionId = 296,
                    optionText = """<math xmlns="http://www.w3.org/1998/Math/MathML"><mo>(</mo><mn>0</mn><mo>;</mo><mn>4</mn><mo>)</mo></math>""",
                    orderIndex = 0,
                    pointsValue = 0
                ),
                Option(
                    id = 851,
                    questionId = 296,
                    optionText = """<math xmlns="http://www.w3.org/1998/Math/MathML"><mo>(</mo><mn>2</mn><mo>;</mo><mn>4</mn><mo>)</mo></math>""",
                    orderIndex = 1,
                    pointsValue = 0
                ),
                Option(
                    id = 852,
                    questionId = 296,
                    optionText = """<math xmlns="http://www.w3.org/1998/Math/MathML"><mo>(</mo><mn>2</mn><mo>;</mo><mo>+</mo><mi>∞</mi><mo>)</mo></math>""",
                    orderIndex = 2,
                    pointsValue = 0
                ),
                Option(
                    id = 853,
                    questionId = 296,
                    optionText = """<math xmlns="http://www.w3.org/1998/Math/MathML"><mo>(</mo><mo>−</mo><mi>∞</mi><mo>;</mo><mn>0</mn><mo>)</mo></math>""",
                    orderIndex = 3,
                    pointsValue = 0
                )
            ),
            createdAt = "2025-11-18T17:20:59.017382Z",
            updatedAt = "2025-11-18T17:32:37.34435Z"
        ),

        // Question 3: Câu hỏi trắc nghiệm có công thức (ID 297)
        Question(
            id = 297,
            contextType = 6,
            contextId = 19,
            questionContent = """Cho hàm số <math xmlns="http://www.w3.org/1998/Math/MathML"><mi>y</mi><mo>=</mo><mi>f</mi><mo>(</mo><mi>x</mi><mo>)</mo></math> có đạo hàm <math xmlns="http://www.w3.org/1998/Math/MathML"><msup><mrow><mi>f</mi></mrow><mrow><mo>′</mo></mrow></msup><mo>(</mo><mi>x</mi><mo>)</mo><mo>=</mo><msup><mrow><mi>x</mi></mrow><mrow><mn>2</mn></mrow></msup><mo>(</mo><mi>x</mi><mo>−</mo><mn>1</mn><mo>)</mo><mo>(</mo><mi>x</mi><mo>−</mo><mn>2</mn><mo>)</mo><mo>,</mo><mo>∀</mo><mi>x</mi><mo>∈</mo><mi>R</mi></math>. Hàm số <math xmlns="http://www.w3.org/1998/Math/MathML"><mi>y</mi><mo>=</mo><mi>f</mi><mo>(</mo><mi>x</mi><mo>)</mo></math> đạt cực tiểu tại điểm nào?""",
            questionType = 0,
            difficultyLevel = 0,
            defaultPoints = 0.5,
            orderIndex = 0,
            isActive = true,
            options = listOf(
                Option(
                    id = 854,
                    questionId = 297,
                    optionText = """<math xmlns="http://www.w3.org/1998/Math/MathML"><mi>x</mi><mo>=</mo><mn>0</mn></math>""",
                    orderIndex = 0,
                    pointsValue = 0
                ),
                Option(
                    id = 855,
                    questionId = 297,
                    optionText = """<math xmlns="http://www.w3.org/1998/Math/MathML"><mi>x</mi><mo>=</mo><mn>1</mn></math>""",
                    orderIndex = 1,
                    pointsValue = 0
                ),
                Option(
                    id = 856,
                    questionId = 297,
                    optionText = """<math xmlns="http://www.w3.org/1998/Math/MathML"><mi>x</mi><mo>=</mo><mn>2</mn></math>""",
                    orderIndex = 2,
                    pointsValue = 0
                ),
                Option(
                    id = 857,
                    questionId = 297,
                    optionText = "Không có điểm cực tiểu",
                    orderIndex = 3,
                    pointsValue = 0
                )
            ),
            createdAt = "2025-11-18T17:20:59.017382Z",
            updatedAt = "" // JSON ghi nhận là null, dùng ""
        )
    )

    // Mock Users
    val mockUser1 = User(
        authProvider = "google",
        avatar = "https://i.pravatar.cc/150?img=1",
        email = "nguyen.van.a@example.com",
        fullName = "Nguyễn Văn A",
        id = 1,
        role = 1
    )

    val mockUser2 = User(
        authProvider = "facebook",
        avatar = "https://i.pravatar.cc/150?img=2",
        email = "tran.thi.b@example.com",
        fullName = "Trần Thị B",
        id = 2,
        role = 2
    )

    val mockUser3 = User(
        authProvider = "email",
        avatar = "https://i.pravatar.cc/150?img=3",
        email = "le.van.c@example.com",
        fullName = "Lê Văn C",
        id = 3,
        role = 1
    )

    val mockUsers = listOf(mockUser1, mockUser2, mockUser3)
    // Mock Assignments
    val mockAssignment1 = Assignment(
        description = "Bài tập về cú pháp cơ bản Kotlin",
        dueDate = "2024-12-01T23:59:59Z",
        id = 1,
        isPublished = true,
        lessonId = 1,
        maxAttempts = 3,
        maxScore = 100,
        passingScore = 70,
        questions =mockQuestionList ,
        showAnswersAfter = 1,
        shuffleOptions = true,
        shuffleQuestions = false,
        timeLimit = 3600,
        title = "Bài tập 1: Cú pháp Kotlin",
        userAttempts = listOf(mockUser1)
    )

    val mockAssignment2 = Assignment(
        description = "Bài tập về Functions và Lambdas",
        dueDate = "2024-12-10T23:59:59Z",
        id = 2,
        isPublished = true,
        lessonId = 3,
        maxAttempts = 2,
        maxScore = 100,
        passingScore = 75,
        questions = mockQuestionList,
        showAnswersAfter = 2,
        shuffleOptions = true,
        shuffleQuestions = true,
        timeLimit = 2700,
        title = "Bài tập 2: Functions",
        userAttempts = emptyList()
    )



    // Mock Authors
    val mockAuthor1 = Author(
        avatar = "https://i.pravatar.cc/150?img=10",
        fullName = "Nguyễn Nhật Ánh",
        id = 1
    )

    val mockAuthor2 = Author(
        avatar = "https://i.pravatar.cc/150?img=11",
        fullName = "Ngô Tất Tố",
        id = 2
    )

    val mockAuthor3 = Author(
        avatar = "https://i.pravatar.cc/150?img=12",
        fullName = "Nam Cao",
        id = 3
    )

    val mockAuthors = listOf(mockAuthor1, mockAuthor2, mockAuthor3)

    // Mock Categories
    val mockCategory1 = Category(
        id = 1,
        name = "Lập trình",
        description = "Các khóa học và sách về lập trình",
        icon = "code",
        isActive = true,
        orderIndex = 1,
        parent = null
    )

    val mockCategory2 = Category(
        id = 2,
        name = "Văn học",
        description = "Sách văn học Việt Nam và thế giới",
        icon = "book",
        isActive = true,
        orderIndex = 2,
        parent = null
    )

    val mockCategory3 = Category(
        id = 3,
        name = "Web Development",
        description = "Phát triển web frontend và backend",
        icon = "web",
        isActive = true,
        orderIndex = 3,
        parent = mockCategory1
    )

    val mockCategory4 = Category(
        id = 4,
        name = "Mobile Development",
        description = "Phát triển ứng dụng di động",
        icon = "mobile",
        isActive = true,
        orderIndex = 4,
        parent = mockCategory1
    )

    val mockCategories = listOf(mockCategory1, mockCategory2, mockCategory3, mockCategory4)

    // Mock Instructors
    val mockInstructor1 = Instructor(
        avatar = "https://i.pravatar.cc/150?img=20",
        bio = "Giảng viên có 10 năm kinh nghiệm trong lĩnh vực lập trình",
        id = 1,
        name = "Trần Minh Tuấn"
    )

    val mockInstructor2 = Instructor(
        avatar = "https://i.pravatar.cc/150?img=21",
        bio = "Chuyên gia về phát triển ứng dụng di động",
        id = 2,
        name = "Phạm Thu Hương"
    )

    val mockInstructor3 = Instructor(
        avatar = "https://i.pravatar.cc/150?img=22",
        bio = "Full-stack developer với nhiều dự án thực tế",
        id = 3,
        name = "Lê Hoàng Nam"
    )

    val mockInstructors = listOf(mockInstructor1, mockInstructor2, mockInstructor3)

    // Mock Materials
    val mockMaterial1 = Material(
        downloadCount = 150,
        fileName = "slide_bai_1.pdf",
        filePath = "/materials/course1/",
        fileSize = 2048000,
        fileType = "pdf",
        fileUrl = "https://example.com/materials/slide_bai_1.pdf",
        id = 1,
        lessonId = 1,
        title = "Slide bài giảng 1",
        updatedAt = "2024-11-20T10:30:00Z"
    )

    val mockMaterial2 = Material(
        downloadCount = 89,
        fileName = "source_code.zip",
        filePath = "/materials/course1/",
        fileSize = 5120000,
        fileType = "zip",
        fileUrl = "https://example.com/materials/source_code.zip",
        id = 2,
        lessonId = 1,
        title = "Source code mẫu",
        updatedAt = "2024-11-21T14:20:00Z"
    )

    val mockMaterials = listOf(mockMaterial1, mockMaterial2)


    val mockAssignments = listOf(mockAssignment1, mockAssignment2)

    // Mock Lessons
    val mockLesson1 = Lesson(
        contentText = "Giới thiệu về Kotlin và cài đặt môi trường",
        courseId = 1,
        description = "Giới thiệu về Kotlin và cài đặt môi trường",
        id = 1,
        isFree = true,
        isPublished = true,
        lessonOrder = 1,
        materials = listOf(mockMaterial1, mockMaterial2),
        title = "Bài 1: Giới thiệu Kotlin",
        userProgress = null,
        videoDuration = 1800,
        videoUrl = "https://example.com/videos/kotlin_intro.mp4",
        assignments = mockAssignments
    )

    val mockLesson2 = Lesson(
        contentText = "Giới thiệu về Kotlin và cài đặt môi trường",
        courseId = 1,
        description = "Các kiểu dữ liệu cơ bản trong Kotlin",
        id = 2,
        isFree = false,
        isPublished = true,
        lessonOrder = 2,
        materials = emptyList(),
        title = "Bài 2: Kiểu dữ liệu",
        userProgress = null,
        videoDuration = 2400,
        videoUrl = "https://example.com/videos/kotlin_datatypes.mp4",
        assignments = mockAssignments
    )

    val mockLesson3 = Lesson(
        contentText = "Giới thiệu về Kotlin và cài đặt môi trường",
        courseId = 1,
        description = "Functions và lambdas trong Kotlin",
        id = 3,
        isFree = false,
        isPublished = true,
        lessonOrder = 3,
        materials = emptyList(),
        title = "Bài 3: Functions",
        userProgress = null,
        videoDuration = 3000,
        videoUrl = "https://example.com/videos/kotlin_functions.mp4",
        assignments = mockAssignments
    )

    val mockLessons = listOf(mockLesson1, mockLesson2, mockLesson3)

    // Mock Chapters
    val mockChapter1 = Chapter(
        bookId = 1,
        createdAt = "2024-11-01T08:00:00Z",
        description = "Khái quát về lập trình hướng đối tượng",
        id = 1,
        orderIndex = 1,
        questionCount = mockQuestionList.size,
        questions = mockQuestionList,
        title = "Chương 1: Giới thiệu OOP",
        updatedAt = "2024-11-01T08:00:00Z"
    )

    val mockChapter2 = Chapter(
        bookId = 1,
        createdAt = "2024-11-02T08:00:00Z",
        description = "Tính kế thừa và đa hình",
        id = 2,
        orderIndex = 2,
        questionCount = mockQuestionList.size,
        questions = mockQuestionList,
        title = "Chương 2: Kế thừa và Đa hình",
        updatedAt = "2024-11-02T08:00:00Z"
    )

    val mockChapter3 = Chapter(
        bookId = 1,
        createdAt = "2024-11-03T08:00:00Z",
        description = "Tính đóng gói và trừu tượng",
        id = 3,
        orderIndex = 3,
        questionCount = 6,
        questions = emptyList(),
        title = "Chương 3: Đóng gói và Trừu tượng",
        updatedAt = "2024-11-03T08:00:00Z"
    )

    val mockChapters = listOf(mockChapter1, mockChapter2, mockChapter3)

    // Mock Books
    val mockBook1 = Book(
        approvalStatus = 1,
        author = mockAuthor1,
        category = mockCategory1,
        coverImage = "https://picsum.photos/300/400?random=1",
        createdAt = "2024-10-01T10:00:00Z",
        description = "Cuốn sách hướng dẫn lập trình Kotlin từ cơ bản đến nâng cao",
        edition = "Lần 1",
        id = 1,
        language = "Tiếng Việt",
        price = 150000.0,
        publicationYear = 2024,
        rating = 4.5f,
        title = "Lập trình Kotlin cho người mới bắt đầu",
        totalReviews = 125,
        updatedAt = "2024-11-20T10:00:00Z"
    )

    val mockBook2 = Book(
        approvalStatus = 1,
        author = mockAuthor2,
        category = mockCategory2,
        coverImage = "https://picsum.photos/300/400?random=2",
        createdAt = "2024-09-15T10:00:00Z",
        description = "Tác phẩm văn học nổi tiếng của Ngô Tất Tố",
        edition = "Lần 5",
        id = 2,
        language = "Tiếng Việt",
        price = 85000.0,
        publicationYear = 2023,
        rating = 4.8f,
        title = "Tắt Đèn",
        totalReviews = 320,
        updatedAt = "2024-10-15T10:00:00Z"
    )

    val mockBook3 = Book(
        approvalStatus = 1,
        author = mockAuthor3,
        category = mockCategory2,
        coverImage = "https://picsum.photos/300/400?random=3",
        createdAt = "2024-08-20T10:00:00Z",
        description = "Truyện ngắn nổi tiếng của Nam Cao",
        edition = "Lần 3",
        id = 3,
        language = "Tiếng Việt",
        price = 65000.0,
        publicationYear = 2023,
        rating = 4.7f,
        title = "Chí Phèo",
        totalReviews = 280,
        updatedAt = "2024-09-20T10:00:00Z"
    )

    val mockBooks = listOf(mockBook1, mockBook2, mockBook3)

    // Mock BookDetail
    val mockBookDetail1 = BookDetail(
        approvalStatus = 1,
        author = mockAuthor1,
        category = mockCategory1,
        chapters = mockChapters,
        coverImage = "https://picsum.photos/300/400?random=1",
        createdAt = "2024-10-01T10:00:00Z",
        description = "Cuốn sách hướng dẫn lập trình Kotlin từ cơ bản đến nâng cao với nhiều ví dụ thực tế",
        ebookFile = "https://example.com/ebooks/kotlin_beginner.pdf",
        edition = "Lần 1",
        id = 1,
        isOwned = true,
        isbn = "978-604-2-24567-8",
        language = "Tiếng Việt",
        price = 150000.0,
        publicationYear = 2024,
        rating = 4.5f,
        reviewNotes = null,
        staticPagePath = "/static/books/1",
        title = "Lập trình Kotlin cho người mới bắt đầu",
        totalReviews = 125,
        updatedAt = "2024-11-20T10:00:00Z"
    )

    // Mock Courses
    val mockCourse1 = Course(
        category = mockCategory3,
        createdAt = "2024-09-01T08:00:00Z",
        description = "Khóa học Kotlin toàn diện cho Android Development",
        estimatedDuration = 40,
        id = 1,
        instructor = mockInstructor1,
        isFree = false,
        level = 1,
        price = 299000.0,
        rating = 4.6f,
        thumbnail = "https://picsum.photos/400/300?random=11",
        title = "Kotlin Android Development",
        totalLessons = 45,
        totalReviews = 189,
        totalStudents = 1250
    )

    val mockCourse2 = Course(
        category = mockCategory3,
        createdAt = "2024-08-15T08:00:00Z",
        description = "Học React từ cơ bản đến nâng cao",
        estimatedDuration = 35,
        id = 2,
        instructor = mockInstructor2,
        isFree = false,
        level = 2,
        price = 399000.0,
        rating = 4.8f,
        thumbnail = "https://picsum.photos/400/300?random=12",
        title = "ReactJS Từ A-Z",
        totalLessons = 60,
        totalReviews = 340,
        totalStudents = 2100
    )

    val mockCourse3 = Course(
        category = mockCategory4,
        createdAt = "2024-10-10T08:00:00Z",
        description = "Khóa học miễn phí về cơ bản Flutter",
        estimatedDuration = 20,
        id = 3,
        instructor = mockInstructor3,
        isFree = true,
        level = 1,
        price = 0.0,
        rating = 4.3f,
        thumbnail = "https://picsum.photos/400/300?random=13",
        title = "Flutter Cơ Bản",
        totalLessons = 25,
        totalReviews = 95,
        totalStudents = 850
    )

    val mockCourses = listOf(mockCourse1, mockCourse2, mockCourse3)

    val mockEnrolledCourse1 = Course(
        id = 101,
        title = "Khóa học Lập trình Android Jetpack Compose",
        description = "Học cách xây dựng ứng dụng Android hiện đại với Jetpack Compose.",
        thumbnail = "https://example.com/thumbnail_compose.png",
        instructor = mockInstructor1,
        category = mockCategory3,
        level = 2,
        isFree = false,
        price = 500000.0,
        rating = 4.8f,
        totalReviews = 120,
        totalStudents = 1500,
        createdAt = "2025-01-01T00:00:00Z",
        estimatedDuration = 20,
        totalLessons = 50
    )

    val mockEnrolledCourse2 = Course(
        id = 102,
        title = "Khóa học Thiết kế UI/UX cho người mới bắt đầu",
        description = "Các nguyên tắc cơ bản về thiết kế giao diện và trải nghiệm người dùng.",
        thumbnail = "https://example.com/thumbnail_uiux.png",
        instructor = mockInstructor1,
        category = mockCategory3,
        level = 1,
        isFree = false,
        price = 300000.0,
        rating = 4.9f,
        totalReviews = 250,
        totalStudents = 3000,
        createdAt = "2025-02-15T00:00:00Z",
        estimatedDuration = 15,
        totalLessons = 30
    )


    val mockMyBookInProgress = MyBook(
        bookId = 201,
        book = mockBook1,
        activatedAt = "2025-11-01T10:00:00Z",
        totalChapters = 20,
        completedChapters = 8 // Đã đọc 8/20 chương
    )

    // Ví dụ về một cuốn sách đã đọc xong
    val mockMyBookCompleted = MyBook(
        bookId = 202,
        book = mockBook2,
        activatedAt = "2025-10-20T15:30:00Z",
        totalChapters = 15,
        completedChapters = 15 // Đã đọc hết 15/15 chương
    )

    // Ví dụ về một cuốn sách mới kích hoạt, chưa đọc
    val mockMyBookNew = MyBook(
        bookId = 203, // Giả sử có một cuốn sách khác
        book = mockBook3,
        activatedAt = "", // Lấy thời gian hiện tại
        totalChapters = 25,
        completedChapters = 0 // Chưa đọc chương nào
    )


    // 3. Tạo một danh sách các cuốn sách của tôi
    val mockMyBooksList = listOf(mockMyBookInProgress, mockMyBookCompleted, mockMyBookNew)



// 2. Tạo các đối tượng MyCourse giả lập

    // Ví dụ về một khóa học đang học dở
    val mockMyCourseInProgress = MyCourse(
        courseId = 101,
        course = mockEnrolledCourse1,
        progressPercentage = 45.5,
        enrolledAt = "2025-10-15T09:30:00Z",
        lastAccessed = "2025-11-26T14:00:00Z",
        isCompleted = false,
        completedAt = "" // Chưa hoàn thành nên là null
    )

    // Ví dụ về một khóa học đã hoàn thành
    val mockMyCourseCompleted = MyCourse(
        courseId = 102,
        course = mockEnrolledCourse2,
        progressPercentage = 100.0,
        enrolledAt = "2025-09-01T18:00:00Z",
        lastAccessed = "2025-11-20T11:25:00Z",
        isCompleted = true,
        completedAt = "2025-11-20T11:25:00Z" // Đã hoàn thành
    )

    // 3. Tạo một danh sách các khóa học của tôi
    val mockMyCoursesList = listOf(mockMyCourseInProgress, mockMyCourseCompleted)

    // Mock CourseDetail
    val mockCourseDetail1 = CourseDetail(
        category = mockCategory3,
        createdAt = "2024-09-01T08:00:00Z",
        description = "Khóa học Kotlin toàn diện cho Android Development với các dự án thực tế",
        id = 1,
        instructor = mockInstructor1,
        isEnrolled = true,
        isFree = false,
        lessons = mockLessons,
        level = 1,
        price = 299000.0,
        rating = 4.6f,
        requirements = listOf(
            "Kiến thức cơ bản về lập trình",
            "Biết sử dụng Android Studio",
            "Có máy tính cài đặt được Android Studio"
        ),
        thumbnail = "https://picsum.photos/400/300?random=11",
        title = "Kotlin Android Development",
        totalReviews = 189,
        totalStudents = 1250,
        updatedAt = "2024-11-22T08:00:00Z",
        whatYouWillLearn = listOf(
            "Nắm vững ngôn ngữ lập trình Kotlin",
            "Xây dựng ứng dụng Android hoàn chỉnh",
            "Hiểu rõ kiến trúc MVVM",
            "Sử dụng Jetpack Compose",
            "Kết nối API và xử lý dữ liệu"
        )
    )

    // Mock Coupons
    val mockCoupon1 = Coupon(
        applicableItemIds = listOf(1, 2, 3),
        applicableItemTypes = listOf(1, 2),
        code = "BLACKFRIDAY2024",
        description = "Giảm giá Black Friday",
        discountType = 1,
        discountTypeName = "Percentage",
        discountValue = 30,
        id = 1,
        isActive = true,
        maxDiscountAmount = 200000,
        minOrderAmount = 500000,
        name = "Black Friday Sale",
        usageLimit = 1000,
        usedCount = 245,
        validFrom = "2024-11-20T00:00:00Z",
        validTo = "2024-11-30T23:59:59Z"
    )

    val mockCoupon2 = Coupon(
        applicableItemIds = emptyList(),
        applicableItemTypes = listOf(1),
        code = "NEWUSER50",
        description = "Giảm giá cho người dùng mới",
        discountType = 2,
        discountTypeName = "Fixed Amount",
        discountValue = 50000,
        id = 2,
        isActive = true,
        maxDiscountAmount = 50000,
        minOrderAmount = 100000,
        name = "New User Discount",
        usageLimit = 500,
        usedCount = 78,
        validFrom = "2024-11-01T00:00:00Z",
        validTo = "2024-12-31T23:59:59Z"
    )

    val mockCoupons = listOf(mockCoupon1, mockCoupon2)

    // Mock Items
    val mockItem1 = OrderItem(
        id = 1,
        itemId = 1,
        itemName = "Kotlin Android Development",
        itemType = 1,
        price = 299000.0,
        quantity = 1,
        subTotal = 299000
    )

    val mockItem2 = OrderItem(
        id = 2,
        itemId = 1,
        itemName = "Lập trình Kotlin cho người mới bắt đầu",
        itemType = 2,
        price = 150000.0,
        quantity = 1,
        subTotal = 150000
    )

    val mockItem3 = OrderItem(
        id = 3,
        itemId = 2,
        itemName = "ReactJS Từ A-Z",
        itemType = 1,
        price = 399000.0,
        quantity = 1,
        subTotal = 399000
    )

    val mockItems = listOf(mockItem1, mockItem2, mockItem3)

    // Mock Orders
    val mockOrder1 = Order(
        currency = "VND",
        discountAmount = 89700,
        exchangeRate = 1,
        finalAmount = 359300,
        id = 1,
        orderCode = "ORD20241120001",
        orderNotes = "Giao hàng vào giờ hành chính",
        paidAt = "2024-11-20T14:30:00Z",
        paymentGateway = "VNPay",
        paymentMethod = "ATM",
        status = 2,
        statusName = "Completed",
        taxAmount = 0,
        totalAmount = 449000,
        transactionId = "VNP20241120143000",
        userId = 1,
        coupon = mockCoupon1,
        items = listOf(mockItem1, mockItem2),
        createdAt = "2024-11-20T14:00:00Z"
    )

    val mockOrder2 = Order(
        currency = "VND",
        discountAmount = 0,
        exchangeRate = 1,
        finalAmount = 399000,
        id = 2,
        orderCode = "ORD20241121002",
        orderNotes = "",
        paidAt = "2024-11-21T09:15:00Z",
        paymentGateway = "MoMo",
        paymentMethod = "E-Wallet",
        status = 1,
        statusName = "Processing",
        taxAmount = 0,
        totalAmount = 399000,
        transactionId = "MOMO20241121091500",
        userId = 2,
        coupon = null,
        items = listOf(mockItem3),
        createdAt = "2024-11-21T09:00:00Z"
    )

    val mockOrders = listOf(mockOrder1, mockOrder2)

    // Mock Notifications
    val mockNotification1 = Notification(
        id = 1,
        userId = 1,
        title = "Khóa học mới được thêm vào",
        content = "Khóa học Kotlin Android Development đã được thêm vào thư viện",
        type = 2,
        priority = 1,
        isRead = false,
        actionUrl = "/courses/1",
        relatedId = "1",
        relatedType = "course",
        createdAt = "2024-11-24T10:00:00Z"
    )

    val mockNotification2 = Notification(
        id = 2,
        userId = 1,
        title = "Đơn hàng đã được xác nhận",
        content = "Đơn hàng #ORD20241120001 của bạn đã được xác nhận và đang xử lý",
        type = 5,
        priority = 2,
        isRead = true,
        actionUrl = "/orders/1",
        relatedId = "1",
        relatedType = "order",
        createdAt = "2024-11-20T14:35:00Z"
    )

    val mockNotification3 = Notification(
        id = 3,
        userId = 2,
        title = "Bài tập sắp đến hạn",
        content = "Bài tập 'Cú pháp Kotlin' sẽ đến hạn trong 2 ngày",
        type = 4,
        priority = 1,
        isRead = false,
        actionUrl = "/assignments/1",
        relatedId = "1",
        relatedType = "assignment",
        createdAt = "2024-11-23T08:00:00Z"
    )

    val mockNotifications = listOf(mockNotification1, mockNotification2, mockNotification3)

    // Mock Forum data
    val mockForumAttachment1 = ForumAttachment(
        fileName = "screenshot.png",
        fileUrl = "https://example.com/attachments/screenshot.png",
        fileSize = 1024000,
        fileType = "image/png"
    )

    val mockForumCategory1 = ForumCategory(
        id = "fc1",
        name = "Lập trình",
        description = "Thảo luận về lập trình",
        color = "#FF5722",
        icon = "code",
        isActive = true,
        sortOrder = 1
    )

    val mockForumCategory2 = ForumCategory(
        id = "fc2",
        name = "Học tập",
        description = "Chia sẻ kinh nghiệm học tập",
        color = "#2196F3",
        icon = "school",
        isActive = true,
        sortOrder = 2
    )

    val mockForumTag1 = ForumTag(
        id = "ft1",
        name = "kotlin",
        description = "Kotlin programming language",
        color = "#7F52FF",
        isActive = true
    )

    val mockForumTag2 = ForumTag(
        id = "ft2",
        name = "android",
        description = "Android development",
        color = "#3DDC84",
        isActive = true
    )

    val mockForumComment1 = ForumComment(
        id = "fc1",
        parentId = "fq1",
        parentType = 1,
        content = "Câu hỏi hay, tôi cũng đang tìm hiểu về vấn đề này",
        userId = 2,
        userName = "Trần Thị B",
        userAvatar = "https://i.pravatar.cc/150?img=2",
        createdAt = "2024-11-23T11:00:00Z",
        updatedAt = "null"
    )

    val mockForumComment2 = ForumComment(
        id = "fc2",
        parentId = "fq1",
        parentType = 1,
        content = "tôi cũng đang tìm hiểu về vấn đề này",
        userId = 2,
        userName = "Trần Thị B",
        userAvatar = "https://i.pravatar.cc/150?img=2",
        createdAt = "2024-11-23T11:00:00Z",
        updatedAt = "null"
    )

    val mockForumComments = mutableListOf(mockForumComment1, mockForumComment2)


    val mockForumAnswer1 = ForumAnswer(
        id = "fa1",
        questionId = "fq1",
        content = "Để sử dụng coroutines trong Kotlin, bạn cần thêm dependency kotlinx-coroutines-core vào build.gradle. Sau đó bạn có thể sử dụng launch hoặc async để tạo coroutine scope.",
        userId = 3,
        userName = "Lê Văn C",
        userAvatar = "https://i.pravatar.cc/150?img=3",
        isAccepted = true,
        isDeleted = false,
        voteCount = 15,
        commentCount = 2,
        createdAt = "2024-11-23T12:00:00Z",
        updatedAt = "null",
        attachments = emptyList()
    )

    val mockForumQuestion1 = ForumQuestion(
        id = "fq1",
        title = "Làm thế nào để sử dụng Coroutines trong Kotlin?",
        content = "Tôi đang học Kotlin và muốn hiểu rõ hơn về Coroutines. Có ai có thể giải thích và cho ví dụ cụ thể không?",
        userId = 1,
        userName = "Nguyễn Văn A",
        userAvatar = "https://i.pravatar.cc/150?img=1",
        categoryId = "fc1",
        categoryName = "Lập trình",
        tags = listOf("kotlin", "coroutines", "android"),
        isSolved = true,
        isPinned = false,
        isClosed = false,
        isDeleted = false,
        viewCount = 245,
        voteCount = 12,
        answerCount = 3,
        acceptedAnswerId = "fa1",
        createdAt = "2024-11-23T10:00:00Z",
        updatedAt = "2024-11-23T14:30:00Z",
        lastActivityAt = "2024-11-23T14:30:00Z",
        attachments = listOf(mockForumAttachment1)
    )

    val mockForumQuestion2 = ForumQuestion(
        id = "fq2",
        title = "ReactJS vs VueJS - Nên học cái nào?",
        content = "Mình đang muốn học frontend framework, không biết nên chọn ReactJS hay VueJS. Mọi người có thể tư vấn giúp mình không?",
        userId = 2,
        userName = "Trần Thị B",
        userAvatar = "https://i.pravatar.cc/150?img=2",
        categoryId = "fc1",
        categoryName = "Lập trình",
        tags = listOf("reactjs", "vuejs", "frontend"),
        isSolved = false,
        isPinned = true,
        isClosed = false,
        isDeleted = false,
        viewCount = 568,
        voteCount = 25,
        answerCount = 8,
        acceptedAnswerId = "null",
        createdAt = "2024-11-22T15:00:00Z",
        updatedAt = "2024-11-24T09:00:00Z",
        lastActivityAt = "2024-11-24T09:00:00Z",
        attachments = emptyList()
    )

    val mockForumStats = ForumStats(
        totalQuestions = 1250,
        totalAnswers = 4560,
        totalUsers = 890,
        solvedQuestions = 980
    )

    val mockForumQuestions = listOf(mockForumQuestion1, mockForumQuestion2)
    val mockForumAnswers = mutableListOf(mockForumAnswer1)
    val mockForumCategories = listOf(mockForumCategory1, mockForumCategory2)
    val mockForumTags = listOf(mockForumTag1, mockForumTag2)

    // Mock LearningPath
    val mockLearningPath1 = LearningPath(
        approvalStatus = 1,
        approvalStatusName = "Approved",
        category = mockCategory1,
        categoryId = 1,
        courseCount = 3,
        courses = listOf(mockCourse1, mockCourse2, mockCourse3),
        createdAt = "2024-10-01T08:00:00Z",
        description = "Lộ trình học lập trình Android từ cơ bản đến nâng cao",
        difficultyLevel = 2,
        difficultyLevelName = "Intermediate",
        enrollmentCount = 456,
        estimatedDuration = 120,
        id = 1,
        instructor = mockInstructor1,
        instructorId = 1,
        isActive = true,
        isEnrolled = true,
        isPublished = true,
        price = 999000,
        qualityScore = 4.7f,
        reviewNotes = "Lộ trình rất chất lượng",
        thumbnail = "https://picsum.photos/400/300?random=20",
        title = "Lộ trình Android Developer",
        updatedAt = "2024-11-20T08:00:00Z"
    )

    val mockLearningPath2 = LearningPath(
        approvalStatus = 1,
        approvalStatusName = "Approved",
        category = mockCategory3,
        categoryId = 3,
        courseCount = 2,
        courses = listOf(mockCourse2),
        createdAt = "2024-09-15T08:00:00Z",
        description = "Lộ trình học Full-stack Web Development",
        difficultyLevel = 3,
        difficultyLevelName = "Advanced",
        enrollmentCount = 289,
        estimatedDuration = 180,
        id = 2,
        instructor = mockInstructor3,
        instructorId = 3,
        isActive = true,
        isEnrolled = false,
        isPublished = true,
        price = 1499000,
        qualityScore = 4.8f,
        reviewNotes = "",
        thumbnail = "https://picsum.photos/400/300?random=21",
        title = "Lộ trình Full-stack Developer",
        updatedAt = "2024-11-15T08:00:00Z"
    )

    val mockLearningPaths = listOf(mockLearningPath1, mockLearningPath2)

    // Mock Authorization
    val mockAuthorization = Authorization(
        accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6Ik5ndXnhu4VuIFbEg24gQSIsImlhdCI6MTUxNjIzOTAyMn0.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c",
        refresh = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlJlZnJlc2ggVG9rZW4iLCJpYXQiOjE1MTYyMzkwMjJ9.4Adcj0vnY3T9V3dXEhIaZjQZxVwb2jMTQW8RmQE8mmo"
    )

    // Mock Response with pagination
    val mockBookResponse = MyResponese(
        items = mockBooks,
        page = 1,
        pageSize = 10,
        total = 50,
        totalPages = 5
    )

    val mockCourseResponse = MyResponese(
        items = mockCourses,
        page = 1,
        pageSize = 10,
        total = 30,
        totalPages = 3
    )

    var mockCartItems : MutableList<CartItem> = emptyList<CartItem>().toMutableList()
    // Mock Cart
    val mockCart = Carts(
        cartItems = mockCartItems,
        totalItems = mockCartItems.size,
        totalPrice = 0.0
    )
}
