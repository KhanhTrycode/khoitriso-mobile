//package com.example.khoitriso.test
//
//import com.example.khoitriso.R
//import com.example.khoitriso.domain.models.Category
//import com.example.khoitriso.domain.models.Course
//import com.example.khoitriso.domain.models.Instructor
//
//object MockData {
//    val categories = listOf(
//        Category(1, "Programming"),
//        Category(2, "Design"),
//        Category(3, "Marketing"),
//        Category(4, "Business")
//    )
//    val instructors = listOf(
//        Instructor(
//            id = 1,
//            name = "Nguyên Văn A",
//            bio = "Giảng viên Android & Kotlin với 10 năm kinh nghiệm",
//            avatar = R.drawable.course_test
//        ),
//        Instructor(
//            id = 2,
//            name = "Trần Thị B",
//            bio = "Chuyên gia UI/UX & Jetpack Compose",
//            avatar = R.drawable.course_test
//        ),
//        Instructor(
//            id = 3,
//            name = "Lê Văn C",
//            bio = "Chuyên gia Backend & REST API",
//            avatar = R.drawable.course_test
//        ),
//        Instructor(
//            id = 4,
//            name = "Phạm Thị D",
//            bio = "Chuyên gia Marketing Online",
//            avatar = R.drawable.course_test
//        )
//    )
//
//    val mockCourses = listOf(
//        Course(
//            id = 1,
//            title = "Course No1",
//            description = "Try Test Course 1",
//            category = categories[0],
//            instructor = instructors[0],
//            thumbnail = R.drawable.course_test.toString(),
//            estimatedDuration = 20,
//            isFree = true,
//            isPublished = true,
//            level = 1,
//            price = 0,
//            rating = 4.5f,
//            totalLessons = 10,
//            totalReviews = 10,
//            totalStudents = 100,
//            approvalStatus = 1
//        ),
//        Course(
//            id = 2,
//            title = "Course No2",
//            description = "Try Test Course 2",
//            category = categories[1],
//            instructor = instructors[1],
//            thumbnail = R.drawable.course_test.toString(),
//            estimatedDuration = 35,
//            isFree = false,
//            isPublished = true,
//            level = 2,
//            price = 50,
//            rating = 4.7f,
//            totalLessons = 15,
//            totalReviews = 20,
//            totalStudents = 200,
//            approvalStatus = 1
//        ),
//        Course(
//            id = 3,
//            title = "Course No3",
//            description = "Try Test Course 3",
//            category = categories[2],
//            instructor = instructors[2],
//            thumbnail = R.drawable.course_test.toString(),
//            estimatedDuration = 50,
//            isFree = true,
//            isPublished = true,
//            level = 1,
//            price = 0,
//            rating = 4.9f,
//            totalLessons = 20,
//            totalReviews = 30,
//            totalStudents = 300,
//            approvalStatus = 1
//        ),
//        Course(
//            id = 4,
//            title = "Course No4",
//            description = "Try Test Course 4",
//            category = categories[3],
//            instructor = instructors[3],
//            thumbnail = R.drawable.course_test.toString(),
//            estimatedDuration = 25,
//            isFree = false,
//            isPublished = true,
//            level = 3,
//            price = 70,
//            rating = 4.3f,
//            totalLessons = 12,
//            totalReviews = 15,
//            totalStudents = 150,
//            approvalStatus = 1
//        )
//    )
//
//    val tryFreeCourse = mockCourses[0]
//
//    val recommendedCourses = mockCourses.subList(1, 3)
//
//    val trendingCourses = mockCourses.subList(2,3)
//}
