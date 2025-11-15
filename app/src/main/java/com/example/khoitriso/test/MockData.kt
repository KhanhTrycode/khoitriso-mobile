package com.example.khoitriso.test

import com.example.khoitriso.R
import com.example.khoitriso.domain.models.Category
import com.example.khoitriso.domain.models.Course
import com.example.khoitriso.domain.models.Instructor

object MockData {
    val categories = listOf(
        Category(1, "Programming"),
        Category(2, "Design"),
        Category(3, "Marketing"),
        Category(4, "Business")
    )
    val instructors = listOf(
        Instructor(
            Id = 1,
            Name = "Nguyên Văn A",
            Bio = "Giảng viên Android & Kotlin với 10 năm kinh nghiệm",
            Avatar = R.drawable.course_test
        ),
        Instructor(
            Id = 2,
            Name = "Trần Thị B",
            Bio = "Chuyên gia UI/UX & Jetpack Compose",
            Avatar = R.drawable.course_test
        ),
        Instructor(
            Id = 3,
            Name = "Lê Văn C",
            Bio = "Chuyên gia Backend & REST API",
            Avatar = R.drawable.course_test
        ),
        Instructor(
            Id = 4,
            Name = "Phạm Thị D",
            Bio = "Chuyên gia Marketing Online",
            Avatar = R.drawable.course_test
        )
    )

    val mockCourses = listOf(
        Course(
            Id = 1,
            Title = "Course No1",
            Description = "Try Test Course 1",
            Category = categories[0],
            Instructor = instructors[0],
            Thumbnail = R.drawable.course_test,
            EstimatedDuration = 20,
            IsFree = true,
            IsPublished = true,
            Level = 1,
            Price = 0,
            Rating = 4.5f,
            TotalLessons = 10,
            TotalReviews = 10,
            TotalStudents = 100,
            ApprovalStatus = 1
        ),
        Course(
            Id = 2,
            Title = "Course No2",
            Description = "Try Test Course 2",
            Category = categories[1],
            Instructor = instructors[1],
            Thumbnail = R.drawable.course_test,
            EstimatedDuration = 35,
            IsFree = false,
            IsPublished = true,
            Level = 2,
            Price = 50,
            Rating = 4.7f,
            TotalLessons = 15,
            TotalReviews = 20,
            TotalStudents = 200,
            ApprovalStatus = 1
        ),
        Course(
            Id = 3,
            Title = "Course No3",
            Description = "Try Test Course 3",
            Category = categories[2],
            Instructor = instructors[2],
            Thumbnail = R.drawable.course_test,
            EstimatedDuration = 50,
            IsFree = true,
            IsPublished = true,
            Level = 1,
            Price = 0,
            Rating = 4.9f,
            TotalLessons = 20,
            TotalReviews = 30,
            TotalStudents = 300,
            ApprovalStatus = 1
        ),
        Course(
            Id = 4,
            Title = "Course No4",
            Description = "Try Test Course 4",
            Category = categories[3],
            Instructor = instructors[3],
            Thumbnail = R.drawable.course_test,
            EstimatedDuration = 25,
            IsFree = false,
            IsPublished = true,
            Level = 3,
            Price = 70,
            Rating = 4.3f,
            TotalLessons = 12,
            TotalReviews = 15,
            TotalStudents = 150,
            ApprovalStatus = 1
        )
    )

    val tryFreeCourse = mockCourses[0]

    val recommendedCourses = mockCourses.subList(1, 3)

    val trendingCourses = mockCourses.subList(2,3)
}