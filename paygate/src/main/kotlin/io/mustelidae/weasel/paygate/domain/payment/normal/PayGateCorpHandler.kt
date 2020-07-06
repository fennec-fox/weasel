package io.mustelidae.weasel.paygate.domain.payment.normal

import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.domain.client.ClientHandler
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.paygate.PayGateFinder

internal class PayGateCorpHandler(
    private val payGateFinder: PayGateFinder,
    payGateEnvironment: PayGateEnvironment
) {
    private val clientHandler = ClientHandler(payGateEnvironment)

    fun getCorp(payGateId: Long): PayGateCorp {
        val payGate = payGateFinder.findOne(payGateId)
        return this.getCorp(payGate)
    }

    fun getCorpWithExpired(payGateId: Long): PayGateCorp {
        val payGate = payGateFinder.findOneWithExpired(payGateId)
        return this.getCorp(payGate)
    }

    private fun getCorp(payGate: PayGate): PayGateCorp {
        return when (payGate.company) {
            PayGate.Company.INICIS -> InicisPayGateCorp(
                payGate,
                clientHandler.inicis()
            )
            PayGate.Company.NAVER_PAY -> NaverPayPayGateCorp(
                payGate,
                clientHandler.naverPay()
            )
            PayGate.Company.KAKAO_PAY -> KakaoPayPayGateCorp(
                payGate,
                clientHandler.kakaoPay()
            )
        }
    }
}
