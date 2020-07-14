package io.mustelidae.weasel.checkout.domain.cart

internal interface Cart {
    val basket: Basket

    fun addBy(item: Item)

    fun removeBy(item: Item)

    companion object {
        fun from(cpId: Long, userId: String, type: Basket.Type, items: List<Item>): Cart {
            return when (type) {
                Basket.Type.BOXING -> BoxingCart(cpId, userId, items)
                Basket.Type.NEAR_COUNTER -> NearCounterCart(cpId, userId, items)
                Basket.Type.NORMAL -> NormalCart(cpId, userId, items)
            }
        }

        fun from(basket: Basket): Cart {
            return when (basket.type) {
                Basket.Type.BOXING -> BoxingCart(basket)
                Basket.Type.NEAR_COUNTER -> NearCounterCart(basket)
                Basket.Type.NORMAL -> NormalCart(basket)
            }
        }
    }
}
