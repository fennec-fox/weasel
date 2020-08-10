package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.paygate.utils.invokeId
import java.time.LocalDate
import kotlin.random.Random

internal class RateContractTest

internal fun RateContract.Companion.aFixture(
    start: LocalDate = LocalDate.now(),
    end: LocalDate = LocalDate.now().plusDays(1),
    feeRate: Double = 0.03
): RateContract {
    return RateContract(RateContract.Type.METHOD, start, end, feeRate).apply { invokeId(this, Random.nextLong()) }
}
