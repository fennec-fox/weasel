package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.paygate.common.PayMethod

internal class PayGateTest

internal fun PayGate.Companion.aFixture(company: PayGate.Company): PayGate {
    return when (company) {
        PayGate.Company.INICIS -> PayGate(PayGate.Company.INICIS, "inicis", PayGate.Type.ONLINE, PayMethod.CREDIT, true, "1", "2")
        PayGate.Company.NAVER_PAY -> PayGate(PayGate.Company.NAVER_PAY, "naverpay", PayGate.Type.EASY, PayMethod.CREDIT, true, "1", "2")
        PayGate.Company.KAKAO_PAY -> PayGate(PayGate.Company.KAKAO_PAY, "kakaoPay", PayGate.Type.PHYSICAL, PayMethod.CREDIT, true, "1", "2")
    }
}
