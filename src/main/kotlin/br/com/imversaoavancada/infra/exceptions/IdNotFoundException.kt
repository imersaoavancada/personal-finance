package br.com.imversaoavancada.infra.exceptions

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response

class IdNotFoundException(
    id: Long?,
    field: String,
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
                                "field" to field,
                                "message" to "id_not_found:$id",
                            ),
                        ),
                ),
            ).build(),
    )
