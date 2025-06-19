package br.com.imversaoavancada

import jakarta.ws.rs.*
import jakarta.ws.rs.core.MediaType

@Path("/hello")
class GreetingResource {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    fun hello() = "Hello from Quarkus!"
}
