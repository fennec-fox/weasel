package io.mustelidae.weasel.paygate.domain.paygate

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mustelidae.weasel.paygate.config.PayGateException
import io.mustelidae.weasel.paygate.domain.paygate.repository.RateContractRepository
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

internal class RateContractManagerTest {

    private val payGateFinder: PayGateFinder = mockk()
    private val rateContractFinder: RateContractFinder = mockk()
    private val rateContractRepository: RateContractRepository = mockk()
    private val rateContractManager = RateContractManager(payGateFinder, rateContractFinder, rateContractRepository)

    @Test
    @DisplayName("정상적인 추가 계약")
    fun add1() {
        // Given
        val payGate = PayGate.aFixture()
        val rateContract = RateContract.aFixture(LocalDate.now().plusDays(2), LocalDate.now().plusDays(3))
        every { payGateFinder.findOrThrow(payGate.id!!) } returns payGate
        val slot = slot<RateContract>()
        every { rateContractRepository.save(capture(slot)) } returns RateContract.aFixture() // mockk 회피

        // When
        rateContractManager.add(payGate.id!!, rateContract)
    }

    @Test
    @DisplayName("기존 계약과 날짜가 틀어지는 경우")
    fun add2() {
        // Given
        val payGate = PayGate.aFixture()
        val rateContract = RateContract.aFixture(LocalDate.now().plusDays(1), LocalDate.now().plusDays(3))
        every { payGateFinder.findOrThrow(payGate.id!!) } returns payGate

        // When
        assertThrows(PayGateException::class.java) {
            rateContractManager.add(payGate.id!!, rateContract)
        }
    }

    @Test
    @DisplayName("기존 계약의 종료일과 시작일 사이에 날짜가 빈 경우")
    fun add3() {
        // Given
        val payGate = PayGate.aFixture()
        val rateContract = RateContract.aFixture(LocalDate.now().plusDays(3), LocalDate.now().plusDays(4))
        every { payGateFinder.findOrThrow(payGate.id!!) } returns payGate

        // When
        assertThrows(PayGateException::class.java) {
            rateContractManager.add(payGate.id!!, rateContract)
        }
    }
}
