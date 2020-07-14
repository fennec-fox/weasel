package io.mustelidae.weasel.checkout.domain.cart

import io.mustelidae.weasel.checkout.utils.sumBy
import java.time.LocalDateTime

class Item(
    val type: Type,
    val id: String,
    val name: String,
    var quantity: Int = 1,
    val amount: Long? = null,
    var description: String? = null,
    var options: List<Option>? = null,
    var imageUrl: String? = null,
    val term: Term? = null
) {

    enum class Type {
        GOOD, TICKET
    }

    data class Term(
        val start: LocalDateTime,
        val end: LocalDateTime
    )

    data class Option(
        val id: String,
        val name: String,
        var quantity: Int = 1,
        val description: String? = null,
        val amount: Long? = null,
        var imageUrl: String? = null,
        val term: Term? = null
    ) {
        fun totalSumOfQuantity(): Long {
            return (amount ?: 0) * quantity
        }
    }

    fun totalSumOfQuantity(): Long {
        val totalAmountOfItem = (amount ?: 0) * quantity
        val totalAmountOfOptions = options?.sumBy { it.totalSumOfQuantity() } ?: 0

        return totalAmountOfItem + totalAmountOfOptions
    }

    fun isEquals(item: Item): Boolean {
        val sameId = (this.id == item.id)

        val sourceOptions = this.options ?: emptyList()
        val targetOptions = this.options ?: emptyList()

        val sameOptionId = sourceOptions.sortedBy { it.id } == targetOptions.sortedBy { it.id }

        return (sameId && sameOptionId)
    }

    companion object
}
