package io.mustelidae.weasel.paygate.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class PayGateEnvironment {

    val dummy = Dummy()
    val naverPay = NaverPay()
    val kakaoPay = KakaoPay()
    val inicis = Inicis()

    class Dummy {
        /* enable dummy */
        @Value(value = "\${app.payGate.dummy.enable}")
        var enable: Boolean = false

        /* dummy test always pay fail */
        @Value(value = "\${app.payGate.dummy.forcePayFail}")
        var forcePayFail: Boolean = false

        @Value(value = "\${app.payGate.dummy.retryPay}")
        var retry: Boolean = false

        /* always cancel fail */
        @Value(value = "\${app.payGate.dummy.forceCancelFail}")
        var forceCancelFail: Boolean = false
    }

    class NaverPay {
        /* api url */
        @Value(value = "\${app.payGate.naverPay.host}")
        lateinit var host: String

        /* partner id */
        @Value(value = "\${app.payGate.naverPay.partnerId}")
        lateinit var partnerId: String

        /* 그룹형 연동 secret key (chainId) */
        @Value(value = "\${app.payGate.naverPay.clientSecret}")
        lateinit var clientSecret: String

        /* api timeout */
        @Value(value = "\${app.payGate.naverPay.timeout}")
        var timeout: Int = 3000

        /* write log */
        @Value(value = "\${app.payGate.naverPay.writeLog}")
        var writeLog: Boolean = false
    }

    class Inicis {
        /* api timeout */
        @Value(value = "\${app.payGate.inicis.timeout}")
        var timeout: Int = 3000

        /* write log */
        @Value(value = "\${app.payGate.inicis.writeLog}")
        var writeLog: Boolean = false

        @Value(value = "\${app.payGate.inicis.libPath}")
        lateinit var libPath: String
    }

    class KakaoPay {
        @Value(value = "\${app.payGate.kakaoPay.host}")
        lateinit var host: String

        /* 발급 받은 API Key */
        @Value(value = "\${app.payGate.kakaoPay.apiKey}")
        lateinit var apiKey: String

        @Value(value = "\${app.payGate.kakaoPay.timeout}")
        var timeout: Int = 3000

        @Value(value = "\${app.payGate.kakaoPay.writeLog}")
        var writeLog: Boolean = false
    }
}
