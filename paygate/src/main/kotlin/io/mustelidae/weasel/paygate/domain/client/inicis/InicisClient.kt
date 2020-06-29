package io.mustelidae.weasel.paygate.domain.client.inicis

internal interface InicisClient {

    fun payment(request: InicisResources.Request.Pay): InicisResources.Reply.Paid

    fun cancel(request: InicisResources.Request.Cancel): InicisResources.Reply.Canceled

    fun cancelOfPartial(
        request: InicisResources.Request.Cancel,
        cancellableAmount: Long
    ): InicisResources.Reply.Canceled
}
