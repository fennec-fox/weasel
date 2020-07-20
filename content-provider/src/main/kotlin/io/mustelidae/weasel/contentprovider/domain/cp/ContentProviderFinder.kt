package io.mustelidae.weasel.contentprovider.domain.cp

import io.mustelidae.weasel.contentprovider.config.ContentProviderException
import io.mustelidae.weasel.contentprovider.domain.cp.repository.ContentProviderRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ContentProviderFinder(
    private val contentProviderRepository: ContentProviderRepository
) {

    fun findOrThrow(cpId: Long): ContentProvider {
        val contentProvider = contentProviderRepository.findByIdOrNull(cpId)
        if (contentProvider == null || contentProvider.status.not())
            throw ContentProviderException("존재 하지 않는 CP 입니다.")
        return contentProvider
    }
}
