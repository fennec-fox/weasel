package io.mustelidae.weasel.checkout.domain.preparation

import io.mustelidae.weasel.checkout.config.CheckoutCartException
import io.mustelidae.weasel.checkout.domain.cart.Basket
import io.mustelidae.weasel.common.code.PayMethod
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.Id
import javax.persistence.OneToMany
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.mapping.Document

@Document
class Preparation(
    val purchaseUserUd: String,
    val extraFields: Map<String, String?> = emptyMap(),
    val rewardDate: LocalDate? = null,
    val ignoreMethods: List<PayMethod>? = null
) {

    @Id
    var id: ObjectId = ObjectId()
        private set

    var created: LocalDateTime = LocalDateTime.now()
        private set

    @OneToMany(mappedBy = "preparation")
    val baskets: MutableList<Basket> = arrayListOf()

    fun addBy(basket: Basket) {
        baskets.add(basket)
        if (basket.preparation != this)
            basket.setBy(this)
    }

    var isCompleted: Boolean = false
        private set
    var modifiedDate: LocalDateTime? = null

    fun complete() {
        isCompleted = true
        modifiedDate = LocalDateTime.now()
        baskets.forEach { it.status = Basket.Status.PAYMENT_COMPLETED }
    }

    fun progress() {
        modifiedDate = LocalDateTime.now()
        baskets.forEach {
            if (it.status == Basket.Status.PAYMENT_COMPLETED)
                throw CheckoutCartException("이미 결제가 완료 되었습니다. 다시 결제 시작해 주세요.", this.id)

            it.status == Basket.Status.PAYMENT_PROGRESS
        }
    }

    companion object
}
