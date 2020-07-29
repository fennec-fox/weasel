package io.mustelidae.weasel.paygate.domain.payment

import io.kotlintest.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.paygate.PayGateFinder
import io.mustelidae.weasel.paygate.domain.paygate.aFixture
import io.mustelidae.weasel.paygate.domain.payment.PayGateResources.PrepareProduct
import io.mustelidae.weasel.security.domain.token.PayToken
import org.junit.jupiter.api.Test

internal class PrepareInteractionTest {

    private val payGateFinder: PayGateFinder = mockk()
    private val payGateEnvironment = PayGateEnvironment().apply {
        dummy.enable = true
    }

    @Test
    fun kakaoPay() {
        // Given
        val prepareInteraction = PrepareInteraction(payGateFinder, payGateEnvironment)
        val payGate = PayGate.aFixture(PayGate.Company.KAKAO_PAY)
        val payToken = PayToken(
            "1",
            "fennec-fox",
            payGate.id!!,
            1000.0
        )
        // When
        every { payGateFinder.findOrThrow(payGate.id!!) } returns payGate

        val prepared = prepareInteraction.kakaoPay(
            PrepareProduct(
                payToken,
                listOf(
                    PrepareProduct.Good(
                        "1",
                        "test",
                        1,
                        1000.0
                    )
                ),
                PrepareProduct.Device.ANY
            ).apply {
                addPostBackUrl("http://localhost:8080")
            }
        )

        // Then
        prepared.redirectUrl shouldNotBe null
    }
}
