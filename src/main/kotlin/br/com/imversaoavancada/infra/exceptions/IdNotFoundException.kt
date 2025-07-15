package br.com.imversaoavancada.infra.exceptions

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response
import kotlin.reflect.KClass

/**
 * @author Eduardo Folly
 */
class IdNotFoundException(
    id: Long?,
    clazz: KClass<*>,
) : WebApplicationException(
        Response
            .status(404)
            .entity(
                mapOf(
                    "title" to "Constraint Violation",
                    "status" to 404,
                    "violations" to
                        listOf(
                            mapOf(
                                "field" to clazz.simpleName,
                                "message" to "id_not_found:$id",
                            ),
                        ),
                ),
            ).build(),
    )
