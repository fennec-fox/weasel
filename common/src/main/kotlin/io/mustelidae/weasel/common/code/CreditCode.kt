package io.mustelidae.weasel.common.code

// FIXME: 카카오페이는 제휴를 해야 코드를 알려줌..
enum class CreditCode(
    val credit: String,
    val inicis: String,
    val naverPay: String,
    val kakaoPay: String
) {
    KEB("외환카드", "01", "CA", ""),
    LOT("롯데카드", "03", "C5", ""),
    HYN("현대카드", "04", "CH", ""),
    KB("국민카드", "06", "C3", ""),
    BC("BC카드", "11", "C1", ""),
    SAMSUNG("삼성카드", "12", "C7", ""),
    SINHAN("신한카드", "14", "C0", ""),
    CITY("시티카드", "15", "C9", ""),
    NH("농협카드", "16", "C4", ""),
    HANA("하나카드", "17", "CF", ""),
    VISA("비자카드", "21", "-", ""),
    MASTER("마스터카드", "22", "-", ""),
    JCB("JCB카드", "23", "-", ""),
    AMEX("아멕스카드", "24", "-", ""),
    DINERS("다이너스카드", "25", "-", ""),
    GJ("광주카드", "-", "C2", ""),
    KDB("산업카드", "-", "C6", "")
}
