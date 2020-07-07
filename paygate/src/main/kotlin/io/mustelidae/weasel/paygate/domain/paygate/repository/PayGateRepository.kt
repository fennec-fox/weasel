package io.mustelidae.weasel.paygate.domain.paygate.repository

import io.mustelidae.weasel.paygate.common.PayMethod
import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import org.springframework.data.jpa.repository.JpaRepository

interface PayGateRepository : JpaRepository<PayGate, Long> {
    fun findByCompanyAndPayMethodAndTypeAndStatusTrue(company: PayGate.Company, payMethod: PayMethod, type: PayGate.Type): PayGate?
    fun findByTypeAndPayMethodAndStatusTrue(type: PayGate.Type, payMethod: PayMethod): List<PayGate>?
}
