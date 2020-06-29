package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.paygate.common.PayMethod
import io.mustelidae.weasel.paygate.config.PayGateClientException
import io.mustelidae.weasel.paygate.config.PayGateException
import io.mustelidae.weasel.paygate.domain.paygate.repository.PayGateRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import javax.transaction.Transactional


@Service
@Transactional
class PayGateFinder(
    private val payGateRepository: PayGateRepository
){

    fun findOne(id: Long): PayGate {
        val payGate = payGateRepository.findByIdOrNull(id)
        if(payGate == null || payGate.status.not())
            throw PayGateException("존재 하지 않는 PG id 입니다.")
        return payGate
    }

    fun findAll(): List<PayGate> {
        return payGateRepository.findAll()
            .filter { it.status }
    }

    fun findAllWithExpired(): List<PayGate> {
        return payGateRepository.findAll()
    }

    internal fun hasPayGate(company: PayGate.Company, payMethod: PayMethod, type:PayGate.Type): Boolean {
        val payGate = payGateRepository.findByCompanyAndPayMethodAndTypeAndStatusTrue(company, payMethod, type)
        return (payGate != null)
    }
}
