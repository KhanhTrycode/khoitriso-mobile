package com.example.khoitriso.data.repository

import com.example.khoitriso.data.api.LessonsApi
import com.example.khoitriso.data.dto.toDomain
import com.example.khoitriso.domain.models.Lesson
import com.example.khoitriso.domain.repository.LessonRepository
import javax.inject.Inject

class LessonRepositoryImpl @Inject constructor(
    private val lessonsApi: LessonsApi
) : LessonRepository {

    override suspend fun getLessonById(lessonId: Int): Result<Lesson> {
        return try {
            val response = lessonsApi.getLessonById(lessonId)
            
            if (response.isSuccessful) {
                val body = response.body()
                val result = body?.Result
                
                if (result != null) {
                    Result.success(result.toDomain())
                } else {
                    Result.failure(Exception("Response body or Result is null"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Error ${response.code()}: ${errorBody ?: response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Network error: ${e.message}", e))
        }
    }
}
