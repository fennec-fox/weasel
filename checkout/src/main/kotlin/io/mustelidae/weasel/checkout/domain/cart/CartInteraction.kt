package io.mustelidae.weasel.checkout.domain.cart

import io.mustelidae.weasel.checkout.domain.cart.repository.BasketRepository
import org.bson.types.ObjectId
import org.springframework.stereotype.Service

@Service
class CartInteraction(
    private val basketFinder: BasketFinder,
    private val basketRepository: BasketRepository
) {

    fun save(cpId: Long, userId: String, type: Basket.Type, items: List<Item>): ObjectId {
        val cart = Cart.from(cpId, userId, type, items)

        basketRepository.save(cart.basket)
        return cart.basket.id
    }

    fun put(basketId: ObjectId, item: Item) {
        val basket = basketFinder.findOne(basketId)
        val cart = Cart.from(basket)
        cart.addBy(item)
        basketRepository.save(cart.basket)
    }

    fun put(basketId: ObjectId, items: List<Item>) {
        val basket = basketFinder.findOne(basketId)
        val cart = Cart.from(basket)

        items.forEach {
            cart.addBy(it)
        }
        basketRepository.save(cart.basket)
    }

    fun remove(basketId: ObjectId, item: Item) {
        val basket = basketFinder.findOne(basketId)
        val cart = Cart.from(basket)
        cart.removeBy(item)
        basketRepository.save(cart.basket)
    }

    fun remove(basketId: ObjectId, items: List<Item>) {
        val basket = basketFinder.findOne(basketId)
        val cart = Cart.from(basket)
        items.forEach {
            cart.removeBy(it)
        }
        basketRepository.save(cart.basket)
    }
}
