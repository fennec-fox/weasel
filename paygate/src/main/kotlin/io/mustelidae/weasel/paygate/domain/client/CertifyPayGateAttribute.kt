package io.mustelidae.weasel.paygate.domain.client

/**
 * PG 화면에서 인증 후 전달 되는 데이터
 */
interface CertifyPayGateAttribute {
    fun validOrThrow()
}
