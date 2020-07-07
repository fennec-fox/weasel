package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.paygate.common.PayMethod
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.ln
import kotlin.random.Random

@Service
@Transactional(readOnly = true)
class SelectPayGateInteraction(
    private val payGateFinder: PayGateFinder
) {

    fun pickRatio(type: PayGate.Type, payMethod: PayMethod): PayGate {
        val payGates = payGateFinder.findAll(type, payMethod)
            .filter { it.isOn }

        return getWeightedRandom(payGates.map { it to it.ratio }.toMap())!!
    }

    private fun getWeightedRandom(weights: Map<PayGate, Double>): PayGate? {
        var result: PayGate? = null
        var bestValue = Double.MAX_VALUE
        for (element in weights.keys) {
            val value = -ln(Random.nextDouble()) / weights.getValue(element)
            if (value < bestValue) {
                bestValue = value
                result = element
            }
        }
        return result
    }
}