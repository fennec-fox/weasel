package io.mustelidae.weasel.paygate.domain.payment

import io.mustelidae.weasel.paygate.config.PayGateEnvironment
import io.mustelidae.weasel.paygate.config.PayGateException
import io.mustelidae.weasel.paygate.domain.client.ClientHandler
import io.mustelidae.weasel.paygate.domain.client.kakaopay.KakaoPayResources
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.paygate.PayGateFinder
import org.springframework.stereotype.Service

@Service
class PrepareInteraction(
    private val payGateFinder: PayGateFinder,
    payGateEnvironment: PayGateEnvironment
) {

    private val clientHandler = ClientHandler(payGateEnvironment)

    fun kakaoPay(product: PayGateResources.PrepareProduct): PayGateResources.Prepared {
        val client = clientHandler.kakaoPay()
        val payGate = payGateFinder.findOne(product.token.payGateId)

        if (payGate.company != PayGate.Company.KAKAO_PAY)
            throw PayGateException("결제수단이 카카오 페이가 아닙니다.")

        val firstGood = product.goods.first()

        val name = if (product.goods.size > 1) {
            "${firstGood.name} 외 ${product.goods.size - 1}"
        } else {
            firstGood.name
        }

        val reply = client.prepare(KakaoPayResources.Request.Prepare(
            product.token,
            payGate.storeId,
            name,
            firstGood.id,
            product.goods.sumBy { it.quantity },
            product.approvalUrl!!,
            product.cancelUrl,
            product.failUrl
        ))

        val redirectUrl = when (product.device) {
            PayGateResources.PrepareProduct.Device.APP -> reply.nextRedirectAppUrl
            PayGateResources.PrepareProduct.Device.MOBILE_WEB -> reply.nextRedirectMobileUrl
            PayGateResources.PrepareProduct.Device.PC_WEB -> reply.nextRedirectPcUrl
            PayGateResources.PrepareProduct.Device.ANY -> reply.nextRedirectMobileUrl
        }

        return PayGateResources.Prepared(
            reply.tid,
            redirectUrl,
            reply.androidAppScheme,
            reply.iosAppScheme
        )
    }
}
