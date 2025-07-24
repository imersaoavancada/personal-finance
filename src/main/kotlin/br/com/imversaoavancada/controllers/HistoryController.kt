package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.entities.History
import br.com.imversaoavancada.services.HistoryService
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestQuery
import java.net.URI

/**
 * @author Eduardo Folly
 */
@Path("/histories")
class HistoryController(
    val service: HistoryService,
) {
    private fun path(history: History): URI =
        URI.create("/histories/${history.id}")

    @GET
    @Path("/count")
    @Produces(MediaType.TEXT_PLAIN)
    fun count(
        @RestQuery term: String?,
    ): Long = service.count(term)

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun list(
        @RestQuery @DefaultValue("0") page: Int,
        @RestQuery @DefaultValue("20") size: Int,
        @RestQuery term: String?,
    ): Response =
        service.listAll(page, size, term).run { Response.ok(this).build() }

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
        @Valid body: History?,
    ): Response =
        body?.let {
            service.create(it).run {
                Response
                    .created(path(this))
                    .entity(this)
                    .build()
            }
        } ?: Response.status(400).build()

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun update(
        @PathParam("id") id: Long,
        @Valid body: History?,
    ): Response =
        body?.let {
            service.update(id, it).run {
                Response.ok(this).build()
            }
        } ?: Response.status(400).build()

    @DELETE
    @Path("/{id}")
    fun delete(
        @PathParam("id") id: Long,
    ): Response = service.delete(id).run { Response.noContent().build() }
}
