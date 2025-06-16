package br.com.imversaoavancada

import io.quarkus.test.junit.QuarkusTest
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.hamcrest.CoreMatchers.`is`
import org.junit.jupiter.api.Test

@QuarkusTest
class GreetingResourceTest {
    @Test
    fun testHelloEndpoint() {
        When {
            get("/hello")
        } Then {
            statusCode(200)
            body(`is`("Hello from Quarkus!"))
        }
    }
}
