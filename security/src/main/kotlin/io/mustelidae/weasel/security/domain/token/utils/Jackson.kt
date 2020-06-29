package io.mustelidae.weasel.security.domain.token.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

internal object Jackson {
    private val mapper = ObjectMapper()
        .registerModule(JavaTimeModule())
        .registerKotlinModule()

    fun getMapper(): ObjectMapper = mapper
}
