package io.mustelidae.weasel.contentprovider.domain.cp

import io.mustelidae.weasel.contentprovider.domain.cp.repository.ContentProviderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ContentProviderManager(
    private val contentProviderFinder: ContentProviderFinder,
    private val contentProviderRepository: ContentProviderRepository
) {

    fun add(contentProvider: ContentProvider): Long {
        return contentProviderRepository.save(contentProvider).id!!
    }

    fun expire(cpId: Long) {
        val cp = contentProviderFinder.findOrThrow(cpId)
        cp.expire()
        contentProviderRepository.save(cp)
    }
}
