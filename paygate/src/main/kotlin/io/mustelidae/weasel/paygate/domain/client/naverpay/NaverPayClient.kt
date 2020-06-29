package io.mustelidae.weasel.paygate.domain.client.naverpay

internal interface NaverPayClient {

    fun payment(request: NaverPayResources.Request.Pay): NaverPayResources.Reply.Paid

    fun cancel(request: NaverPayResources.Request.Cancel): NaverPayResources.Reply.Canceled

    fun cancelOfPartial(
        request: NaverPayResources.Request.Cancel,
        cancellableAmount: Long
    ): NaverPayResources.Reply.Canceled
}
