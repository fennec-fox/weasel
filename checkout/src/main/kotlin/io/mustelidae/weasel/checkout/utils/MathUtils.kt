package io.mustelidae.weasel.checkout.utils

internal inline fun <T> Iterable<T>.sumBy(selector: (T) -> Long): Long {
    var sum: Long = 0
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
