package io.mustelidae.weasel.checkout.utils

import kotlin.random.Random

private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

internal fun Random.nextString(until: Long): String {
    return (1..until)
        .map { _ -> Random.nextInt(0, charPool.size) }
        .map(charPool::get)
        .joinToString("")
}
