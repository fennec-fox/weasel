package io.mustelidae.weasel.checkout.domain.cart.repository

import io.mustelidae.weasel.checkout.domain.cart.Basket
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface BasketRepository : MongoRepository<Basket, ObjectId>
