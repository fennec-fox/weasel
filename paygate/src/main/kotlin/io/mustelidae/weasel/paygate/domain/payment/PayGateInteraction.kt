package io.mustelidae.weasel.paygate.domain.payment

import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.config.PayGateException
import io.mustelidae.weasel.paygate.domain.client.CertifyPayGateAttribute
import io.mustelidae.weasel.paygate.domain.paygate.PayGateFinder
import io.mustelidae.weasel.paygate.domain.payment.normal.PayGateCorpHandler
import io.mustelidae.weasel.security.domain.token.PayToken
import org.springframework.stereotype.Service

/**
 * 결제하기
 * mental model: 카드 긁기, 긁은 카드 취소 하기
 */
@Service
class PayGateInteraction(
    private val payGateFinder: PayGateFinder,
    payGateEnvironment: PayGateEnvironment
) {
    private val payGateCompanyHandler =
        PayGateCorpHandler(
            payGateFinder,
            payGateEnvironment
        )

    /**
     * 결제 토큰을 이용한 결제
     */
    fun payByTokenPayGateId(token: PayToken, certifyPayGateAttribute: CertifyPayGateAttribute): PayGateResources.Paid {
        val payGateCorp = payGateCompanyHandler.getCorp(token.payGateId)

        val paid = payGateCorp.pay(token, certifyPayGateAttribute)

        payGateCorp.loadAdjustment()

        return paid
    }

    /**
     * 결제 토큰은 무시하고 PaygateID를 받아서 결제
     */
    fun payByReassignPayGateId(
        token: PayToken,
        certifyPayGateAttribute: CertifyPayGateAttribute,
        reassignedPayGateId: Long
    ): PayGateResources.Paid {
        val payGateCorp = payGateCompanyHandler.getCorp(reassignedPayGateId)
        val payGateOfToken = payGateFinder.findOrThrow(token.payGateId)

        if (payGateCorp.payGate.company != payGateOfToken.company)
            throw PayGateException("결제 시 선택한 PG와 결제를 진행하려는 PG가 다른 회사입니다.")

        val paid = payGateCorp.pay(token, certifyPayGateAttribute)

        payGateCorp.loadAdjustment()

        return paid
    }

    /**
     * 취소
     */
    fun cancel(payGateId: Long, cancel: PayGateResources.Cancel): PayGateResources.Canceled {
        val payGateCorp = payGateCompanyHandler.getCorpWithExpired(payGateId)

        val canceled = payGateCorp.cancel(cancel)

        payGateCorp.loadAdjustment()

        return canceled
    }

    /**
     * 결제 금액 일부 취소
     */
    fun partialCancel(payGateId: Long, partialCancel: PayGateResources.PartialCancel): PayGateResources.Canceled {
        val payGateCorp = payGateCompanyHandler.getCorpWithExpired(payGateId)

        val canceled = payGateCorp.partialCancel(partialCancel)

        payGateCorp.loadAdjustment()

        return canceled
    }
}
