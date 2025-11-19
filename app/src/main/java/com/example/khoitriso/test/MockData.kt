package com.example.khoitriso.test

import com.example.khoitriso.R
import com.example.khoitriso.domain.models.*

object MockData {

    // ---------------------------
    // Authors (12 total)
    // ---------------------------
    val authors = listOf(
        Author("https://example.com/a1.png", "Nguyễn Văn A", 1),
        Author("https://example.com/a2.png", "Trần Thị B", 2),

        Author("https://example.com/a3.png", "Phạm Đức C", 3),
        Author("https://example.com/a4.png", "Hoàng Mai D", 4),
        Author("https://example.com/a5.png", "Lê Thanh E", 5),
        Author("https://example.com/a6.png", "Trịnh Quốc F", 6),
        Author("https://example.com/a7.png", "Đỗ Ngọc G", 7),
        Author("https://example.com/a8.png", "Trần Hải H", 8),
        Author("https://example.com/a9.png", "Vũ Minh I", 9),
        Author("https://example.com/a10.png", "Đặng Tú J", 10),
        Author("https://example.com/a11.png", "Lưu Thảo K", 11),
        Author("https://example.com/a12.png", "Nguyễn Minh L", 12),
    )

    // ---------------------------
    // Categories (13 total)
    // ---------------------------
    val categories = listOf(
        Category(1, "Programming", "Programming books and courses", "", true, 1, null),
        Category(2, "Design", "UI/UX and design", "", true, 2, null),
        Category(3, "Backend", "Server-side topics", "", true, 3, null),

        Category(4, "AI", "Artificial Intelligence", "", true, 4, null),
        Category(5, "Marketing", "Digital marketing", "", true, 5, null),
        Category(6, "Mobile", "Android/iOS Development", "", true, 6, null),
        Category(7, "Frontend", "React/Vue/Web", "", true, 7, null),
        Category(8, "DevOps", "DevOps & Cloud", "", true, 8, null),
        Category(9, "Data Science", "ML & Data Analysis", "", true, 9, null),
        Category(10, "Soft Skills", "Communication", "", true, 10, null),
        Category(11, "Security", "Cyber Security", "", true, 11, null),
        Category(12, "Database", "SQL, NoSQL", "", true, 12, null),
        Category(13, "Blockchain", "Web3 & Crypto", "", true, 13, null),
    )

    // ---------------------------
    // Instructors (13 total)
    // ---------------------------
    val instructors = listOf(
        Instructor("", "Giảng viên Android", 1, "Nguyên Văn A"),
        Instructor("", "Chuyên gia UI/UX", 2, "Trần Thị B"),
        Instructor("", "Backend Developer", 3, "Lê Văn C"),
        Instructor("", "AWS Cloud Engineer", 4, "Nguyễn Thành D"),
        Instructor("", "AI Engineer", 5, "Phạm Như E"),
        Instructor("", "KMM Specialist", 6, "Đặng Bá F"),
        Instructor("", "Game Developer", 7, "Hồ Mỹ G"),
        Instructor("", "Cyber Security Expert", 8, "Trần Duy H"),
        Instructor("", "Fullstack Dev", 9, "Lê Văn I"),
        Instructor("", "React Developer", 10, "Nguyễn Quốc K"),
        Instructor("", "UI/UX Designer", 11, "Phạm Linh L"),
        Instructor("", "Spring Boot Instructor", 12, "Vũ Trọng M"),
        Instructor("", "iOS Swift Developer", 13, "Đỗ Thành N"),
    )

    // ---------------------------
    // Users (12 total)
    // ---------------------------
    val users = listOf(
        User("local", "", "user1@example.com", "User One", 1, 1),
        User("google", "", "user2@example.com", "User Two", 2, 2),

        User("local", "", "user3@example.com", "User Three", 3, 1),
        User("local", "", "user4@example.com", "User Four", 4, 1),
        User("google", "", "user5@gmail.com", "User Five", 5, 2),
        User("facebook", "", "user6@fb.com", "User Six", 6, 1),
        User("local", "", "user7@example.com", "User Seven", 7, 2),
        User("google", "", "user8@gmail.com", "User Eight", 8, 2),
        User("local", "", "user9@example.com", "User Nine", 9, 1),
        User("local", "", "user10@example.com", "User Ten", 10, 1),
        User("google", "", "user11@gmail.com", "User Eleven", 11, 2),
        User("facebook", "", "user12@fb.com", "User Twelve", 12, 1),
    )

    // ---------------------------
    // Courses (12 total)
    // ---------------------------
    val mockCourses = listOf(
        Course(
            category = categories[0],
            description = "Try Test Course 1",
            estimatedDuration = 20,
            id = 1,
            instructor = instructors[0],
            lessons = listOf(
                Lesson(
                    0,
                    1,
                    "Giới thiệu khóa học: Course No1",
                    1001,
                    true,
                    true,
                    0,
                    emptyList(),
                    "Giới thiệu khóa học",
                    null,
                    60,
                    ""
                )
            ),
            isFree = true,
            level = 1,
            price = 0,
            rating = 4.5f,
            thumbnail = R.drawable.course_test.toString(),
            title = "Course No1",
            totalLessons = 10,
            totalReviews = 10,
            totalStudents = 100
        ),
        // It's good practice to ensure unique IDs
        Course(
            category = categories[1],
            description = "Try Test Course 2",
            estimatedDuration = 35,
            id = 2,
            instructor = instructors[1],
            lessons = listOf(
                Lesson(
                    0,
                    2,
                    "Giới thiệu khóa học: Course No2",
                    1002,
                    false,
                    true,
                    0,
                    emptyList(),
                    "Giới thiệu khóa học",
                    null,
                    60,
                    ""
                )
            ),
            isFree = false,
            level = 2,
            price = 50,
            rating = 4.7f,
            thumbnail = R.drawable.course_test.toString(),
            title = "Course No2",
            totalLessons = 15,
            totalReviews = 20,
            totalStudents = 200
        ),

        // Additional mock courses
        *Array(10) { index ->
            val id = index + 3
            val isFreeFlag = id % 2 == 0
            Course(
                category = categories[id % categories.size],
                description = "Mock Course $id description",
                estimatedDuration = 10 + id,
                id = id,
                instructor = instructors[id % instructors.size],
                lessons = listOf(
                    Lesson(
                        0,
                        id,
                        "Giới thiệu khóa học: Course No$id",
                        id * 1000 + 1,
                        isFreeFlag,
                        true,
                        0,
                        emptyList(),
                        "Giới thiệu khóa học",
                        null,
                        60,
                        ""
                    )
                ),
                isFree = isFreeFlag,
                level = (id % 3) + 1,
                price = if (isFreeFlag) 0 else 20 + id,
                rating = 4f + (id % 5) * 0.1f,
                thumbnail = R.drawable.course_test.toString(),
                title = "Course No$id",
                totalLessons = 5 + id,
                totalReviews = 10 + id,
                totalStudents = 100 * id
            )
        }
    )

    // ---------------------------
    // Lessons (12 total)
    // ---------------------------
    val lessons = listOf(
        Lesson(
            0,
            1,
            "Lesson 1 description",
            1,
            true,
            true,
            1,
            emptyList(),
            "Lesson 1",
            null,
            120,
            "https://example.com/video1.mp4"
        ),
        Lesson(
            0,
            1,
            "Lesson 2 description",
            2,
            false,
            true,
            2,
            emptyList(),
            "Lesson 2",
            null,
            90,
            "https://example.com/video2.mp4"
        ),
    ) + (3..12).map {
        Lesson(
            0,
            it % 3 + 1,
            "Lesson $it description",
            it,
            it % 2 == 0,
            true,
            it,
            emptyList(),
            "Lesson $it",
            null,
            60 + it * 2,
            "https://example.com/video$it.mp4"
        )
    }

    // ---------------------------
    // Chapters (12 total)
    // ---------------------------
    val chapters = listOf(
        Chapter(1, "2025-11-17", "Chapter 1", 1, 1, 0, emptyList(), "Chapter 1", "2025-11-17"),
        Chapter(1, "2025-11-17", "Chapter 2", 2, 2, 0, emptyList(), "Chapter 2", "2025-11-17"),
    ) + (3..12).map {
        Chapter(
            1,
            "2025-11-17",
            "Chapter $it description",
            it,
            it,
            0,
            emptyList(),
            "Chapter $it",
            "2025-11-17"
        )
    }

    // ---------------------------
    // Books (12 total)
    // ---------------------------
    val books = listOf(
        Book(
            1,
            authors[0],
            categories[0],
            "",
            "2025-11-17",
            "Book 1 description",
            "1st",
            1,
            true,
            "ISBN-001",
            "en",
            100,
            2025,
            4.5f,
            "Book One",
            5,
            "2025-11-17"
        ),
        Book(
            1,
            authors[1],
            categories[1],
            "",
            "2025-11-17",
            "Book 2 description",
            "2nd",
            2,
            true,
            "ISBN-002",
            "en",
            150,
            2024,
            4.0f,
            "Book Two",
            8,
            "2025-11-17"
        ),
    ) + (3..12).map {
        Book(
            1,
            authors[it % authors.size],
            categories[it % categories.size],
            "",
            "2025-11-17",
            "Book $it description",
            "${it}th",
            it,
            true,
            "ISBN-$it",
            "en",
            80 + it,
            2020 + (it % 5),
            4.0f + (it % 5) * 0.1f,
            "Book $it",
            5 + it,
            "2025-11-17"
        )
    }

    // ---------------------------
    // BookDetail (11 total)
    // ---------------------------
    val bookDetails = listOf(
        BookDetail(
            1, authors[0], 1, categories[0], 1,
            chapters, "", "2025-11-17", "Full book detail",
            "", "1st", 1, true, false, "ISBN-001",
            "en", 100, 2025, 5, "", "",
            "Book One", 5, "2025-11-17"
        )
    ) + (2..11).map {
        BookDetail(
            1, authors[it % authors.size], authors[it % authors.size].id,
            categories[it % categories.size], categories[it % categories.size].id,
            chapters, "", "2025-11-17", "Book detail $it",
            "", "${it}th", it, true, false, "ISBN-D$it",
            "en", 120 + it, 2020 + (it % 5), 4 + (it % 5),
            "", "", "Book Detail $it", 10 + it,
            "2025-11-17"
        )
    }

    // ---------------------------
    // Assignments (11 total)
    // ---------------------------
    val assignments = listOf(
        Assignment(
            description = "Assignment 1",
            dueDate = "2025-12-01",
            id = 1,
            isPublished = true,
            lessonId = lessons[0].id,
            maxAttempts = 3,
            maxScore = 100,
            passingScore = 50,
            questions = null,
            showAnswersAfter = 0,
            shuffleOptions = false,
            shuffleQuestions = false,
            timeLimit = 60,
            title = "Assignment 1",
            userAttempts = users
        )
    ) + (2..11).map {
        Assignment(
            description = "Assignment $it",
            dueDate = "2025-12-${if (it < 10) "0$it" else it}",
            id = it,
            isPublished = true,
            lessonId = lessons[0].id,
            maxAttempts = 3,
            maxScore = 100,
            passingScore = 50,
            questions = null,
            showAnswersAfter = 0,
            shuffleOptions = it % 2 == 0,
            shuffleQuestions = it % 2 == 1,
            timeLimit = 60 + it,
            title = "Assignment $it",
            userAttempts = users
        )
    }

    // ---------------------------
    // Coupons (11 total)
    // ---------------------------
    val coupons = listOf(
        Coupon(
            emptyList(),
            emptyList(),
            "NEWYEAR",
            "New Year Discount",
            1,
            "Percent",
            10,
            1,
            true,
            100,
            0,
            "New Year",
            100,
            0,
            "2025-01-01",
            "2025-12-31"
        )
    ) + (2..11).map {
        Coupon(
            emptyList(), emptyList(), "COUPON$it",
            "Discount coupon $it", 1, "Percent",
            5 + it, it, true,
            100 + it, 0, "Coupon $it",
            100, 0, "2025-01-01", "2025-12-31"
        )
    }

    // ---------------------------
    // Items (11 total)
    // ---------------------------
    val items = listOf(
        Item(1, 101, "Book One", 1, "Book", 100, 1, 100)
    ) + (2..11).map {
        Item(it, 100 + it, "Item $it", 1, "Book", 50 + it, 1, 50 + it)
    }

    // ---------------------------
    // Orders (11 total)
    // ---------------------------
    val orders = listOf(
        Order(
            "USD",
            10,
            1,
            90,
            1,
            "ORD001",
            "",
            "2025-11-17",
            "stripe",
            "card",
            1,
            "Paid",
            0,
            100,
            "TXN001",
            users[0].id,
            coupons[0],
            items,
            "2025-11-17"
        )
    ) + (2..11).map {
        Order(
            "USD", it, 1, 100 + it,
            it, "ORD00$it", "", "2025-11-${10 + it}",
            "stripe", "card", 1, "Paid",
            0, 110 + it, "TXN00$it",
            users[0].id, coupons[0], items,
            "2025-11-17"
        )
    }

    // ---------------------------
    // LearningPaths (3 total)
    // ---------------------------
    val learningPaths = listOf(
        LearningPath(
            approvalStatus = 1,
            approvalStatusName = "Approved",
            category = categories[0],
            categoryId = categories[0].id,
            courseCount = 3,
            courses = mockCourses[0],
            createdAt = "2025-11-17",
            description = "A structured path to learn programming",
            difficultyLevel = 2,
            difficultyLevelName = "Intermediate",
            enrollmentCount = 120,
            estimatedDuration = 120,
            id = 1,
            instructor = instructors[0],
            instructorId = instructors[0].id,
            isActive = true,
            isEnrolled = false,
            isPublished = true,
            price = 199,
            qualityScore = 0f,
            reviewNotes = "",
            thumbnail = R.drawable.course_test.toString(),
            title = "Programming Bootcamp",
            updatedAt = "2025-11-17"
        ),
        LearningPath(
            approvalStatus = 1,
            approvalStatusName = "Published",
            category = categories[1],
            categoryId = categories[1].id,
            courseCount = 2,
            courses = mockCourses[1],
            createdAt = "2025-11-17",
            description = "Design fundamentals and UI/UX",
            difficultyLevel = 1,
            difficultyLevelName = "Beginner",
            enrollmentCount = 80,
            estimatedDuration = 60,
            id = 2,
            instructor = instructors[1],
            instructorId = instructors[1].id,
            isActive = true,
            isEnrolled = false,
            isPublished = true,
            price = 99,
            qualityScore = 0f,
            reviewNotes = "",
            thumbnail = R.drawable.course_test.toString(),
            title = "Design Starter Pack",
            updatedAt = "2025-11-17"
        ),
        LearningPath(
            approvalStatus = 1,
            approvalStatusName = "Live",
            category = categories[2],
            categoryId = categories[2].id,
            courseCount = 4,
            courses = mockCourses[2 % mockCourses.size],
            createdAt = "2025-11-17",
            description = "Backend and APIs",
            difficultyLevel = 3,
            difficultyLevelName = "Advanced",
            enrollmentCount = 200,
            estimatedDuration = 240,
            id = 3,
            instructor = instructors[2],
            instructorId = instructors[2].id,
            isActive = true,
            isEnrolled = false,
            isPublished = true,
            price = 299,
            qualityScore = 0f,
            reviewNotes = "",
            thumbnail = R.drawable.course_test.toString(),
            title = "Backend Mastery",
            updatedAt = "2025-11-17"
        )
    )

    // ---------------------------
    // Questions (12 total)
    // ---------------------------
    val questions = listOf(
        Question(1),
        Question(2),
    ) + (3..12).map { Question(it) }

    // ---------------------------
    // Authorization
    // ---------------------------
    val authorization = Authorization("access-token-xyz", "refresh-token-abc")
}
