package io.mustelidae.weasel.checkout.domain.preparation

import io.mustelidae.weasel.checkout.config.CheckoutPrepareException
import io.mustelidae.weasel.checkout.domain.preparation.repository.PreparationRepository
import org.bson.types.ObjectId
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class PreparationFinder(
    private val preparationRepository: PreparationRepository
) {

    fun findOrThrow(id: ObjectId): Preparation {
        return preparationRepository.findByIdOrNull(id) ?: throw CheckoutPrepareException("예비거래 정보가 존재하지 않습니다.", id)
    }
}
