package br.com.imversaoavancada

import io.restassured.response.ResponseBodyExtractionOptions
import kotlin.reflect.KClass

/**
 * @author Eduardo Folly
 */
fun <T : Any> ResponseBodyExtractionOptions.parse(clazz: KClass<T>): T =
    this.`as`(clazz.java)
