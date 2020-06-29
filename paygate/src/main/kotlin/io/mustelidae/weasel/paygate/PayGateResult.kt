package io.mustelidae.weasel.paygate

import io.mustelidae.weasel.paygate.common.PayMethod
import java.time.LocalDateTime

class PayGateResult {

    data class Pay(
        val isSuccess: Boolean,
        val tid: String
    ) {
        data class Detail(
            val payMethod: PayMethod,
            val confirmDate: LocalDateTime, // 승인일자
            val storeId: String, // 상점ID
            val owner: String? = null, // 결제자
            val paymentAmt: Long, // 거래금액
            val partCancel: Boolean, // 부분취소 가능 여부
            val message: String, // 지불 결과 메시지
            val methodInfo: MethodInfo // 결제수단 정보 json
        )
    }

    data class Cancel(
        val isSuccess: Boolean,
        val remainAmount: Long,
        val code: String? = null,
        val cause: String? = null
    )

    data class Error(
        val code: String? = null,
        val cause: String? = null
    )
}
