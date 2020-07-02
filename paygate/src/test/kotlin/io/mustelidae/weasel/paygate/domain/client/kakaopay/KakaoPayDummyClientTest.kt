package io.mustelidae.weasel.paygate.domain.client.kakaopay

import io.kotlintest.shouldBe
import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.security.domain.token.PayToken
import org.junit.jupiter.api.Test

internal class KakaoPayDummyClientTest {

    private val dummy = PayGateEnvironment().dummy.apply {
        this.retry = true
        this.forcePayFail = false
        this.forceCancelFail = false
    }
    private val kakaoPayClient: KakaoPayClient = KakaoPayDummyClient(dummy)

    @Test
    fun prepare() {
        // Given
        val token = PayToken("1", "1,", 1, 1000)
        val request = KakaoPayResources.Request.Prepare(
            token,
            "1",
            "test",
            "1",
            1,
            "",
            null,
            null
        )
        // When
        // Then
        kakaoPayClient.prepare(request)
    }

    @Test
    fun pay() {
        // Given
        val token = PayToken("1", "1,", 1, 1000)
        val tid = "1"
        val request = KakaoPayResources.Request.Pay(
            token,
            tid,
            "1",
            "1"
        )
        // When
        val reply = kakaoPayClient.pay(request)
        // Then
        reply.tid shouldBe tid
    }
}
