package io.mustelidae.weasel.utils

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder

object Jackson {
    private val mapper = Jackson2ObjectMapperBuilder.json()
        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .modules(JavaTimeModule())
        .serializers(CustomDateSerializer())
        .build<ObjectMapper>()
        .registerKotlinModule()
        .enable(MapperFeature.DEFAULT_VIEW_INCLUSION)

    fun getMapper(): ObjectMapper = mapper
}

private class CustomDateSerializer : StdSerializer<Date>(Date::class.java) {
    override fun serialize(value: Date?, gen: JsonGenerator?, provider: SerializerProvider?) {
        if (value != null && gen != null) {
            gen.writeString(
                LocalDateTime.ofInstant(
                    value.toInstant(),
                    ZoneId.systemDefault()
                ).format(DateTimeFormatter.ISO_DATE_TIME)
            )
        }
    }
}

internal fun <T> T.toJson(): String = Jackson.getMapper().writeValueAsString(this)
internal inline fun <reified T> String.fromJson(): T = Jackson.getMapper().readValue(this)
