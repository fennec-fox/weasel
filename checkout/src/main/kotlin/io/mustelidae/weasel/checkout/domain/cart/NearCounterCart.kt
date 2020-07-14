package io.mustelidae.weasel.checkout.domain.cart

import io.mustelidae.weasel.checkout.config.CheckoutCartException

/**
 * 삭제만 가능한 카트
 * mental model: 곧 계산한 카트라서 물건을 빼기만 가능
 */
internal class NearCounterCart : Cart {

    internal constructor(
        cpId: Long,
        userId: String,
        items: List<Item>
    ) {
        this.basket = Basket(Basket.Type.NEAR_COUNTER, cpId, userId)
        items.forEach { this.basket.addBy(it) }
    }

    constructor(basket: Basket) {
        if (basket.type != Basket.Type.NEAR_COUNTER)
            throw CheckoutCartException("잘못된 카트가 선택 되었습니다.", basket.id)

        this.basket = basket
    }

    override val basket: Basket

    override fun addBy(item: Item) {
        throw CheckoutCartException("해당 카트는 상품의 추가가 불가능 합니다.", basket.id)
    }

    override fun removeBy(item: Item) {
        if (basket.status != Basket.Status.WAIT)
            throw CheckoutCartException("해당 카트는 결제 진행 중이기 때문에 삭제가 불가능 합니다.", basket.id)

        basket.removeBy(item)
    }
}
