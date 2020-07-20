package io.mustelidae.weasel.common.code

enum class IndustryCode(val types: List<ProductType>) {
    Product(listOf(
        ProductType.GENERAL
    )),
    BOOK(listOf(
        ProductType.GENERAL,
        ProductType.EBOOK
    )),
    MUSIC(listOf(
        ProductType.CD,
        ProductType.LP,
        ProductType.MP3
    )),
    PLAY(listOf(
        ProductType.CULTURE_TICKET
    )),
}
