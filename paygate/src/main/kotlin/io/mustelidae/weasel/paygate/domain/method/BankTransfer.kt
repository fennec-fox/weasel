package io.mustelidae.weasel.paygate.domain.method

import io.mustelidae.weasel.common.code.BankCode
import io.mustelidae.weasel.common.code.PayMethod

data class BankTransfer(
    val bankCode: BankCode,
    private val paidAmount: Long,
    private val accountNumber: String? = null
) : MethodInfo {
    override fun method(): PayMethod = PayMethod.BANK_TRANSFER

    override fun paidAmount(): Long = paidAmount

    override fun name(): String {
        return bankCode.name
    }

    override fun getIdentity(): String? {
        return accountNumber
    }

    override fun detail(): Map<String, Any> {
        return mapOf()
    }
}
