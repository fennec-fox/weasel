package io.mustelidae.weasel.paygate.domain.payment.normal

import io.mustelidae.weasel.paygate.common.BankCode
import io.mustelidae.weasel.paygate.common.CreditCode
import io.mustelidae.weasel.paygate.config.PayGateException
import io.mustelidae.weasel.paygate.domain.client.CertifyPayGateAttribute
import io.mustelidae.weasel.paygate.domain.client.naverpay.NaverPayClient
import io.mustelidae.weasel.paygate.domain.client.naverpay.NaverPayResources
import io.mustelidae.weasel.paygate.domain.method.BankTransfer
import io.mustelidae.weasel.paygate.domain.method.CreditCard
import io.mustelidae.weasel.paygate.domain.method.MethodInfo
import io.mustelidae.weasel.paygate.domain.method.NaverPayPoint
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.payment.PayGateResources
import io.mustelidae.weasel.security.domain.token.PayToken
import java.time.LocalDate
import java.time.format.DateTimeFormatter

internal class NaverPayPayGateCorp(
    override val payGate: PayGate,
    private val naverPayClient: NaverPayClient
) : PayGateCorp {

    private lateinit var paid: NaverPayResources.Reply.Paid
    private lateinit var canceled: NaverPayResources.Reply.Canceled

    override fun pay(token: PayToken, certifyPayGateAttribute: CertifyPayGateAttribute): PayGateResources.Paid {
        val certifyAttribute = certifyPayGateAttribute as NaverPayResources.CertifyAttribute

        this.paid = naverPayClient.payment(
                NaverPayResources.Request.Pay(
                        token,
                        payGate.storeId,
                        payGate.storeKey ?: throw PayGateException("naver pay must storeKey"),
                        certifyAttribute.paymentId
                )
        )
        val detail = paid.detail
        val methods = mutableListOf<MethodInfo>()

        val primaryMethod: MethodInfo? = when (detail.primaryPayMeans) {
            "BANK" -> {
                val bankCode = BankCode.values().find { it.naverPay == detail.bankCorpCode }!!
                BankTransfer(
                    bankCode,
                    detail.primaryPayAmount,
                    detail.bankAccountNo
                )
            }
            "CARD" -> {
                val creditCode = CreditCode.values().find { it.naverPay == detail.cardCorpCode }!!
                val installment = (detail.cardInstCount != 0)
                CreditCard(
                    creditCode,
                    detail.cardNo,
                    detail.primaryPayAmount,
                    true,
                    installment,
                    null,
                    detail.cardInstCount.toString(),
                    null,
                    detail.cardAuthNo
                )
            }
            else -> null
        }

        primaryMethod?.let { methods.add(it) }

        if (detail.npointPayAmount > 0) {
            methods.add(
                NaverPayPoint(
                    detail.npointPayAmount,
                    LocalDate.parse(detail.useCfmYmdt, DateTimeFormatter.ofPattern("yyyyMMdd"))
                )
            )
        }

        return PayGateResources.Paid(
            paid.paymentId,
            token.orderId,
            paid.detail.totalPayAmount,
            true,
            paid.detail.admissionState,
            methods,
            paid.detail.getPaidDate(),
            paid.detail.extraDeduction,
            paid.detail.merchantUserKey
        )
    }

    override fun cancel(cancel: PayGateResources.Cancel): PayGateResources.Canceled {
        this.canceled = naverPayClient.cancel(
            NaverPayResources.Request.Cancel(
                payGate.storeId,
                payGate.storeKey ?: throw PayGateException("naver pay must storeKey"),
                cancel.transactionId,
                cancel.isAdmin,
                cancel.paidAmount,
                cancel.cause
            )
        )
        return PayGateResources.Canceled(
            canceled.detail.getCanceledDate(),
            0
        )
    }

    override fun partialCancel(partialCancel: PayGateResources.PartialCancel): PayGateResources.Canceled {
        this.canceled = naverPayClient.cancelOfPartial(
            NaverPayResources.Request.Cancel(
                payGate.storeId,
                payGate.storeKey ?: throw PayGateException("naver pay must storeKey"),
                partialCancel.transactionId,
                partialCancel.isAdmin,
                partialCancel.cancelAmount,
                partialCancel.cause
            ),
            partialCancel.currentAmount
        )

        return PayGateResources.Canceled(
            canceled.detail.getCanceledDate(),
            canceled.detail.totalRestAmount
        )
    }

    override fun loadAdjustment() {
        // TODO("Not yet implemented")
    }
}
