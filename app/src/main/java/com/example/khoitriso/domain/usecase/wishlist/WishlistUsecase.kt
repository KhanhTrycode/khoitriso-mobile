package com.example.khoitriso.domain.usecase.wishlist

import com.example.khoitriso.domain.models.MyResponese
import com.example.khoitriso.domain.models.Wishlist
import com.example.khoitriso.domain.models.WishlistItem
import com.example.khoitriso.domain.repository.WishlistRepository

data class WishlistUsecase(
    val getWishlist: GetWishlist,
    val addToWishlist: AddToWishlist,
    val removeFromWishlist: RemoveFromWishlist,
    val removeItemFromWishlist: RemoveItemFromWishlist,
    val isInWishlist: IsInWishlist,
    val clearWishlist: ClearWishlist
)

class GetWishlist(private val repo: WishlistRepository) {
    suspend operator fun invoke(
        page: Int = 1,
        pageSize: Int = 200,
        itemType: Int? = null
    ) = repo.getWishlist(page, pageSize, itemType)
}

class AddToWishlist(private val repo: WishlistRepository) {
    suspend operator fun invoke(
        itemId: Int,
        itemType: Int
    ): Result<Unit> {
        // Chỉ cần biết success hay không, không cần wishlist data
        return repo.addToWishlist(itemId, itemType).map { Unit }
    }
}

class RemoveFromWishlist(private val repo: WishlistRepository) {
    suspend operator fun invoke(wishlistId: Int) = repo.removeFromWishlist(wishlistId)
}

class RemoveItemFromWishlist(private val repo: WishlistRepository) {
    suspend operator fun invoke(
        itemId: Int,
        itemType: Int
    ) = repo.removeItemFromWishlist(itemId, itemType)
}

class IsInWishlist(private val repo: WishlistRepository) {
    suspend operator fun invoke(
        itemId: Int,
        itemType: Int
    ) = repo.isInWishlist(itemId, itemType)
}

class ClearWishlist(private val repo: WishlistRepository) {
    suspend operator fun invoke() = repo.clearWishlist()
}

