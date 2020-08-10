package io.mustelidae.weasel.adjustment.common

import io.mustelidae.weasel.common.code.PayMethod
import java.lang.RuntimeException

open class AdjustmentException(message: String, open val causeBy: Map<String, Any?>? = null) : RuntimeException(message)

class SettlementException(
    message: String,
    payGateId: Long,
    payMethod: PayMethod,
    settlementId: Long? = null,
    transactionId: String? = null,
    transactionAmount: Double? = null,
    settlementAmount: Double? = null
) : AdjustmentException(
    message,
    mapOf(
        "payGateId" to payGateId,
        "대사 ID(${payMethod.name.toLowerCase()} settlementId)" to settlementId,
        "PG txNo(transactionId)" to transactionId,
        "결제 or 취소 금액(transactionAmount)" to transactionAmount,
        "대사 대상 금액(settlementAmount)" to transactionAmount
    )
)
