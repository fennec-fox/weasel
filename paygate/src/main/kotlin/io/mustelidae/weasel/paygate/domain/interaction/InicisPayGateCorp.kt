package io.mustelidae.weasel.paygate.domain.interaction

import io.mustelidae.weasel.paygate.common.CreditCode
import io.mustelidae.weasel.paygate.common.PayMethod
import io.mustelidae.weasel.paygate.config.NotSupportPayMethodException
import io.mustelidae.weasel.paygate.domain.client.CertifyPayGateAttribute
import io.mustelidae.weasel.paygate.domain.client.inicis.InicisClient
import io.mustelidae.weasel.paygate.domain.client.inicis.InicisResources
import io.mustelidae.weasel.paygate.domain.method.CreditCard
import io.mustelidae.weasel.paygate.domain.method.MethodInfo
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.security.domain.token.PayToken

internal class InicisPayGateCorp(
    override val payGate: PayGate,
    private val inicisClient: InicisClient
) : PayGateCorp {

    lateinit var paid: InicisResources.Reply.Paid

    override fun pay(token: PayToken, certifyPayGateAttribute: CertifyPayGateAttribute): PayGateResources.Paid {

        return when (certifyPayGateAttribute) {
            is InicisResources.CertifyCreditAttribute -> {
                creditPay(token, payGate, certifyPayGateAttribute)
            }
            else -> throw NotSupportPayMethodException("지원하지 않는 결제수단 입니다.")
        }
    }

    private fun creditPay(token: PayToken, payGate: PayGate, certifyCreditAttribute: InicisResources.CertifyCreditAttribute): PayGateResources.Paid {
        this.paid = inicisClient.payment(
            InicisResources.Request.Pay(
                token,
                certifyCreditAttribute.P_TID!!,
                certifyCreditAttribute.P_REQ_URL,
                payGate.storeId,
                PayMethod.CREDIT
            )
        )
        val methods = mutableListOf<MethodInfo>()

        val credit = paid.credit!!
        val creditCode = CreditCode.values().find { it.inicis == credit.code }!!
        val installment = (credit.interestMonth != "00")
        val creditCard = CreditCard(
            creditCode,
            credit.number,
            paid.paymentAmount - (credit.cardPoint ?: 0),
            credit.canPartialCancel,
            installment,
            credit.isInterestFree,
            credit.interestMonth,
            credit.cardPoint,
            credit.approveCode
        ).apply {
            type = if (credit.isCheckCard) CreditCard.CreditType.CHECK else CreditCard.CreditType.CREDIT
            purchaseCode = credit.purchaseCode
        }

        methods.add(creditCard)

        return PayGateResources.Paid(
            paid.tid,
            paid.orderId,
            paid.paymentAmount,
            credit.canPartialCancel,
            "",
            methods,
            paid.payedDate,
            null,
            paid.userId
        )
    }

    override fun cancel(cancel: PayGateResources.Cancel): PayGateResources.Canceled {
        val reply = inicisClient.cancel(
            InicisResources.Request.Cancel(
                payGate.storeId,
                cancel.transactionId,
                cancel.paidAmount,
                cancel.cause
            ))
        return PayGateResources.Canceled(
            reply.canceledDate,
            0
        )
    }

    override fun partialCancel(partialCancel: PayGateResources.PartialCancel): PayGateResources.Canceled {
        val reply = inicisClient.cancelOfPartial(
            InicisResources.Request.Cancel(
                payGate.storeId,
                partialCancel.transactionId,
                partialCancel.cancelAmount,
                partialCancel.cause
            ),
            partialCancel.currentAmount
        )
        return PayGateResources.Canceled(
            reply.canceledDate,
            reply.remainAmount,
            reply.updatedTid
        )
    }

    override fun loadAdjustment() {
        TODO("Not yet implemented")
    }
}
