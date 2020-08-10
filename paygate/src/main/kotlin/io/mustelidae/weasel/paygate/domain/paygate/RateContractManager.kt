package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.paygate.config.PayGateException
import io.mustelidae.weasel.paygate.domain.paygate.repository.RateContractRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * PG 수수료 계약 관리
 */
@Service
@Transactional
class RateContractManager(
    private val payGateFinder: PayGateFinder,
    private val rateContractFinder: RateContractFinder,
    private val rateContractRepository: RateContractRepository
) {

    fun add(payGateId: Long, rateContract: RateContract): Long {
        val payGate = payGateFinder.findOrThrow(payGateId)

        val rateContracts = payGate.rateContracts
            .filter { it.type == rateContract.type }
            .toMutableList()

        rateContracts.sortBy { it.end }

        val lastContract = rateContracts.last()

        if (lastContract.end.plusDays(1) != rateContract.start)
            throw PayGateException("계약 일자가 연결되어 있지 않습니다. 마지막 계약일자 ${lastContract.end}, 신규 계약 일자 ${rateContract.start}")

        rateContract.setBy(payGate)
        return rateContractRepository.save(rateContract).id!!
    }

    fun expire(id: Long) {
        val rateContract = rateContractFinder.findOrThrow(id)
        rateContract.expire()
        rateContractRepository.save(rateContract)
    }
}
