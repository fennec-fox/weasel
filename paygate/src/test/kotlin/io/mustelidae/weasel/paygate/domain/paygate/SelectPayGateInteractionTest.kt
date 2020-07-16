package io.mustelidae.weasel.paygate.domain.paygate

import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mustelidae.weasel.common.code.PayMethod
import io.mustelidae.weasel.paygate.utils.invokeId
import org.junit.jupiter.api.Test

internal class SelectPayGateInteractionTest {

    private val payGateFinder: PayGateFinder = mockk()

    @Test
    fun pickRatio() {
        // Given
        val selectPayGateInteraction = SelectPayGateInteraction(payGateFinder)
        val payGates = listOf(
            PayGate(PayGate.Company.INICIS, "inicis", PayGate.Type.ONLINE, PayMethod.CREDIT, false, "").apply {
                invokeId(this, 1)
                ratio = 0.0
            },
            PayGate(PayGate.Company.NAVER_PAY, "naver_pay", PayGate.Type.ONLINE, PayMethod.CREDIT, false, "").apply {
                invokeId(this, 2)
                ratio = 100.0
            }
        )
        val type = PayGate.Type.ONLINE
        val payMethod = PayMethod.CREDIT

        // When
        every { payGateFinder.findAll(type, payMethod) } returns payGates

        val payGate = selectPayGateInteraction.pickRatio(type, payMethod)

        // Then
        payGate.id shouldBe 2
    }
}
