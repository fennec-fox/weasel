package io.mustelidae.weasel.checkout.domain.cart

import java.time.LocalDateTime
import kotlin.random.Random

internal class ItemTest

internal fun Item.Companion.aFixture(type: Item.Type): Item {
    val id = Random.nextInt().toString()
    return when (type) {
        Item.Type.GOOD -> {
            Item(
                type,
                id,
                "fixture item",
                1,
                1000,
                "is fixure item",
                listOf(
                    Item.Option(
                        Random.nextInt().toString(),
                        "option item",
                        1,
                        "is option",
                        500
                    )
                )
            )
        }
        Item.Type.TICKET -> {
            Item(
                type,
                id,
                "wwd 202x ticket",
                1,
                400,
                "is ticket",
                null,
                null,
                Item.Term(
                    LocalDateTime.now().plusHours(1),
                    LocalDateTime.now().plusHours(3)
                )
            )
        }
    }
}
