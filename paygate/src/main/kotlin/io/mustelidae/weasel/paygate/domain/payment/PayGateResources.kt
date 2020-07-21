package io.mustelidae.weasel.paygate.domain.payment

import io.mustelidae.weasel.paygate.domain.method.MethodInfo
import io.mustelidae.weasel.security.domain.token.PayToken
import java.time.LocalDateTime

class PayGateResources {

    data class Cancel(
        val transactionId: String,
        val isAdmin: Boolean,
        val paidAmount: Double,
        val cause: String? = null
    )

    data class Canceled(
        val canceledDate: LocalDateTime,
        val remainAmount: Double,
        val updatedTransactionId: String? = null
    ) {
        fun hasNewTransactionId(): Boolean {
            return (updatedTransactionId != null)
        }
    }

    data class PartialCancel(
        val transactionId: String,
        val isAdmin: Boolean,
        val currentAmount: Double,
        val cancelAmount: Double,
        val cause: String? = null
    )

    data class Paid(
        val transactionId: String,
        val orderId: String,
        val paymentAmount: Double,
        val canPartialCancel: Boolean,
        val payGateResultMessage: String,
        val methodInfos: List<MethodInfo>,
        val paidDate: LocalDateTime,
        val extraDeduction: Boolean? = null,
        val owner: String? = null
    )

    data class PrepareProduct(
        val token: PayToken,
        val goods: List<Good>,
        val device: Device
    ) {
        data class Good(
            val id: String,
            val name: String,
            val quantity: Int,
            val amount: Double
        )

        var approvalUrl: String? = null
            private set
        var cancelUrl: String? = null
            private set
        var failUrl: String? = null
            private set

        fun addPostBackUrl(approvalUrl: String, cancelUrl: String? = null, failUrl: String? = null) {
            this.approvalUrl = approvalUrl
            this.cancelUrl = cancelUrl
            this.failUrl = failUrl
        }

        enum class Device {
            APP, MOBILE_WEB, PC_WEB, ANY
        }
    }

    data class Prepared(
        val transactionId: String,
        val redirectUrl: String? = null,
        val androidAppScheme: String? = null,
        val iosAppScheme: String? = null
    )
}
