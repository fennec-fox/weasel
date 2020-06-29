package io.mustelidae.weasel.paygate.domain.client.kakaopay

interface KakaoPayClient {

    fun prepare(request: KakaoPayResources.Request.Prepare): KakaoPayResources.Reply.Prepared

    fun pay(request: KakaoPayResources.Request.Pay): KakaoPayResources.Reply.Paid

    fun cancel(request: KakaoPayResources.Request.Cancel): KakaoPayResources.Reply.Canceled

    fun cancelOfPartial(request: KakaoPayResources.Request.Cancel, cancellableAmount: Long): KakaoPayResources.Reply.Canceled
}
