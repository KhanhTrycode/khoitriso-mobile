package com.example.khoitriso.data.dto

import com.example.khoitriso.domain.models.Wishlist
import com.example.khoitriso.domain.models.WishlistItem
import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import java.lang.reflect.Type

// Custom TypeAdapter to keep Item as JsonObject
class JsonObjectTypeAdapter : JsonDeserializer<JsonObject>, JsonSerializer<JsonObject> {
    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): JsonObject? {
        return json?.asJsonObject
    }

    override fun serialize(src: JsonObject?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement? {
        return src
    }
}

data class WishlistItemDto(
    val Id: Int,
    val ItemId: Int,
    val ItemType: Int, // 0: Book, 1: Course, 2: LearningPath
    val AddedAt: String,
    @JsonAdapter(JsonObjectTypeAdapter::class)
    val Item: JsonObject? = null // Full item data as JsonObject, will be parsed in repository
)

data class WishlistDto(
    val Items: List<WishlistItemDto>,
    val TotalItems: Int
)

// DTO for paged wishlist response from backend
data class PagedWishlistResultDto(
    val Page: Int,
    val PageSize: Int,
    val Total: Int,
    val Data: List<WishlistItemDto>
)

// Extension function that takes Gson as parameter for parsing Item
fun WishlistItemDto.toDomain(gson: com.google.gson.Gson): WishlistItem {
    // Parse Item based on ItemType
    val item: Any? = when (ItemType) {
        0 -> { // Book
            Item?.let {
                try {
                    gson.fromJson(it, BookDto::class.java)?.toDomain()
                } catch (e: Exception) {
                    null
                }
            }
        }
        1 -> { // Course
            Item?.let {
                try {
                    gson.fromJson(it, CourseDto::class.java)?.toDomain()
                } catch (e: Exception) {
                    null
                }
            }
        }
        else -> null
    }
    
    return WishlistItem(
        id = Id,
        itemId = ItemId,
        itemType = ItemType,
        addedAt = AddedAt,
        item = item
    )
}

fun WishlistDto.toDomain(gson: com.google.gson.Gson) = Wishlist(
    items = Items.map { it.toDomain(gson) },
    totalItems = TotalItems
)

