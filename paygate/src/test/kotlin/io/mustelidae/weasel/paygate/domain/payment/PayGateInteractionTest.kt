package io.mustelidae.weasel.paygate.domain.payment

import io.kotlintest.matchers.asClue
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mustelidae.weasel.common.code.PayMethod
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.client.inicis.InicisResources
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.paygate.PayGateFinder
import io.mustelidae.weasel.paygate.domain.paygate.aFixture
import io.mustelidae.weasel.security.domain.token.PayToken
import org.junit.jupiter.api.Test

internal class PayGateInteractionTest {

    private val payGateFinder: PayGateFinder = mockk()
    private val payGateEnvironment = PayGateEnvironment().apply {
        dummy.enable = true
    }

    @Test
    fun payByTokenInicis() {
        // Given
        val payGateInteraction = PayGateInteraction(payGateFinder, payGateEnvironment)
        val payGate = PayGate.aFixture(PayGate.Company.INICIS)
        val payToken = PayToken(
            "1",
            "fennecInicis",
            payGate.id!!,
            10000.0
        )
        val certifyCreditAttribute = InicisResources.CertifyCreditAttribute().apply {
            P_TID = "1231414"
            P_STATUS = "00"
            P_RMESG1 = "OK"
            P_REQ_URL = "http://localhost/inicis/ok"
            P_AMT = payToken.paymentAmount.toLong()
        }

        // When
        every { payGateFinder.findOrThrow(payGate.id!!) } returns payGate
        val paid = payGateInteraction.payByTokenPayGateId(payToken, certifyCreditAttribute)

        // Then
        paid.asClue {
            it.orderId shouldBe payToken.orderId
            it.methodInfos.size shouldBe 1
        }
        val credit = paid.methodInfos.first()

        credit.method() shouldBe PayMethod.CREDIT
    }

    @Test
    fun `cancel$paygate`() {
        // Given
        val payGateInteraction = PayGateInteraction(payGateFinder, payGateEnvironment)
        val payGate = PayGate.aFixture(PayGate.Company.INICIS)
        val cancel = PayGateResources.Cancel(
            "1234",
            true,
            1000.0,
            "is test"
        )
        // When
        every { payGateFinder.findOrThrow(payGate.id!!) } returns payGate
        every { payGateFinder.findOneWithExpired(payGate.id!!) } returns payGate

        val canceled = payGateInteraction.cancel(payGate.id!!, cancel)
        // Then
        canceled.asClue {
            it.updatedTransactionId shouldBe null
            it.hasNewTransactionId() shouldBe false
            it.remainAmount shouldBe 0.0
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
            1000.0,
            500.0,
            "is test"
        )
        // When
        every { payGateFinder.findOrThrow(payGate.id!!) } returns payGate
        every { payGateFinder.findOneWithExpired(payGate.id!!) } returns payGate

        val canceled = payGateInteraction.partialCancel(payGate.id!!, partialCancel)
        // Then
        canceled.asClue {
            it.updatedTransactionId shouldBe null
            it.hasNewTransactionId() shouldBe false
            it.remainAmount shouldBe 500.0
        }
    }
}
