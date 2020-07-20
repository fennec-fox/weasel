package io.mustelidae.weasel.contentprovider.domain.cp.repository

import io.mustelidae.weasel.contentprovider.domain.cp.ContentProvider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ContentProviderRepository : JpaRepository<ContentProvider, Long>
