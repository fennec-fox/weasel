package io.mustelidae.weasel.paygate.common

enum class BankCode(
    val bank: String,
    val inicis: String,
    val naverPay: String
) {
    KDB("산업은행", "02", "002"),
    IBK("기업은행", "03", "003"),
    KB("국민은행", "04", "004"),
    KEB("외환은행", "05", "005"),
    SUHYUP("수협중앙회", "07", "007"),
    NH("농협중앙회", "11", "011"),
    WOORI("우리은행", "20", "020"),
    SC("SC제일은행", "23", "023"),
    DAEGU("대구은행", "31", "031"),
    BUSAN("부산은행", "32", "032"),
    KJ("광주은행", "34", "034"),
    JB("전북은행", "37", "037"),
    KN("경남은행", "39", "039"),
    CITY("한국씨티은행", "27", "027"),
    POST("우체국", "71", "071"),
    HANA("하나은행", "81", "081"),
    SH("신한은행", "88", "088"),
    KBNK("K뱅크", "89", "-"),
    KABNK("카카오뱅크", "90", "-")
}
