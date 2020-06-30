package io.mustelidae.weasel.paygate.domain.interaction

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
}
