package io.mustelidae.weasel.paygate.domain.client

import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.client.inicis.InicisClient
import io.mustelidae.weasel.paygate.domain.client.inicis.InicisDummyClient
import io.mustelidae.weasel.paygate.domain.client.inicis.InicisStableClient
import io.mustelidae.weasel.paygate.domain.client.kakaopay.KakaoPayClient
import io.mustelidae.weasel.paygate.domain.client.kakaopay.KakaoPayDummyClient
import io.mustelidae.weasel.paygate.domain.client.kakaopay.KakaoPayStableClient
import io.mustelidae.weasel.paygate.domain.client.naverpay.NaverPayClient
import io.mustelidae.weasel.paygate.domain.client.naverpay.NaverPayDummyClient
import io.mustelidae.weasel.paygate.domain.client.naverpay.NaverPayStableClient

internal class ClientHandler(
    private val payGateEnvironment: PayGateEnvironment
) {

    private val enableDummy = payGateEnvironment.dummy.enable

    fun naverPay(): NaverPayClient {
        return if (enableDummy)
            NaverPayDummyClient(payGateEnvironment.dummy)
        else
            NaverPayStableClient(payGateEnvironment.naverPay)
    }

    fun kakaoPay(): KakaoPayClient {
        return if (enableDummy)
            KakaoPayDummyClient(payGateEnvironment.dummy)
        else
            KakaoPayStableClient(payGateEnvironment.kakaoPay)
    }

    fun inicis(): InicisClient {
        return if (enableDummy)
            InicisDummyClient(payGateEnvironment.dummy)
        else
            InicisStableClient(payGateEnvironment.inicis)
    }
}
