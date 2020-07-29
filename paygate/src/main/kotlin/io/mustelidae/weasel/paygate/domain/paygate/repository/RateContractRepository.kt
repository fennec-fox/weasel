package io.mustelidae.weasel.paygate.domain.paygate.repository

import io.mustelidae.weasel.paygate.domain.paygate.RateContract
import org.springframework.data.jpa.repository.JpaRepository

interface RateContractRepository : JpaRepository<RateContract, Long>
