package io.mustelidae.weasel.paygate.common

enum class PayMethod(val desc: String) {
    CREDIT("신용카드"),
    NAVER_PAY("네이버페이"),
    KAKAO_PAY("카카오페이")
}
