package io.mustelidae.weasel.paygate.domain.client.inicis

internal enum class InicisPayType {
    CARD, // 신용카드
    HPMN, // 해피머니
    CULTURE, // 문화상품권
    MOBILE, // 휴대폰
    VBANK, // 가상계좌
    EWALLET, // 전자지갑
    ETC // 기타
}
