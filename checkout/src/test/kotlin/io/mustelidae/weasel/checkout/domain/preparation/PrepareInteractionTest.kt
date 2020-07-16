package io.mustelidae.weasel.checkout.domain.preparation

import io.kotlintest.matchers.asClue
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mustelidae.weasel.checkout.domain.cart.Basket
import io.mustelidae.weasel.checkout.domain.cart.BasketFinder
import io.mustelidae.weasel.checkout.domain.cart.Cart
import io.mustelidae.weasel.checkout.domain.cart.aFixture
import io.mustelidae.weasel.checkout.domain.preparation.repository.PreparationRepository
import io.mustelidae.weasel.checkout.utils.nextString
import java.time.LocalDate
import kotlin.random.Random
import org.junit.jupiter.api.Test

internal class PrepareInteractionTest {

    private val basketFinder: BasketFinder = mockk()
    private val preparationFinder: PreparationFinder = mockk()
    private val preparationRepository: PreparationRepository = mockk()
    private val prepareInteraction: PrepareInteraction = PrepareInteraction(basketFinder, preparationFinder, preparationRepository)

    @Test
    fun prepare() {
        // Given
        val extraFields = mapOf<String, String?>(
            "test" to "1"
        )
        val userId = Random.nextString(5)
        val cart = Cart.from(Basket.aFixture())
        val rewardDate = LocalDate.now()

        val slot = slot<Preparation>()
        every { preparationRepository.save(capture(slot)) } returns Preparation.aFixture() // returns는 mockk을 우회하기 위한 dummy 값임

        // When
        val id = prepareInteraction.prepare(userId, cart, extraFields, rewardDate)

        // Then
        val preparation = slot.captured
        id shouldBe preparation.id

        preparation.asClue {
            it.isCompleted shouldBe false
            it.baskets.size shouldBe 1
            it.baskets.first() shouldBe cart.basket
        }
    }

    @Test
    fun processingPayment() {
    }

    @Test
    fun confirmPayment() {
    }
}
