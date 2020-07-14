package io.mustelidae.weasel.checkout.domain.cart

import io.mustelidae.weasel.checkout.config.CheckoutCartException
import java.time.LocalDateTime
import javax.persistence.Id
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

/**
 * 바구니
 * mental model: 물건 담은 바구니
 */
@Document
class Basket(
    val type: Type,
    val cpId: Long,
    val userId: String
) {

    @Id
    var id: ObjectId = ObjectId()
        private set

    var items: MutableList<Item> = mutableListOf()
        private set

    var status: Status = Status.WAIT

    var created: LocalDateTime = LocalDateTime.now()
        private set

    enum class Type {
        BOXING,
        NEAR_COUNTER,
        NORMAL
    }

    enum class Status {
        WAIT, // 대기
        PAYMENT_PROGRESS, // 결제 중
        PAYMENT_COMPLETED, // 결제 완료
    }

    internal fun addBy(item: Item) {
        this.items.forEach {
            if (it.isEquals(item))
                throw CheckoutCartException("동일한 상품이 이미 존재 합니다.", this.id)
        }
        this.items.add(item)
    }

    internal fun removeBy(item: Item) {
        val target = this.items.find { it.id == item.id }
        this.items.remove(target)
    }

    companion object
}
