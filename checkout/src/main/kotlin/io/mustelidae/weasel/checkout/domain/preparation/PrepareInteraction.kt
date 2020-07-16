package io.mustelidae.weasel.checkout.domain.preparation

import io.mustelidae.weasel.checkout.config.CheckoutException
import io.mustelidae.weasel.checkout.domain.cart.BasketFinder
import io.mustelidae.weasel.checkout.domain.cart.Cart
import io.mustelidae.weasel.checkout.domain.preparation.repository.PreparationRepository
import io.mustelidae.weasel.common.code.PayMethod
import java.time.LocalDate
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class PrepareInteraction(
    private val basketFinder: BasketFinder,
    private val preparationFinder: PreparationFinder,
    private val preparationRepository: PreparationRepository
) {

    fun prepare(
        userId: String,
        cart: Cart,
        extraFields: Map<String, String?> = emptyMap(),
        rewardDate: LocalDate? = null,
        ignoreMethods: List<PayMethod>? = null
    ): ObjectId {
        val preparation = Preparation(userId, extraFields, rewardDate, ignoreMethods).apply {
            addBy(cart.basket)
        }
        preparationRepository.save(preparation)
        return preparation.id
    }

    fun prepare(
        userId: String,
        basketId: ObjectId,
        extraFields: Map<String, String?> = emptyMap(),
        rewardDate: LocalDate? = null,
        ignoreMethods: List<PayMethod>? = null
    ): ObjectId {
        val basket = basketFinder.findOrThrow(basketId)
        val preparation = Preparation(userId, extraFields, rewardDate, ignoreMethods).apply {
            addBy(basket)
        }

        preparationRepository.save(preparation)
        return preparation.id
    }

    fun prepareWithCarts(
        userId: String,
        carts: List<Cart>,
        extraFields: Map<String, String?> = emptyMap(),
        rewardDate: LocalDate? = null,
        ignoreMethods: List<PayMethod>? = null
    ): ObjectId {
        val preparation = Preparation(userId, extraFields, rewardDate, ignoreMethods)
        carts.forEach { preparation.addBy(it.basket) }
        preparationRepository.save(preparation)
        return preparation.id
    }

    fun prepareWithBasketIds(
        userId: String,
        basketIds: List<ObjectId>,
        extraFields: Map<String, String?> = emptyMap(),
        rewardDate: LocalDate? = null,
        ignoreMethods: List<PayMethod>? = null
    ): ObjectId {
        val baskets = basketFinder.findAll(basketIds)

        if (baskets.isEmpty())
            throw CheckoutException("결제를 할 대상이 존재하지 않습니다.")

        val preparation = Preparation(userId, extraFields, rewardDate, ignoreMethods)
        baskets.forEach { preparation.addBy(it) }

        preparationRepository.save(preparation)
        return preparation.id
    }

    fun processingPayment(id: ObjectId) {
        val preparation = preparationFinder.findOrThrow(id)
        preparation.progress()
        preparationRepository.save(preparation)
    }

    fun confirmPayment(id: ObjectId) {
        val preparation = preparationFinder.findOrThrow(id)
        preparation.complete()
        preparationRepository.save(preparation)
    }
}
