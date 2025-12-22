package com.example.khoitriso.domain.usecase.lesson

import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.repository.LessonRepository
import javax.inject.Inject

class LessonUsecase @Inject constructor(
    private val lessonRepository: LessonRepository
) {
    suspend fun getLessonById(lessonId: Int): Result<Lesson> {
        return lessonRepository.getLessonById(lessonId)
    }
}
