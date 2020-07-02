package io.mustelidae.weasel.paygate.domain.method

interface MethodInfo {
    fun paidAmount(): Long
    fun name(): String
    fun getIdentity(): String?
    fun detail(): Map<String, Any>
}
