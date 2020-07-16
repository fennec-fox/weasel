package io.mustelidae.weasel.checkout.config

import io.mustelidae.weasel.checkout.domain.cart.Basket
import java.lang.RuntimeException
import org.bson.types.ObjectId

open class CheckoutException(message: String, val causeBy: Map<String, Any>? = null) : RuntimeException(message)

class CheckoutCartException(message: String, basketId: ObjectId) : CheckoutException(message, mapOf("basketId" to basketId.toString()))

class CheckoutPrepareException(message: String, prepareId:ObjectId): CheckoutException(message, mapOf("prepareId" to prepareId.toString()))
