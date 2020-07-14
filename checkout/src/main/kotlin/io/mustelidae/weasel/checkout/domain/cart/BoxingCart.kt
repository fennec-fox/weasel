package io.mustelidae.weasel.checkout.domain.cart

import io.mustelidae.weasel.checkout.config.CheckoutCartException

/**
 * 신규 아이템 추가, 삭제 불가능한 카트
 * mental model: 바구니에 물건을 담으면 테이프로 봉인
 */
internal class BoxingCart : Cart {

    internal constructor(
        cpId: Long,
        userId: String,
        items: List<Item>
    ) {
        this.basket = Basket(Basket.Type.BOXING, cpId, userId)
        items.forEach { this.basket.addBy(it) }
    }

    constructor(basket: Basket) {
        if (basket.type != Basket.Type.BOXING)
            throw CheckoutCartException("잘못된 카트가 선택 되었습니다.", basket.id)

        this.basket = basket
    }

    override val basket: Basket

    override fun addBy(item: Item) {
        throw CheckoutCartException("해당 카트는 상품의 추가가 불가능 합니다.", basket.id)
    }

    override fun removeBy(item: Item) {
        throw CheckoutCartException("해당 카트는 상품의 삭제가 불가능 합니다.", basket.id)
    }
}
