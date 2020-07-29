package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.common.code.PayMethod
import io.mustelidae.weasel.paygate.utils.invokeId

internal class PayGateTest

internal fun PayGate.Companion.aFixture(company: PayGate.Company = PayGate.Company.INICIS): PayGate {
    val payGate = when (company) {
        PayGate.Company.INICIS -> PayGate(PayGate.Company.INICIS, "inicis", PayGate.Type.ONLINE, PayMethod.CREDIT, true, "1", "2").apply { invokeId(this, 1) }
        PayGate.Company.NAVER_PAY -> PayGate(PayGate.Company.NAVER_PAY, "naverpay", PayGate.Type.EASY, PayMethod.CREDIT, true, "1", "2").apply { invokeId(this, 2) }
        PayGate.Company.KAKAO_PAY -> PayGate(PayGate.Company.KAKAO_PAY, "kakaoPay", PayGate.Type.PHYSICAL, PayMethod.CREDIT, true, "1", "2").apply { invokeId(this, 3) }
    }
    payGate.addBy(RateContract.aFixture())
    return payGate
}
