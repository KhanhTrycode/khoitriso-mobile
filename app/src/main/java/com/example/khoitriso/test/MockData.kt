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
        Instructor(avatar = "", bio = "Giảng viên Android", id = 1, name = "Nguyên Văn A"),
        Instructor(avatar = "", bio = "Chuyên gia UI/UX", id = 2, name = "Trần Thị B"),
        Instructor(avatar = "", bio = "Backend Developer", id = 3, name = "Lê Văn C"),
        Instructor(avatar = "", bio = "AWS Cloud Engineer", id = 4, name = "Nguyễn Thành D"),
        Instructor(avatar = "", bio = "AI Engineer", id = 5, name = "Phạm Như E"),
        Instructor(avatar = "", bio = "KMM Specialist", id = 6, name = "Đặng Bá F"),
        Instructor(avatar = "", bio = "Game Developer", id = 7, name = "Hồ Mỹ G"),
        Instructor(avatar = "", bio = "Cyber Security Expert", id = 8, name = "Trần Duy H"),
        Instructor(avatar = "", bio = "Fullstack Dev", id = 9, name = "Lê Văn I"),
        Instructor(avatar = "", bio = "React Developer", id = 10, name = "Nguyễn Quốc K"),
        Instructor(avatar = "", bio = "UI/UX Designer", id = 11, name = "Phạm Linh L"),
        Instructor(avatar = "", bio = "Spring Boot Instructor", id = 12, name = "Vũ Trọng M"),
        Instructor(avatar = "", bio = "iOS Swift Developer", id = 13, name = "Đỗ Thành N"),
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

    // Helper function to create lessons
    private fun createMockLessonsForCourse(courseId: Int, count: Int): List<Lesson> {
        return (1..count).map { lessonIndex ->
            Lesson(
                id = courseId * 100 + lessonIndex,
                courseId = courseId,
                title = "Chapter $lessonIndex: Topic for Course $courseId",
                description = "This is a detailed description for lesson $lessonIndex of course $courseId.",
                lessonOrder = lessonIndex,
                isFree = lessonIndex == 1, // Only the first lesson is free
                isPublished = true,
                videoDuration = 300 + (lessonIndex * 50),
                videoUrl = "https://example.com/video_${courseId}_${lessonIndex}.mp4",
                contentText = 0, // Placeholder
                materials = emptyList(),
                userProgress = null
            )
        }
    }

    // ---------------------------
    // Courses (12 total) - FIXED & COMPLETED
    // ---------------------------
    val mockCourses = listOf(
        Course(
            id = 1,
            title = "Complete Kotlin Development Masterclass",
            description = "This is the best course for learning Kotlin from scratch. We cover all the basics and advanced topics of modern Android development with Jetpack Compose.",
            instructor = instructors[0],
            category = categories[0],
            price = 490000,
            thumbnail = R.drawable.course_test.toString(),
            isFree = false,
            level = 2,
            rating = 4.8f,
            totalLessons = 5,
            totalReviews = 150,
            totalStudents = 2300,
            estimatedDuration = 1200,
            requirements = listOf(
                "A computer with internet access (Windows, Mac, or Linux).",
                "Basic understanding of any programming language is a plus but not required."
            ),
            lessons = createMockLessonsForCourse(1, 5),
            whatYouWillLearn = listOf(
                "A computer with internet access (Windows, Mac, or Linux).",
                "Basic understanding of any programming language is a plus but not required."
            )
        )
    ) + (2..12).map { index ->
        val isFreeFlag = index % 2 == 0
        Course(
            id = index,
            title = "Course No$index",
            description = "Mock Course $index description. This course covers topics from the ${categories[index % categories.size].name} category.",
            instructor = instructors[index % instructors.size],
            category = categories[index % categories.size],
            price = if (isFreeFlag) 0 else (20 + index),
            thumbnail = R.drawable.course_test.toString(),
            isFree = isFreeFlag,
            level = (index % 3) + 1,
            rating = 4.0f + (index % 5) * 0.1f,
            totalLessons = 5,
            totalReviews = 10 + index,
            totalStudents = 100 * index,
            estimatedDuration = 600 + index * 50,
            requirements = listOf("Basic knowledge for course $index"),
            lessons = createMockLessonsForCourse(index, 5),
            whatYouWillLearn = listOf(
                "A computer with internet access (Windows, Mac, or Linux).",
                "Basic understanding of any programming language is a plus but not required."
            )
        )
    }

    // ---------------------------
    // Books (12 total) - FIXED
    // ---------------------------
    val books = (1..12).map { index ->
        Book(
            author = authors[index % authors.size],
            category = categories[index % categories.size],
            description = "Book $index description",
            edition = "${index}th",
            id = index,
            language = "en",
            publicationYear = 2020 + (index % 5),
            rating = 4.0f + (index % 5) * 0.1f,
            title = "Book $index",
            totalReviews = 5 + index,
            approvalStatus = 0,
            coverImage = "",
            createdAt = "",
            price = 201,
            updatedAt = "",
        )
    }

    // ---------------------------
    // Chapters (10 sample)
    // ---------------------------
    val chapters = (1..10).map { i ->
        Chapter(
            bookId = (i % books.size) + 1,
            createdAt = "2025-11-17",
            description = "Chapter $i for book ${(i % books.size) + 1}",
            id = 100 + i,
            orderIndex = i,
            questionCount = 0,
            questions = emptyList(),
            title = "Chapter $i",
            updatedAt = "2025-11-17"
        )
    }

    // ---------------------------
    // BookDetails (10 sample)
    // ---------------------------
    val bookDetails = (1..10).map { i ->
        BookDetail(
            approvalStatus = 1,
            author = authors[i % authors.size],
            category = categories[i % categories.size],
            chapters = chapters.filter { it.bookId == ((i % books.size) + 1) },
            coverImage = "",
            createdAt = "2025-11-17",
            description = "Detailed description for BookDetail $i",
            ebookFile = "",
            edition = "${i}th",
            id = 200 + i,
            isOwned = false,
            isbn = "ISBN-BD-$i",
            language = "en",
            price = 900+i*3,
            publicationYear = 2020 + (i % 5),
            rating = (3f + (i % 3)),
            reviewNotes = "",
            staticPagePath = "",
            title = "Book Detail $i",
            totalReviews = 1 + i,
            updatedAt = "2025-11-17"
        )
    }

    // ---------------------------
    // Assignments (10 sample)
    // ---------------------------
    val assignments = (1..10).map { i ->
        Assignment(
            description = "Assignment $i for lesson",
            dueDate = "2025-12-${if (i < 10) "0$i" else "$i"}",
            id = 300 + i,
            isPublished = true,
            lessonId = mockCourses[i % mockCourses.size].lessons.firstOrNull()?.id ?: (i * 1000),
            maxAttempts = 3,
            maxScore = 100,
            passingScore = 50,
            questions = null,
            showAnswersAfter = 0,
            shuffleOptions = false,
            shuffleQuestions = false,
            timeLimit = 60,
            title = "Assignment $i",
            userAttempts = users.take(3)
        )
    }

    // ---------------------------
    // Coupons (10 sample)
    // ---------------------------
    val coupons = (1..10).map { i ->
        Coupon(
            applicableItemIds = emptyList(),
            applicableItemTypes = emptyList(),
            code = "COUPON$i",
            description = "Coupon $i description",
            discountType = if (i % 2 == 0) 1 else 2,
            discountTypeName = if (i % 2 == 0) "Percent" else "Fixed",
            discountValue = if (i % 2 == 0) 10 + i else 5 + i,
            id = 400 + i,
            isActive = true,
            maxDiscountAmount = 100,
            minOrderAmount = 0,
            name = "Coupon $i",
            usageLimit = 100,
            usedCount = 0,
            validFrom = "2025-01-01",
            validTo = "2025-12-31"
        )
    }

    // ---------------------------
    // Items (10 sample)
    // ---------------------------
    val items = (1..10).map { i ->
        Item(
            id = 500 + i,
            itemId = 1000 + i,
            itemName = "Item $i",
            itemType = 1,
            itemTypeName = "Product",
            price = 10 * i,
            quantity = 1,
            subTotal = 10 * i
        )
    }

    // ---------------------------
    // Orders (10 sample)
    // ---------------------------
    val orders = (1..10).map { i ->
        Order(
            currency = "USD",
            discountAmount = if (i % 2 == 0) 5 else 0,
            exchangeRate = 1,
            finalAmount = (items[i - 1].subTotal - if (i % 2 == 0) 5 else 0),
            id = 600 + i,
            orderCode = "ORD${600 + i}",
            orderNotes = "",
            paidAt = "2025-11-17",
            paymentGateway = "stripe",
            paymentMethod = "card",
            status = 1,
            statusName = "Paid",
            taxAmount = 0,
            totalAmount = items[i - 1].subTotal,
            transactionId = "TXN${600 + i}",
            userId = users[i % users.size].id,
            coupon = if (i % 3 == 0) coupons[i % coupons.size] else null,
            items = listOf(items[i - 1]),
            createdAt = "2025-11-17"
        )
    }

    // ---------------------------
    // LearningPaths (10 sample)
    // ---------------------------
    val learningPaths = (1..10).map { i ->
        LearningPath(
            approvalStatus = 1,
            approvalStatusName = "Published",
            category = categories[i % categories.size],
            categoryId = categories[i % categories.size].id,
            courseCount = 1,
            courses = mockCourses.subList(2,5),
            createdAt = "2025-11-17",
            description = "Learning path $i",
            difficultyLevel = (i % 3) + 1,
            difficultyLevelName = listOf("Beginner", "Intermediate", "Advanced")[i % 3],
            enrollmentCount = 50 * i,
            estimatedDuration = 60 * i,
            id = 700 + i,
            instructor = instructors[i % instructors.size],
            instructorId = instructors[i % instructors.size].id,
            isActive = true,
            isEnrolled = false,
            isPublished = true,
            price = 99 + i,
            qualityScore = 4.0f,
            reviewNotes = "",
            thumbnail = R.drawable.course_test.toString(),
            title = "Learning Path $i",
            updatedAt = "2025-11-17"
        )
    }

    // ---------------------------
    // Questions (10 sample)
    // ---------------------------
    val questions = (1..10).map { i -> Question(id = 800 + i) }

    // ---------------------------
    // Authorizations (10 sample)
    // ---------------------------
    val authorizations = (1..10).map { i -> Authorization(accessToken = "access-$i", refresh = "refresh-$i") }
}
