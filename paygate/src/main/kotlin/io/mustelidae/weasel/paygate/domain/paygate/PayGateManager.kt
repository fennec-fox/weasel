package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.paygate.config.PayGateException
import io.mustelidae.weasel.paygate.domain.paygate.repository.PayGateRepository
import javax.transaction.Transactional
import org.springframework.stereotype.Service

@Service
@Transactional
class PayGateManager(
    private val payGateFinder: PayGateFinder,
    private val payGateRepository: PayGateRepository
) {

    fun add(payGate: PayGate): Long {

        val isExist = payGateFinder.hasPayGate(payGate.company, payGate.payMethod, payGate.type)

        if (isExist)
            throw PayGateException("이미 해당 회사의 결제 수단 및 타입이 존재 합니다.")

        if (payGate.rateContracts.isEmpty())
            throw PayGateException("계약 정보가 존재하지 않습니다.")

        return payGateRepository.save(payGate).id!!
    }

    fun modify(id: Long, name: String, ratio: Double) {
        val payGate = payGateFinder.findOrThrow(id)

        payGate.apply {
            this.name = name
            this.ratio = ratio
        }

        payGateRepository.save(payGate)
    }

    fun expire(id: Long, cause: String) {
        val payGate = payGateFinder.findOrThrow(id)
        payGate.expire(cause)
        payGateRepository.save(payGate)
    }
}
