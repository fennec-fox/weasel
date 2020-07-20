package io.mustelidae.weasel.common.code

enum class ProductType(
    /* 소득공제 여부 */
    val incomeDeduction: Boolean
) {
    GENERAL(false),
    DIGITAL_CONTENT(false),
    CASHABLE(false),
    BOOK(true),
    EBOOK(true),
    CD(false),
    LP(false),
    MP3(false),
    CULTURE_TICKET(true)
}
