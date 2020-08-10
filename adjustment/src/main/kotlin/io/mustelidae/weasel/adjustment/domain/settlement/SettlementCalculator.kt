package io.mustelidae.weasel.adjustment.domain.settlement

interface SettlementCalculator {

    fun credit(transactionAmount: Double, rateFee: Double, vat: Boolean): Double
}
