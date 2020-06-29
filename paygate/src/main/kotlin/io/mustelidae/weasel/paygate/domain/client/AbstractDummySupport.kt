package io.mustelidae.weasel.paygate.domain.client

import kotlin.random.Random

internal abstract class AbstractDummySupport {

    fun numberOfAccount(): String {
        return "${Random.nextInt(10000, 99999)}-${Random.nextInt(1000, 9999)}-${Random.nextInt(100000, 999999)}"
    }

    fun numberOfCard(): String {
        val random = java.util.Random()
        val sb = StringBuilder()
        (0..14).forEach { _ -> sb.append(random.nextInt(10)) }
        sb.append(getCheckDigit(sb.toString()))
        return sb.toString()
    }

    private val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun keyOfVan(): String {
        return (1..16)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }

    private fun getCheckDigit(number: String): Int {
        var sum = 0
        (number.indices).forEach { i ->
            // Get the digit at the current position.
            var digit = Integer.parseInt(number.substring(i, i + 1))

            if (i % 2 == 0) {
                digit *= 2
                if (digit > 9) {
                    digit = digit / 10 + digit % 10
                }
            }
            sum += digit
        }

        val mod = sum % 10
        return if (mod == 0) 0 else 10 - mod
    }
}
