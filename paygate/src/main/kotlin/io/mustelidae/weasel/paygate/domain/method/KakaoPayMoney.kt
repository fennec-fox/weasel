package io.mustelidae.weasel.paygate.domain.method

import io.mustelidae.weasel.paygate.common.PayMethod

data class KakaoPayMoney(
    private val paidAmount: Long
) : MethodInfo {
    override fun method(): PayMethod = PayMethod.KAKAO_PAY_MONEY
    override fun paidAmount(): Long = paidAmount
    override fun name(): String {
        return PayMethod.KAKAO_PAY_MONEY.name
    }

    override fun getIdentity(): String? {
        return null
    }

    override fun detail(): Map<String, Any> {
        return mapOf()
    }
}
