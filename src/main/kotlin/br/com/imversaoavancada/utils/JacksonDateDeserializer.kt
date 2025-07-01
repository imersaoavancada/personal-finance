package br.com.imversaoavancada.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.util.Date

/**
 * @author Eduardo Folly
 */
class JacksonDateDeserializer : JsonDeserializer<Date>() {
    override fun deserialize(
        parser: JsonParser,
        context: DeserializationContext?,
    ): Date = Date(parser.valueAsLong)
}
