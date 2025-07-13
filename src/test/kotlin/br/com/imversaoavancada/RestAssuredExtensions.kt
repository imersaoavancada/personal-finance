package br.com.imversaoavancada

import io.restassured.response.ResponseBodyExtractionOptions
import kotlin.reflect.KClass

/**
 * @author Eduardo Folly
 */

sealed class Error(
    val prefix: String,
    val field: String,
) {
    class Create(
        field: String,
    ) : Error("create", field)

    class Update(
        field: String,
    ) : Error("update", field)

    fun notBlank(): Map<String, Any?> =
        mapOf(
            "field" to "$prefix.body.$field",
            "message" to "not_blank",
        )

    fun notNull(): Map<String, Any?> =
        mapOf(
            "field" to "$prefix.body.$field",
            "message" to "not_null",
        )

    fun size(vararg numbers: Number): Map<String, Any?> =
        mapOf(
            "field" to "$prefix.body.$field",
            "message" to listOf("size_between", *numbers).joinToString(":"),
        )

    fun positiveOrZero(): Map<String, Any?> =
        mapOf(
            "field" to "$prefix.body.$field",
            "message" to "positive_or_zero",
        )

    fun idNotNull(id: Long? = null): Map<String, Any?> =
        mapOf(
            "field" to field,
            "message" to "id_not_found:$id",
        )
}

fun <T : Any> ResponseBodyExtractionOptions.parse(clazz: KClass<T>): T =
    this.`as`(clazz.java)
