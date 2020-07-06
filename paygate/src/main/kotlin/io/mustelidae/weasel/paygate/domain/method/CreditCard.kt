package io.mustelidae.weasel.paygate.domain.method

import io.mustelidae.weasel.paygate.common.CreditCode
import io.mustelidae.weasel.paygate.common.PayMethod

data class CreditCard(
    val creditCode: CreditCode,
    private val number: String,
    private val paidAmount: Long, // 실 카드 결제 금액
    val canPartialCancel: Boolean,
    val installment: Boolean, // 할부(true), 일시불(false)
    val freeInstallment: Boolean? = null, // 무이자 여부
    val installmentMonth: String? = null, // 할부 개월 수
    val cardPoint: Long? = null, // 카드 포인트 사용
    val approveCode: String? = null
) : MethodInfo {

    internal var type: CreditType? = null // 카드 종류
    internal var purchaseCode: String? = null // 매입사 코드

    override fun method(): PayMethod = PayMethod.CREDIT

    override fun paidAmount(): Long = paidAmount

    override fun name(): String {
        return creditCode.name
    }

    override fun getIdentity(): String? {
        return number
    }

    override fun detail(): Map<String, Any> {
        val detailMap = mutableMapOf(
            "paidAmount" to paidAmount,
            "canPartialCancel" to canPartialCancel,
            "installment" to installment
        )

        if (installment) {
            freeInstallment?.let { detailMap["freeInstallment"] = it }
            installmentMonth?.let { detailMap["installmentMonth"] = it }
        }

        type?.let { detailMap["type"] = it.name }
        purchaseCode?.let { detailMap["purchaseCode"] = it }
        cardPoint?.let { detailMap["cardPoint"] = it }
        approveCode?.let { detailMap["approveCode"] = it }

        return detailMap
    }

    internal enum class CreditType {
        CREDIT, CHECK, GIFT
    }
}
