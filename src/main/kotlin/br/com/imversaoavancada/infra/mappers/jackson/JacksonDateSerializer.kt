package br.com.imversaoavancada.infra.mappers.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import java.util.Date

/**
 * @author Eduardo Folly
 */
class JacksonDateSerializer : JsonSerializer<Date>() {
    override fun serialize(
        value: Date?,
        gen: JsonGenerator,
        serializers: SerializerProvider?,
    ) = if (value == null) {
        gen.writeNull()
    } else {
        gen.writeNumber(value.time)
    }
}
