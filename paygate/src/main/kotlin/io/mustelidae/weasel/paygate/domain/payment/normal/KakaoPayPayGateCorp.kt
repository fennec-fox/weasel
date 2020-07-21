package io.mustelidae.weasel.paygate.domain.payment.normal

import io.mustelidae.weasel.common.code.CreditCode
import io.mustelidae.weasel.paygate.domain.client.CertifyPayGateAttribute
import io.mustelidae.weasel.paygate.domain.client.kakaopay.KakaoPayClient
import io.mustelidae.weasel.paygate.domain.client.kakaopay.KakaoPayResources
import io.mustelidae.weasel.paygate.domain.method.CreditCard
import io.mustelidae.weasel.paygate.domain.method.KakaoPayMoney
import io.mustelidae.weasel.paygate.domain.method.MethodInfo
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.payment.PayGateResources
import io.mustelidae.weasel.security.domain.token.PayToken

internal class KakaoPayPayGateCorp(
    override val payGate: PayGate,
    private val kakaoPayClient: KakaoPayClient
) : PayGateCorp {

    private lateinit var paid: KakaoPayResources.Reply.Paid
    private lateinit var canceled: KakaoPayResources.Reply.Canceled

    override fun pay(token: PayToken, certifyPayGateAttribute: CertifyPayGateAttribute): PayGateResources.Paid {
        val certifyAttribute = certifyPayGateAttribute as KakaoPayResources.CertifyAttribute

        this.paid = kakaoPayClient.pay(
            KakaoPayResources.Request.Pay(
                token,
                certifyAttribute.tid,
                payGate.storeId,
                certifyAttribute.pg_token
            )
        )
        val methods = mutableListOf<MethodInfo>()
        val methodInfo: MethodInfo? = when (paid.paymentMethodType) {
            "CARD" -> {
                val cardInfo = paid.cardInfo!!
                val creditCode = CreditCode.values().find { it.kakaoPay == cardInfo.issuerCorpCode }!!
                val installment = (cardInfo.installMonth != "00")
                val isFreeInstallment = (cardInfo.interestFreeInstall == "Y")
                CreditCard(
                    creditCode,
                    cardInfo.bin,
                    paid.amount.total - paid.amount.point - paid.amount.discount,
                    true,
                    installment,
                    isFreeInstallment,
                    cardInfo.installMonth,
                    null,
                    cardInfo.approvedId
                ).apply {
                    type = if (cardInfo.cardType == "체크") CreditCard.CreditType.CHECK else CreditCard.CreditType.CREDIT
                    purchaseCode = cardInfo.purchaseCorpCode
                }
            }
            "MONEY" -> {
                KakaoPayMoney(
                    paid.amount.total - paid.amount.point - paid.amount.discount
                )
            }
            else -> null
        }

        methodInfo?.let { methods.add(it) }

        return PayGateResources.Paid(
            paid.tid,
            paid.partnerOrderId,
            paid.amount.total.toDouble(),
            true,
            paid.paymentMethodType,
            methods,
            paid.createdAt,
            null,
            paid.partnerUserId

        )
    }

    override fun cancel(cancel: PayGateResources.Cancel): PayGateResources.Canceled {
        this.canceled = kakaoPayClient.cancel(
            KakaoPayResources.Request.Cancel(
                payGate.storeId,
                cancel.transactionId,
                cancel.paidAmount.toLong()
            ))
        return PayGateResources.Canceled(
            canceled.canceledAt,
            0.0
        )
    }

    override fun partialCancel(partialCancel: PayGateResources.PartialCancel): PayGateResources.Canceled {
        this.canceled = kakaoPayClient.cancelOfPartial(
            KakaoPayResources.Request.Cancel(
                payGate.storeId,
                partialCancel.transactionId,
                partialCancel.cancelAmount.toLong()
            ),
            partialCancel.currentAmount.toLong()
        )
        return PayGateResources.Canceled(
            canceled.canceledAt,
            0.0
        )
    }

    override fun loadAdjustment() {
        // TODO("Not yet implemented")
    }
}
