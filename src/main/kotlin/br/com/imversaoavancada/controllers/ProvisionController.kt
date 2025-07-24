package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.entities.Provision
import br.com.imversaoavancada.services.ProvisionService
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

/**
 * *@author: William Braziellas
 * github: @wbraziellas
*/

@Path("/provisions")
class ProvisionController(
    val service: ProvisionService,
) {
    private fun path(provision: Provision): URI =
        URI.create("/histories/${provision.id}")

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getById(
        @PathParam("id") id: Long,
    ): Response = service.getById(id).run { Response.ok(this).build() }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun create(
        @Valid body: Provision?,
    ): Response =
        body?.let {
            service.create(it).run {
                Response
                    .created(path(this))
                    .entity(this)
                    .build()
            }
        } ?: Response.status(400).build()
}
