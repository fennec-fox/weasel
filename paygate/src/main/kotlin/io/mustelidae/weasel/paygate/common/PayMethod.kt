package io.mustelidae.weasel.paygate.common

enum class PayMethod(val desc: String) {
    CREDIT("신용카드"),
    BANK_TRANSFER("계좌 이체"),
    NAVER_PAY_POINT("네이버페이 포인트"),
    KAKAO_PAY_MONEY("카카오페이 머니")
}
