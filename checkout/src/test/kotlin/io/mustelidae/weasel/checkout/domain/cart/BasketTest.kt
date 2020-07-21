package io.mustelidae.weasel.checkout.domain.cart

import io.mustelidae.weasel.checkout.utils.nextString
import io.mustelidae.weasel.common.code.ProductType
import kotlin.random.Random

internal class BasketTest

internal fun Basket.Companion.aFixture(
    type: Basket.Type = Basket.Type.NORMAL,
    cpId: Long = Random.nextLong(100, 1000),
    userId: String = Random.nextString(4),
    status: Basket.Status = Basket.Status.WAIT
): Basket {
    return Basket(type, cpId, userId).apply {
        this.status = status
        this.addBy(Item.aFixture(ProductType.GENERAL))
    }
}
