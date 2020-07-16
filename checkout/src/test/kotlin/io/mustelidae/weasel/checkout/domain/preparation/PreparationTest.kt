package io.mustelidae.weasel.checkout.domain.preparation

import io.mustelidae.weasel.checkout.domain.cart.Basket
import io.mustelidae.weasel.checkout.domain.cart.aFixture
import io.mustelidae.weasel.checkout.utils.nextString
import io.mustelidae.weasel.common.code.PayMethod
import java.time.LocalDate
import kotlin.random.Random

internal class PreparationTest

internal fun Preparation.Companion.aFixture(
    userId: String = Random.nextString(4),
    extraFields: Map<String, String?> = emptyMap(),
    rewardDate: LocalDate? = null,
    ignoreMethods: List<PayMethod>? = null
): Preparation {
    return Preparation(userId, extraFields, rewardDate, ignoreMethods).apply {
        this.addBy(Basket.aFixture())
    }
}
