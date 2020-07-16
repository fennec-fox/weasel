package io.mustelidae.weasel.paygate.domain.client.inicis

import io.mustelidae.weasel.common.code.CreditCode
import io.mustelidae.weasel.paygate.config.PayGateClientException
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.client.AbstractDummySupport
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import java.time.LocalDateTime

internal class InicisDummyClient(
    private val dummy: PayGateEnvironment.Dummy
) : InicisClient, AbstractDummySupport() {

    private val company = PayGate.Company.INICIS

    override fun payment(request: InicisResources.Request.Pay): InicisResources.Reply.Paid {

        if (dummy.forcePayFail)
            throw PayGateClientException(
                company,
                "강제 결제 실패 처리 (force-pay-fail)",
                dummy.retry,
                "1315"
            )

        return InicisResources.Reply.Paid(
            request.token.userId,
            request.token.orderId,
            request.tid,
            request.payMethod,
            LocalDateTime.now(),
            request.storeId,
            "Weasel",
            request.token.paymentAmount
        ).apply {
            credit = InicisResources.Reply.Paid.Credit(
                CreditCode.AMEX.inicis,
                "1234-5678-9010-1234",
                canPartialCancel = true,
                isInterestFree = false,
                isCheckCard = true,
                interestMonth = "10",
                applyPrice = request.token.paymentAmount,
                approveCode = "1235143",
                name = CreditCode.AMEX.name,
                issuer = null,
                purchaseCode = null,
                cardPoint = null
            )
        }
    }

    override fun cancel(request: InicisResources.Request.Cancel): InicisResources.Reply.Canceled {

        if (dummy.forceCancelFail)
            throw PayGateClientException(
                company,
                "강제 취소 실패 처리(force-cancel-fail)",
                dummy.retry
            )

        return InicisResources.Reply.Canceled(
            request.tid,
            LocalDateTime.now(),
            request.cancelAmount,
            0
        )
    }

    override fun cancelOfPartial(
        request: InicisResources.Request.Cancel,
        cancellableAmount: Long
    ): InicisResources.Reply.Canceled {

        if (dummy.forceCancelFail)
            throw PayGateClientException(
                company,
                "강제 취소 실패 처리(force-cancel-fail)",
                dummy.retry
            )

        return InicisResources.Reply.Canceled(
            super.keyOfVan(),
            LocalDateTime.now(),
            request.cancelAmount,
            cancellableAmount - request.cancelAmount
        )
    }
}
