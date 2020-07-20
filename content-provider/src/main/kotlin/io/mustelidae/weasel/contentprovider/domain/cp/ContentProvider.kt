package io.mustelidae.weasel.contentprovider.domain.cp

import io.mustelidae.weasel.common.code.IndustryCode
import io.mustelidae.weasel.common.code.SharingType
import java.net.URL
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.Id
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener

@Entity
@EntityListeners(value = [AuditingEntityListener::class])
class ContentProvider(
    val industryCode: IndustryCode,
    val name: String,
    val sharingType: SharingType,
    val confirmationType: ConfirmationType,
    val confirmURL: URL,
    val description: String? = null
) {

    @Id
    @GeneratedValue
    var id: Long? = null
        private set

    var corpNumber: String? = null

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

    fun expire() {
        this.status = false
    }

    enum class ConfirmationType {
        THREE_WAY,
        ONE_WAY
    }
}
