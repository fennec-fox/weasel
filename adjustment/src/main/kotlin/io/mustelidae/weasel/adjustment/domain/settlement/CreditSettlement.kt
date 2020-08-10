package io.mustelidae.weasel.adjustment.domain.settlement

import io.mustelidae.weasel.adjustment.common.SettlementException
import io.mustelidae.weasel.common.code.PayMethod
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
class CreditSettlement(
    /* PayGate Id */
    val payGateId: Long,
    /* PG사 ID */
    val storeId: String,
    /* CP ID */
    val cpId: Long,
    /* PG사 결제 수수료 요율 */
    val feeRate: Double,
    /* 주문번호 */
    val orderId: String,
    /* 사용자 ID */
    val userId: String,
    /* PG사 txNo */
    val transactionId: String,
    /* PG 통신 시간 */
    val transactionDate: LocalDateTime,
    /* PG사 결제, 취소 금액 */
    val transactionAmount: Double,
    /* PG사 지불 수수료 */
    val feeAmount: Double,
    /* PG사와 대사를 할 금액 */
    val settlementAmount: Double,
    /* 부가세 포함 여부  */
    val vat: Boolean,
    /* 상태 */
    val status: Status,
    /* 연결 된 PG사 txNo */
    val refTransactionId: String? = null,
    /* 연결 된 대사 ID*/
    val refSettlementId: Long? = null,
    /* 매입사 코드 */
    val acquirerCode: String? = null,
    /* 발급사 코드 */
    val issuerCode: String? = null
) {

    @Id
    @GeneratedValue
    var id: Long? = null
        private set

    enum class Status {
        PAID,
        ALL_CANCEL,
        PARTIAL_CANCEL
    }

    companion object {
        fun paid(
            payGateId: Long,
            storeId: String,
            cpId: Long,
            feeRate: Double,
            orderId: String,
            userId: String,
            transactionId: String,
            transactionDate: LocalDateTime,
            transactionAmount: Double,
            feeAmount: Double,
            settlementAmount: Double,
            vat: Boolean,
            acquirerCode: String? = null,
            issuerCode: String? = null
        ): CreditSettlement {
            return CreditSettlement(
                payGateId,
                storeId,
                cpId,
                feeRate,
                orderId,
                userId,
                transactionId,
                transactionDate,
                transactionAmount,
                feeAmount,
                settlementAmount,
                vat,
                Status.PAID,
                acquirerCode = acquirerCode,
                issuerCode = issuerCode
            )
        }

        /* 양수로 입력 해야 함. */
        fun canceled(
            transactionId: String,
            transactionDate: LocalDateTime,
            paidCreditSettlement: CreditSettlement
        ): CreditSettlement {
            return CreditSettlement(
                paidCreditSettlement.payGateId,
                paidCreditSettlement.storeId,
                paidCreditSettlement.cpId,
                paidCreditSettlement.feeRate,
                paidCreditSettlement.orderId,
                paidCreditSettlement.userId,
                transactionId,
                transactionDate,
                paidCreditSettlement.transactionAmount * -1,
                paidCreditSettlement.feeAmount * -1,
                paidCreditSettlement.settlementAmount * -1,
                paidCreditSettlement.vat,
                Status.ALL_CANCEL,
                paidCreditSettlement.transactionId,
                paidCreditSettlement.id!!
            )
        }

        /* 양수로 입력 해야 함. */
        fun partialCancel(
            transactionId: String,
            transactionDate: LocalDateTime,
            transactionAmount: Double,
            feeAmount: Double,
            settlementAmount: Double,
            paidCreditSettlement: CreditSettlement
        ): CreditSettlement {

            if (transactionAmount < 0 || settlementAmount < 0)
                throw SettlementException(
                    "부분 취소 시 대사 대상 금액은 항상 양수 이어야 합니다.",
                    paidCreditSettlement.payGateId,
                    PayMethod.CREDIT,
                    transactionId = transactionId,
                    transactionAmount = transactionAmount,
                    settlementAmount = settlementAmount
                )

            return CreditSettlement(
                paidCreditSettlement.payGateId,
                paidCreditSettlement.storeId,
                paidCreditSettlement.cpId,
                paidCreditSettlement.feeRate,
                paidCreditSettlement.orderId,
                paidCreditSettlement.userId,
                transactionId,
                transactionDate,
                transactionAmount * -1,
                feeAmount,
                settlementAmount,
                paidCreditSettlement.vat,
                Status.PARTIAL_CANCEL,
                paidCreditSettlement.transactionId,
                paidCreditSettlement.id!!
            )
        }
    }
}
