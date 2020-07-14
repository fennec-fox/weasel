package io.mustelidae.weasel.checkout.domain.cart

import io.mustelidae.weasel.checkout.config.CheckoutCartException

/**
 * 일반 카트
 * mental model: 일반적인 카트. 결제 전까지 물건을 담고 빼고 할 수 있다.
 */
internal class NormalCart : Cart {
    internal constructor(
        cpId: Long,
        userId: String,
        items: List<Item>
    ) {
        this.basket = Basket(Basket.Type.NORMAL, cpId, userId)
        items.forEach { this.basket.addBy(it) }
    }

    constructor(basket: Basket) {
        if (basket.type != Basket.Type.NORMAL)
            throw CheckoutCartException("잘못된 카트가 선택 되었습니다.", basket.id)

        this.basket = basket
    }

    override val basket: Basket

    override fun addBy(item: Item) {
        if (basket.status != Basket.Status.WAIT)
            throw CheckoutCartException("해당 카트는 결제 진행 중이기 때문에 추가가 불가능 합니다.", basket.id)

        basket.addBy(item)
    }

    override fun removeBy(item: Item) {
        if (basket.status != Basket.Status.WAIT)
            throw CheckoutCartException("해당 카트는 결제 진행 중이기 때문에 삭제가 불가능 합니다.", basket.id)

        basket.removeBy(item)
    }
}
