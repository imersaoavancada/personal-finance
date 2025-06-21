package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.entities.Bank
import br.com.imversaoavancada.services.BankService
import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import java.net.URI

/**
 * @author Eduardo Folly
 */
@Path("/banks")
class BankController(
    val service: BankService,
) {
    private fun path(bank: Bank): URI = URI.create("/banks/${bank.id}")

    // TODO: Count

    // TODO: Search
    // TODO: Pagination
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun list(): List<Bank> = service.listAll()

    // TODO: Get by id

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

    // TODO: Update
    @PUT
    @Path("/{id}")
    fun update(@PathParam("id") id : Long, bankToBeUpdated: Bank): Response =
        service.update(id, bankToBeUpdated).run {
            Response.ok(this)
                .entity(this)
                .build()
        }

    // TODO: Delete
}
