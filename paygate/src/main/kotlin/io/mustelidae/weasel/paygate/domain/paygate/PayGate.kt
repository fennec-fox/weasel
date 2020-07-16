package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.common.code.PayMethod
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.EnumType.STRING
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.Id
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(value = [AuditingEntityListener::class])
class PayGate(
    @Enumerated(STRING)
    @Column(length = 15)
    val company: Company,
    @Column(length = 50)
    var name: String,
    @Enumerated(STRING)
    @Column(length = 15)
    val type: Type,
    @Enumerated(STRING)
    @Column(length = 15)
    val payMethod: PayMethod,
    val hasVat: Boolean,
    val storeId: String,
    val storeKey: String? = null
) {

    @Id
    @GeneratedValue
    var id: Long? = null
        private set

    var ratio: Double = 100.0

    var status: Boolean = true
        private set

    var isOn: Boolean = true
        private set

    @Column(length = 50)
    var cause: String? = null
        private set

    @CreatedDate
    var created: LocalDateTime? = null
        private set

    @LastModifiedDate
    var modified: LocalDateTime? = null
        private set

    fun expire(cause: String) {
        this.status = false
        this.cause = cause
    }

    fun on() {
        this.isOn = true
    }

    fun off() {
        this.isOn = false
    }

    enum class Type {
        ONLINE, // 일반 결제
        MANUAL, // 수기 결제
        EASY, // 간편 결제
        PHYSICAL // 실물 배송 결제
    }

    enum class Company {
        INICIS,
        NAVER_PAY,
        KAKAO_PAY
    }

    companion object
}
