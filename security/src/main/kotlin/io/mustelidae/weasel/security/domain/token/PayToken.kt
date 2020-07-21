package io.mustelidae.weasel.security.domain.token

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class PayToken(
    val orderId: String,
    val userId: String,
    val payGateId: Long,
    val paymentAmount: Double
) {
    private val timestamp: LocalDateTime = LocalDateTime.now()

    fun encrypt(cryptoKey: String): String = Crypto(cryptoKey).enc(this)

    fun isExpired(minute: Int): Boolean {
        return ChronoUnit.MINUTES.between(timestamp, LocalDateTime.now()) > minute
    }

    companion object {
        fun decrypt(cryptoKey: String, token: String): PayToken {
            return Crypto(cryptoKey).dec(token, PayToken::class.java)
        }
    }
}
