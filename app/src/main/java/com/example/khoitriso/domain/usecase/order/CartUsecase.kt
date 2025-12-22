package com.example.khoitriso.domain.usecase.order

import com.example.khoitriso.domain.models.Carts
import com.example.khoitriso.domain.models.OrderItem
import com.example.khoitriso.domain.repository.CartRepository

data class CartUsecase(
    val getCart: GetCart,
    val addToCart: AddToCart,
    val removeFromCart: RemoveFromCart,
    val clearCart: ClearCart,
)

class GetCart(private val cartRepository: CartRepository) {
    suspend operator fun invoke(): Result<Carts> {
        return cartRepository.getCart()
    }
}

class AddToCart(private val cartRepository: CartRepository) {
    suspend operator fun invoke(itemId: Int, itemType: Int): Result<Int> {
        return cartRepository.addToCart(itemId = itemId, itemType = itemType)
    }
}

class RemoveFromCart(private val cartRepository: CartRepository) {
    suspend operator fun invoke(cartId: Int): Result<Boolean>{
        return cartRepository.removeFromCart(cartId)
    }
}

class ClearCart(private val cartRepository: CartRepository) {
    suspend operator fun invoke(): Result<Boolean>{
        return cartRepository.clearCart()
    }

}