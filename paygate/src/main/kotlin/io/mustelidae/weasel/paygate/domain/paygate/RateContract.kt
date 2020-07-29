package io.mustelidae.weasel.paygate.domain.paygate

import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import javax.persistence.CascadeType.ALL
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.FetchType.LAZY
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.ManyToOne
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(value = [AuditingEntityListener::class])
class RateContract(
    val start: LocalDate,
    val end: LocalDate,
    val feeRate: Double,
    val guaranteedAmount: Long? = null,
    val contractImage: URL? = null
) {

    @Id
    @GeneratedValue
    var id: Long? = null
        private set

    @CreatedDate
    var created: LocalDateTime? = null
        private set

    @LastModifiedDate
    var modified: LocalDateTime? = null
        private set

    @CreatedBy
    @LastModifiedBy
    @Column(length = 100)
    var auditor: String? = null

    var status: Boolean = true
        private set

    @ManyToOne(fetch = LAZY, cascade = [ALL])
    var payGate: PayGate? = null

    fun setBy(payGate: PayGate) {
        this.payGate = payGate
        if (payGate.rateContracts.contains(this).not())
            payGate.addBy(this)
    }

    fun expire() {
        status = false
    }

    companion object
}
