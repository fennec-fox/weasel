package io.mustelidae.weasel.paygate.domain.interaction

import io.kotlintest.matchers.asClue
import io.kotlintest.shouldBe
import io.kotlintest.shouldNotBe
import io.mockk.mockk
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.paygate.PayGateFinder
import io.mustelidae.weasel.paygate.domain.paygate.aFixture
import org.junit.jupiter.api.Test

internal class PayGateCancelInteractionTest {

    private val payGateFinder: PayGateFinder = mockk()
    private val payGateEnvironment = PayGateEnvironment().apply {
        dummy.enable = true
    }

    @Test
    fun `cancel$paygate`() {
        // Given
        val payGateCancelInteraction = PayGateCancelInteraction(payGateFinder, payGateEnvironment)
        val payGate = PayGate.aFixture(PayGate.Company.INICIS)
        val cancel = PayGateResources.Cancel(
            "1234",
            true,
            1000,
            "is test"
        )
        // When
        val canceled = payGateCancelInteraction.cancel(payGate, cancel)
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
        val payGateCancelInteraction = PayGateCancelInteraction(payGateFinder, payGateEnvironment)
        val payGate = PayGate.aFixture(PayGate.Company.INICIS)
        val partialCancel = PayGateResources.PartialCancel(
            "5678",
            false,
            1000,
            500,
            "is test"
        )
        // When
        val canceled = payGateCancelInteraction.partialCancel(payGate, partialCancel)
        // Then
        canceled.asClue {
            it.updatedTransactionId shouldNotBe null
            it.hasNewTransactionId() shouldBe true
            it.remainAmount shouldBe 500
        }
    }
}
