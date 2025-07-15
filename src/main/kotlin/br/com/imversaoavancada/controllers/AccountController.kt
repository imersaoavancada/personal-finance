package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.entities.Account
import br.com.imversaoavancada.services.AccountService
import jakarta.validation.Valid
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import org.jboss.resteasy.reactive.RestQuery
import java.net.URI

/**
 * @author Eduardo Folly
 */
@Path("/accounts")
class AccountController(
    val service: AccountService,
) {
    private fun path(account: Account): URI =
        URI.create("/accounts/${account.id}")

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
    ): List<Account> = service.listAll(page, size, term)

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
        @Valid body: Account?,
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
        @Valid body: Account?,
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
    ): Response = service.delete(id).let { Response.noContent().build() }
}
