package br.com.imversaoavancada.controllers

import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.*
import io.restassured.module.kotlin.extensions.Given
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*

/**
 * @author Eduardo Folly
 */
@QuarkusTest
@TestHTTPEndpoint(BankController::class)
class BankControllerTest {
    companion object {
        @BeforeAll
        @JvmStatic
        fun initAll() {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }
    }

    @Test
    fun firstCountTest() {
        When {
            get("/count")
        } Then {
            statusCode(200)
            body(equalTo("2"))
        }
    }

    @Test
    fun firstCountWithTermTest() {
        Given {
            queryParam("term", "cheio")
        } When {
            get("/count")
        } Then {
            statusCode(200)
            body(equalTo("1"))
        }
    }

    @Test
    fun firstListTest() {
        When {
            get()
        } Then {
            statusCode(200)
            body("size()", equalTo(2))
        }
    }

    @Test
    fun firstListWithTermTest() {
        Given {
            queryParam("term", "cheio")
        } When {
            get()
        } Then {
            statusCode(200)
            body("size()", equalTo(1))
        }
    }

    // getById com id válido

    // getById com id inválido

    // create

    // Verificar se a operação anterior está correta

    // update

    // Verificar se a operação anterior está correta

    // delete

    // Verificar se a operação anterior está correta
}
