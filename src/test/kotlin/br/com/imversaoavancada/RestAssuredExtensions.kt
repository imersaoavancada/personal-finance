package br.com.imversaoavancada

import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.response.ValidatableResponse
import org.hamcrest.Matchers.equalTo
import org.hamcrest.Matchers.hasItems
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
    ) : Error("create", field) {
        constructor(clazz: KClass<*>) : this(clazz.simpleName!!)
    }

    class Update(
        field: String,
    ) : Error("update", field) {
        constructor(clazz: KClass<*>) : this(clazz.simpleName!!)
    }

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

    fun idNotFound(id: Any? = null): Map<String, Any?> =
        mapOf(
            "field" to field,
            "message" to "id_not_found:$id",
        )
}

fun <T : Any> ResponseBodyExtractionOptions.parse(clazz: KClass<T>): T =
    this.`as`(clazz.java)

fun ValidatableResponse.checkError(
    status: Int,
    vararg errors: Map<String, Any?>,
): ValidatableResponse =
    body(
        "status",
        equalTo(status),
        "violations.size()",
        equalTo(errors.size),
        "violations",
        hasItems(*errors),
    )
