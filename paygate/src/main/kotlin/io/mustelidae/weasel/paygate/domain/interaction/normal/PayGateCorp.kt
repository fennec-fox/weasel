package io.mustelidae.weasel.paygate.domain.interaction.normal

import io.mustelidae.weasel.paygate.domain.client.CertifyPayGateAttribute
import io.mustelidae.weasel.paygate.domain.interaction.PayGateResources
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.security.domain.token.PayToken

/**
 * PG 결제, 취소
 * - 취소의 경우 payGate의 계약 종료로 인해 만료가 되었어도 취소가 가능 해야 한다.
 */
internal interface PayGateCorp {

    val payGate: PayGate

    fun pay(token: PayToken, certifyPayGateAttribute: CertifyPayGateAttribute): PayGateResources.Paid

    /**
     * 결제 취소
     */
    fun cancel(cancel: PayGateResources.Cancel): PayGateResources.Canceled

    /**
     * 결제 부분 취소
     */
    fun partialCancel(partialCancel: PayGateResources.PartialCancel): PayGateResources.Canceled

    fun loadAdjustment()
}
