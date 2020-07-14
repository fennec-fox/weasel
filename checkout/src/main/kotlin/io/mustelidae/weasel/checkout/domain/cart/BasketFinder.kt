package io.mustelidae.weasel.checkout.domain.cart

import io.mustelidae.weasel.checkout.config.CheckoutException
import io.mustelidae.weasel.checkout.domain.cart.repository.BasketRepository
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class BasketFinder(
    private val basketRepository: BasketRepository
) {

    fun findOne(id: ObjectId): Basket {
        return basketRepository.findByIdOrNull(id) ?: throw CheckoutException("해당 카트가 존재 하지 않습니다.")
    }
}
