package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.entities.Bank
import br.com.imversaoavancada.services.BankService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestQuery
import java.net.URI

/**
 * @author Eduardo Folly
 */
@Path("/banks")
class BankController(
    val service: BankService,
) {
    private fun path(bank: Bank): URI = URI.create("/banks/${bank.id}")

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
    ): List<Bank> = service.listAll(page, size, term)

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    fun getById(
        @PathParam("id") id: Long,
    ): Response = service.getById(id).run { Response.ok(this).build() }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun create(body: Bank): Response =
        service.create(body).run {
            Response
                .created(path(this))
                .entity(this)
                .build()
        }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    fun update(
        @PathParam("id") id: Long,
        bank: Bank,
    ): Response = service.update(id, bank).run { Response.ok(this).build() }

    @DELETE
    @Path("/{id}")
    fun delete(
        @PathParam("id") id: Long,
    ): Response = service.delete(id).let { Response.noContent().build() }
}
