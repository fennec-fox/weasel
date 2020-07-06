package io.mustelidae.weasel.paygate.domain.interaction

import io.kotlintest.matchers.asClue
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.paygate.PayGateFinder
import io.mustelidae.weasel.paygate.domain.paygate.aFixture
import org.junit.jupiter.api.Test

internal class PayGateInteractionTest {

    private val payGateFinder: PayGateFinder = mockk()
    private val payGateEnvironment = PayGateEnvironment().apply {
        dummy.enable = true
    }

    @Test
    fun `cancel$paygate`() {
        // Given
        val payGateInteraction = PayGateInteraction(payGateFinder, payGateEnvironment)
        val payGate = PayGate.aFixture(PayGate.Company.INICIS)
        val cancel = PayGateResources.Cancel(
            "1234",
            true,
            1000,
            "is test"
        )
        // When
        every { payGateFinder.findOne(payGate.id!!) } returns payGate
        every { payGateFinder.findOneWithExpired(payGate.id!!) } returns payGate

        val canceled = payGateInteraction.cancel(payGate.id!!, cancel)
        // Then
        canceled.asClue {
            it.updatedTransactionId shouldBe null
            it.hasNewTransactionId() shouldBe false
            it.remainAmount shouldBe 0
        }
    }

    @Test
    fun `partialCancel$paygate`() {
        // Given
        val payGateInteraction = PayGateInteraction(payGateFinder, payGateEnvironment)
        val payGate = PayGate.aFixture(PayGate.Company.NAVER_PAY)
        val partialCancel = PayGateResources.PartialCancel(
            "5678",
            false,
            1000,
            500,
            "is test"
        )
        // When
        every { payGateFinder.findOne(payGate.id!!) } returns payGate
        every { payGateFinder.findOneWithExpired(payGate.id!!) } returns payGate

        val canceled = payGateInteraction.partialCancel(payGate.id!!, partialCancel)
        // Then
        canceled.asClue {
            it.updatedTransactionId shouldBe null
            it.hasNewTransactionId() shouldBe false
            it.remainAmount shouldBe 500
        }
    }
}
