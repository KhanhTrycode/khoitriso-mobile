package com.example.khoitriso.domain.repository

import com.example.khoitriso.domain.models.Lesson

interface LessonRepository {
    suspend fun getLessonById(lessonId: Int): Result<Lesson>
}
