package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.entities.Bank
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
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


    @Test
    @DisplayName("Teste para obter um banco por ID válido")
    fun getBankByIdValidTest(){
        val bankId = 1L
        val bankCode = "000"
        val bankName = "Banco Vazio"

        When {
            get("/{id}", bankId)
        } Then {
            statusCode(200)
            body("id", equalTo(bankId.toInt()))
            body("code", equalTo(bankCode))
            body("name", equalTo(bankName))
        }
    }

    @Test
    @DisplayName("Teste para obter um banco por ID Inválido")
    fun getBankByIdInvalidTest(){
        val invalidId = 999999L

        When {
            get("/{id}", invalidId)
        } Then {
            statusCode(404)
        }
    }

    // create

    // Verificar se a operação anterior está correta

    // update

    // Verificar se a operação anterior está correta

    // delete

    // Verificar se a operação anterior está correta
}
