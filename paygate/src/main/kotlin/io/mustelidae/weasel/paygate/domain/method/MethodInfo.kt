package io.mustelidae.weasel.paygate.domain.method

import io.mustelidae.weasel.paygate.common.PayMethod

interface MethodInfo {
    fun method(): PayMethod
    fun paidAmount(): Long
    fun name(): String
    fun getIdentity(): String?
    fun detail(): Map<String, Any>
}
