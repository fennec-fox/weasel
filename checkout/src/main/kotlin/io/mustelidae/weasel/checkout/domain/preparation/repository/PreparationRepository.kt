package io.mustelidae.weasel.checkout.domain.preparation.repository

import io.mustelidae.weasel.checkout.domain.preparation.Preparation
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface PreparationRepository : MongoRepository<Preparation, ObjectId>
