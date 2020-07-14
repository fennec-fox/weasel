package io.mustelidae.weasel.checkout.domain.cart

import io.kotlintest.matchers.asClue
import io.kotlintest.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mustelidae.weasel.checkout.config.CheckoutCartException
import io.mustelidae.weasel.checkout.domain.cart.repository.BasketRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class CartInteractionTest {

    private val basketFinder: BasketFinder = mockk()
    private val basketRepository: BasketRepository = mockk()
    private val cartInteraction = CartInteraction(basketFinder, basketRepository)

    @Test
    fun save() {
        // Given
        val cpId = 1L
        val userId = "fennec-fox"
        val basketType = Basket.Type.NORMAL
        val items = listOf(
            Item.aFixture(Item.Type.GOOD),
            Item.aFixture(Item.Type.TICKET)
        )

        // When
        val slot = slot<Basket>()
        every { basketRepository.save(capture(slot)) } returns Basket.aFixture(basketType) // returns는 mockk을 우회하기 위한 dummy 값임

        val basketId = cartInteraction.save(cpId, userId, basketType, items)

        // Then
        slot.isCaptured shouldBe true
        slot.captured.asClue {
            it.id shouldBe basketId
            it.type shouldBe basketType
            it.userId shouldBe userId
            it.items.size shouldBe items.size
        }
    }

    @Test
    fun putNormalCart() {
        // Given

        val basket = Basket.aFixture(Basket.Type.NORMAL)
        val sizeOfItemForBasket = basket.items.size

        // When
        every { basketFinder.findOne(basket.id) } returns basket

        val slot = slot<Basket>()
        every { basketRepository.save(capture(slot)) } returns Basket.aFixture() // returns는 mockk을 우회하기 위한 dummy 값임

        cartInteraction.put(basket.id, Item.aFixture(Item.Type.TICKET))

        // Then
        slot.isCaptured shouldBe true
        slot.captured.asClue {
            it.items.size shouldBe sizeOfItemForBasket + 1
        }
    }

    @Test
    fun putBoxingCart() {
        // Given
        val basket = Basket.aFixture(Basket.Type.BOXING)

        // When
        every { basketFinder.findOne(basket.id) } returns basket

        val slot = slot<Basket>()
        every { basketRepository.save(capture(slot)) } returns Basket.aFixture() // returns는 mockk을 우회하기 위한 dummy 값임

        // Then
        Assertions.assertThrows(CheckoutCartException::class.java) {
            cartInteraction.put(basket.id, Item.aFixture(Item.Type.TICKET))
        }
    }

    @Test
    fun removeNormal() {
        // Given
        val basket = Basket.aFixture(Basket.Type.NORMAL)
        val item = basket.items.first()
        val sizeOfItemForBasket = basket.items.size

        // When
        every { basketFinder.findOne(basket.id) } returns basket

        val slot = slot<Basket>()
        every { basketRepository.save(capture(slot)) } returns Basket.aFixture() // returns는 mockk을 우회하기 위한 dummy 값임

        cartInteraction.remove(basket.id, item)

        // Then
        slot.isCaptured shouldBe true
        slot.captured.asClue {
            it.items.contains(item) shouldBe false
            it.items.size shouldBe sizeOfItemForBasket - 1
        }
    }
}
