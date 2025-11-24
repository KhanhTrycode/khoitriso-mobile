package com.example.khoitriso.domain.request


data class GetBookRequest(
   val page: Int = 1,
   val pageSize: Int =20,
   val search: String?= null,
   val approvalStatus: Int? = null,
   val authorId: Int? = null,
   val sortBy: String? = null,
   val sortOrder: String? = null,
)