package br.com.imversaoavancada.infra.mappers.exceptions

import jakarta.persistence.PersistenceException
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.ext.ExceptionMapper
import jakarta.ws.rs.ext.Provider
import org.hibernate.exception.ConstraintViolationException

/**
 * @author Eduardo Folly
 */
@Provider
class PersistenceExceptionMapper : ExceptionMapper<PersistenceException> {
    override fun toResponse(exception: PersistenceException): Response {
        val violation =
            when (exception) {
                is ConstraintViolationException -> {
                    mapOf(
                        "field" to exception.constraintName?.replace("_", "."),
                        "message" to "constraint_violation_exception",
                    )
                }

                else -> {
                    mapOf(
                        "field" to exception.message,
                        "message" to "persistence_exception",
                    )
                }
            }

        return Response
            .status(400)
            .entity(
                mapOf(
                    "title" to "Constraint Violation",
                    "status" to 400,
                    "violations" to listOf(violation),
                ),
            ).build()
    }
}
