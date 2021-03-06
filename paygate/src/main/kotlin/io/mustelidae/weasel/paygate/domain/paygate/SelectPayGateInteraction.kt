package io.mustelidae.weasel.paygate.domain.paygate

import io.mustelidae.weasel.common.code.PayMethod
import kotlin.math.ln
import kotlin.random.Random
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Paygate 임의 선택 하기
 * mental model: 무인 계산기 기계 선택
 */
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
