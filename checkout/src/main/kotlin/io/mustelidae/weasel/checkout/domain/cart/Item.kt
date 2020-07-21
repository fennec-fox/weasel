package io.mustelidae.weasel.checkout.domain.cart

import io.mustelidae.weasel.common.code.ProductType
import java.time.LocalDateTime

class Item(
    val type: ProductType,
    val id: String,
    val name: String,
    var quantity: Int = 1,
    val amount: Double = 0.0,
    var description: String? = null,
    var options: List<Option>? = null,
    var imageUrl: String? = null,
    val term: Term? = null
) {

    data class Term(
        val start: LocalDateTime,
        val end: LocalDateTime
    )

    data class Option(
        val id: String,
        val name: String,
        var quantity: Int = 1,
        val amount: Double = 0.0,
        val description: String? = null,
        var imageUrl: String? = null,
        val term: Term? = null
    ) {
        fun totalSumOfQuantity(): Double {
            return amount * quantity
        }
    }

    fun totalSumOfQuantity(): Double {
        val totalAmountOfItem = amount * quantity
        val totalAmountOfOptions = options?.sumByDouble { it.totalSumOfQuantity() } ?: 0.0

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
