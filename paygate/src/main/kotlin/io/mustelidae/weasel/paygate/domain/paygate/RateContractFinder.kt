package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.paygate.config.PayGateException
import io.mustelidae.weasel.paygate.domain.paygate.repository.RateContractRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class RateContractFinder(
    private val rateContractRepository: RateContractRepository
) {

    fun findOrThrow(id: Long): RateContract {
        val rateContract = rateContractRepository.findByIdOrNull(id)
        if (rateContract == null || rateContract.status.not())
            throw PayGateException("해당 계약 정보가 존재하지 않습니다.")

        return rateContract
    }
}
