package io.mustelidae.weasel.paygate.domain.paygate.repository

import io.mustelidae.weasel.paygate.domain.paygate.PayGate
import io.mustelidae.weasel.paygate.domain.paygate.QPayGate.payGate
import io.mustelidae.weasel.paygate.domain.paygate.QRateContract.rateContract
import java.time.LocalDate
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class PayGateDSLRepository : QuerydslRepositorySupport(PayGate::class.java) {

    fun findPayGateUnderContact(id: Long): PayGate {
        val now = LocalDate.now()
        return from(payGate)
            .innerJoin(payGate.rateContracts, rateContract)
            .fetchJoin()
            .where(
                payGate.id.eq(id)
                    .and(rateContract.status.eq(true))
                    .and(rateContract.start.loe(now))
                    .and(rateContract.end.gt(now))
            )
            .fetchOne()
    }
}
