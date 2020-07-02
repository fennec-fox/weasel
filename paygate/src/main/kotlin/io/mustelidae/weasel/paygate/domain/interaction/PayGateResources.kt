package io.mustelidae.weasel.paygate.domain.interaction

import io.mustelidae.weasel.paygate.domain.method.MethodInfo
import io.mustelidae.weasel.security.domain.token.PayToken
import java.time.LocalDateTime

class PayGateResources {

    data class Cancel(
        val transactionId: String,
        val isAdmin: Boolean,
        val paidAmount: Long,
        val cause: String? = null
    )

    data class Canceled(
        val canceledDate: LocalDateTime,
        val remainAmount: Long,
        val updatedTransactionId: String? = null
    ) {
        fun hasNewTransactionId(): Boolean {
            return (updatedTransactionId != null)
        }
    }

    data class PartialCancel(
        val transactionId: String,
        val isAdmin: Boolean,
        val currentAmount: Long,
        val cancelAmount: Long,
        val cause: String? = null
    )

    data class Paid(
        val transactionId: String,
        val orderId: String,
        val paymentAmount: Long,
        val canPartialCancel: Boolean,
        val payGateResultMessage: String,
        val methodInfos: List<MethodInfo>,
        val paidDate: LocalDateTime,
        val extraDeduction: Boolean? = null,
        val owner: String? = null
    )

    data class PrepareProduct(
        val token: PayToken,
        val goods: List<Good>
    ) {
        data class Good(
            val name: String,
            val amount: Long
        )
    }

    data class PreparedPayment(
        val transactionId: String
    )
}
