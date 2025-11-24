package com.example.khoitriso.test

import com.example.khoitriso.domain.models.ForumQuestion // THAY ĐỔI 1: Import domain model
import com.example.khoitriso.domain.repository.ForumRepository
import com.example.khoitriso.domain.usecase.forum.GetQuestions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class GetQuestionsTest {

    // Khai báo các đối tượng cần thiết
    private lateinit var getQuestionsUsecase: GetQuestions
    private val mockForumRepository: ForumRepository = mock()

    @Before
    fun setUp() {
        // Khởi tạo usecase với repository giả (mock) trước mỗi test
        getQuestionsUsecase = GetQuestions(mockForumRepository)
    }

    @Test
    fun `invoke should return success result from repository`() = runTest {
        // 1. Arrange (Sắp xếp)
        // THAY ĐỔI 2: Tạo dữ liệu giả là một List<ForumQuestion> (domain model)
        val fakeDomainData = listOf(
            ForumQuestion(
                id = "1", title = "Test Question 1", content = "Content 1",
                userId = 1,
                userName = "Test",
                createdAt = ""
            ),
            ForumQuestion(
                id = "2", title = "Test Question 2", content = "Content 2",
                userId = 1,
                userName = "Test",
                createdAt = ""
            )
        )

        // Giả lập hành vi: Khi gọi repo.getQuestions, nó sẽ trả về Result.success
        // chứa danh sách các đối tượng domain.
        whenever(mockForumRepository.getQuestions(
            search = null,
            categoryId = null,
            tag = null,
            isSolved = null,
            isPinned = null,
            page = 1,
            pageSize = 20,
            sortBy = null,
            desc = null
        )).thenReturn(Result.success(fakeDomainData)) // THAY ĐỔI 3: Trả về dữ liệu domain

        // 2. Act (Hành động)
        // Gọi usecase với các tham số mặc định
        val result = getQuestionsUsecase()

        // 3. Assert (Xác nhận)
        // Kiểm tra rằng kết quả trả về là thành công
        assertTrue(result.isSuccess)
        // Kiểm tra rằng dữ liệu bên trong kết quả khớp với dữ liệu giả đã tạo
        assertEquals(fakeDomainData, result.getOrNull())

        // Xác nhận rằng hàm getQuestions của repository đã được gọi đúng 1 lần
        verify(mockForumRepository).getQuestions(
            search = null,
            categoryId = null,
            tag = null,
            isSolved = null,
            isPinned = null,
            page = 1,
            pageSize = 20,
            sortBy = null,
            desc = null
        )
    }

    @Test
    fun `invoke should return failure result when repository fails`() = runTest {
        // 1. Arrange (Sắp xếp)
        val exception = RuntimeException("API Error")

        // Giả lập hành vi: Khi gọi repo.getQuestions, nó sẽ trả về một Result.failure
        whenever(mockForumRepository.getQuestions(
            search = null,
            categoryId = null,
            tag = null,
            isSolved = null,
            isPinned = null,
            page = 1,
            pageSize = 20,
            sortBy = null,
            desc = null
        )).thenReturn(Result.failure(exception))

        // 2. Act (Hành động)
        val result = getQuestionsUsecase()

        // 3. Assert (Xác nhận)
        // Kiểm tra rằng kết quả trả về là thất bại
        assertTrue(result.isFailure)
        // Kiểm tra rằng exception bên trong kết quả khớp với exception đã tạo
        assertEquals(exception, result.exceptionOrNull())
    }
}
