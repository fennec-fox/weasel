package io.mustelidae.weasel.adjustment.domain.settlement

import java.math.BigDecimal
import kotlin.math.floor
import org.slf4j.LoggerFactory

class InicisSettlementCalculator : SettlementCalculator {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun credit(transactionAmount: Double, rateFee: Double, vat: Boolean): Double {
        val vatRate = if (vat) 0.1 else 0.0
        val calculatedFee = floor(transactionAmount * (rateFee * 0.01))
        val tax = BigDecimal(calculatedFee * vatRate).setScale(0, BigDecimal.ROUND_HALF_UP).toDouble()
        val settlementAmt = transactionAmount - calculatedFee - tax

        log.debug("inicis settlement amount is $settlementAmt. cause by ( transactionAmount: $transactionAmount, rateFee: $rateFee, vat: $vatRate | calcFee:$calculatedFee, tax: $tax)")

        return settlementAmt
    }
}
