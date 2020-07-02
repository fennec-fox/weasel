package io.mustelidae.weasel.paygate.domain.method

import io.mustelidae.weasel.paygate.common.PayMethod
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class NaverPayPoint(
    private val paidAmount: Long,
    val expectAccumulateDate: LocalDate
) : MethodInfo {
    override fun paidAmount(): Long = paidAmount

    override fun name(): String {
        return PayMethod.NAVER_PAY_POINT.name
    }

    override fun getIdentity(): String? {
        return null
    }

    override fun detail(): Map<String, Any> {
        return mapOf(
            "expectAccumulateDate" to expectAccumulateDate.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        )
    }
}
