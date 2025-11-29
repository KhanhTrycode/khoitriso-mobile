package com.example.khoitriso.data.dto.request

data class PagingRequest(
    val page: Int = 1,
    val pageSize: Int = 20,
    val search: String? = null,
    val approvalStatus: Int? = null,
    val authorId: Int? = null,
    val sortBy: String? = null,
    val sortOrder: String? = null
)

fun PagingRequest.toMap(): Map<String, String> {
    val map = mutableMapOf<String, String>()
    map["page"] = page.toString()
    map["pageSize"] = pageSize.toString()
    search?.let { map["search"] = it }
    approvalStatus?.let { map["approvalStatus"] = it.toString() }
    authorId?.let { map["authorId"] = it.toString() }
    sortBy?.let { map["sortBy"] = it }
    sortOrder?.let { map["sortOrder"] = it }
    return map
}