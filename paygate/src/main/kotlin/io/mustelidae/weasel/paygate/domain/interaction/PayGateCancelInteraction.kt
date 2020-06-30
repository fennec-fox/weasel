package io.mustelidae.weasel.paygate.domain.interaction

import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.config.PayGateException
import io.mustelidae.weasel.paygate.domain.client.ClientHandler
import io.mustelidae.weasel.paygate.domain.client.inicis.InicisResources
import io.mustelidae.weasel.paygate.domain.client.kakaopay.KakaoPayResources
import io.mustelidae.weasel.paygate.domain.client.naverpay.NaverPayResources
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.paygate.PayGateFinder
import org.springframework.stereotype.Service

/**
 * PG사 별 취소 interaction
 * - 취소의 경우 payGate의 계약 종료로 인해 만료가 되었어도 취소가 가능 해야 한다.
 */
@Service
class PayGateCancelInteraction(
    private val payGateFinder: PayGateFinder,
    payGateEnvironment: PayGateEnvironment
) {
    private val clientHandler = ClientHandler(payGateEnvironment)

    /**
     * 결제 취소
     */
    fun cancel(payGateId: Long, cancel: PayGateResources.Cancel) {
        val payGate = payGateFinder.findOneWithExpired(payGateId)
        this.cancel(payGate, cancel)
    }

    internal fun cancel(payGate: PayGate, cancel: PayGateResources.Cancel): PayGateResources.Canceled {
        return when (payGate.company) {
            PayGate.Company.INICIS -> {
                val reply = clientHandler.inicis().cancel(
                    InicisResources.Request.Cancel(
                    payGate.storeId,
                    cancel.transactionId,
                    cancel.paidAmount,
                    cancel.cause
                ))
                PayGateResources.Canceled(reply.canceledDate, 0)
            }
            PayGate.Company.NAVER_PAY -> {
                val reply = clientHandler.naverPay().cancel(
                    NaverPayResources.Request.Cancel(
                    payGate.storeId,
                    payGate.storeKey ?: throw PayGateException("naver pay must storeKey"),
                    cancel.transactionId,
                    cancel.isAdmin,
                    cancel.paidAmount,
                    cancel.cause
                ))
                PayGateResources.Canceled(reply.getCanceledDate(), 0)
            }
            PayGate.Company.KAKAO_PAY -> {
                val reply = clientHandler.kakaoPay().cancel(
                    KakaoPayResources.Request.Cancel(
                    payGate.storeId,
                    cancel.transactionId,
                    cancel.paidAmount
                ))
                PayGateResources.Canceled(reply.canceledAt, 0)
            }
        }
    }

    /**
     * 결제 TransactionID 기준 금액 부분 취소
     * - inicis의 경우 새로운 transactionID가 생성 된다.
     */
    fun partialCancel(payGateId: Long, partialCancel: PayGateResources.PartialCancel): PayGateResources.Canceled {
        val payGate = payGateFinder.findOneWithExpired(payGateId)
        return this.partialCancel(payGate, partialCancel)
    }

    internal fun partialCancel(payGate: PayGate, partialCancel: PayGateResources.PartialCancel): PayGateResources.Canceled {
        return when (payGate.company) {
            PayGate.Company.INICIS -> {
                val reply = clientHandler.inicis().cancelOfPartial(
                    InicisResources.Request.Cancel(
                        payGate.storeId,
                        partialCancel.transactionId,
                        partialCancel.cancelAmount,
                        partialCancel.cause
                    ),
                    partialCancel.currentAmount
                )
                PayGateResources.Canceled(reply.canceledDate, reply.remainAmount, reply.updatedTid)
            }
            PayGate.Company.NAVER_PAY -> {
                val reply = clientHandler.naverPay().cancelOfPartial(
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
                PayGateResources.Canceled(reply.getCanceledDate(), reply.totalRestAmount)
            }
            PayGate.Company.KAKAO_PAY -> {
                val reply = clientHandler.kakaoPay().cancelOfPartial(
                    KakaoPayResources.Request.Cancel(
                        payGate.storeId,
                        partialCancel.transactionId,
                        partialCancel.cancelAmount
                    ),
                    partialCancel.currentAmount
                )
                PayGateResources.Canceled(reply.canceledAt, 0)
            }
        }
    }
}
