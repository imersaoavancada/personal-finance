package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.entities.Bank
import br.com.imversaoavancada.parse
import io.quarkus.test.common.http.TestHTTPEndpoint
import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.module.kotlin.extensions.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*

/**
 * @author Eduardo Folly
 */
@QuarkusTest
@TestHTTPEndpoint(BankController::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BankControllerTest {
    val nameNotBlankError =
        mapOf(
            "field" to "create.body.name",
            "message" to "not_blank",
        )

    val codeNotBlankError =
        mapOf(
            "field" to "create.body.code",
            "message" to "not_blank",
        )

    val codeOnlyNumbersError =
        mapOf(
            "field" to "create.body.code",
            "message" to "only_numbers",
        )

    val codeSizeEqualError =
        mapOf(
            "field" to "create.body.code",
            "message" to "size_equal:3",
        )

    val nameSizeBetweenError =
        mapOf(
            "field" to "create.body.name",
            "message" to "size_between:1:150",
        )

    companion object {
        var count = 2
        lateinit var bank: Bank

        @BeforeAll
        @JvmStatic
        fun initAll() {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }
    }

    /*

    Ordem para execução dos testes. (Manifesto de Testes)

    - Definições iniciais - Premissas
    1) Quantos registros o banco de dados inicia?
    2) Uma área para definição de objetos?

    Ordem:
    1) Contar quantos registros tem.
    2) Listar todos os registros.



    3) Criar um banco

    3.1) Requisição vazia?
         body:

    3.2) Requisição com o objeto vazio?
         body: { }
         kotlin: mapOf<String, Any?>()

    3.3) Requisição com objeto nulo?
         body: {
                  "code": null,
                  "name": null,
               }

         kotlin: mapOf<String, Any?>("code" to null, "name" to null)

    3.4) ????
         body: {
                  "code": "",
                  "name": "",
               }

         kotlin: mapOf<String, Any?>("code" to "", "name" to "")

    3.5) ????
         body: {
                  "code": " ",
                  "name": " ",
               }

         kotlin: mapOf<String, Any?>("code" to " ", "name" to " ")

    3.6) Requisição com problemas de validação?
    3.6.1) Código inválido. Maior que 3 caracteres.
    3.6.2) Nome inválido. Maior que 150 caracteres.
    3.7) Requisição correta.


     */

    @Test
    @Order(1)
    fun firstCountTest() {
        When {
            get("/count")
        } Then {
            statusCode(200)
            contentType(ContentType.TEXT)
            body(equalTo("$count"))
        }
    }

    @Test
    @Order(2)
    fun firstListTest() {
        When {
            get()
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("size()", equalTo(count))
        }
    }

    @Test
    @Order(3)
    fun insertEmptyRequest() {
        Given {
            contentType(ContentType.JSON)
        } When {
            post()
        } Then {
            statusCode(400)
        }
    }

    @Test
    @Order(4)
    fun insertEmptyObjectRequest() {
        Given {
            contentType(ContentType.JSON)
            body(mapOf<String, Any?>())
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(2),
                "violations",
                hasItems(nameNotBlankError, codeNotBlankError),
            )
        }
    }

    @Test
    @Order(5)
    fun insertNullObjectRequest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "code" to null,
                    "name" to null,
                ),
            )
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(2),
                "violations",
                hasItems(nameNotBlankError, codeNotBlankError),
            )
        }
    }

    @Test
    @Order(6)
    fun insertEmptyValuesRequest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "code" to "",
                    "name" to "",
                ),
            )
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(5),
                "violations",
                hasItems(
                    nameNotBlankError,
                    codeNotBlankError,
                    codeOnlyNumbersError,
                    codeSizeEqualError,
                    nameSizeBetweenError,
                ),
            )
        }
    }

    @Test
    @Order(7)
    fun insertBlankValuesRequest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "code" to " ",
                    "name" to " ",
                ),
            )
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(4),
                "violations",
                hasItems(
                    codeNotBlankError,
                    codeSizeEqualError,
                    codeOnlyNumbersError,
                    nameNotBlankError,
                ),
            )
        }
    }

    @Test
    @Order(8)
    fun insertWrongValuesRequest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "code" to "A".repeat(4),
                    "name" to "A".repeat(151),
                ),
            )
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(3),
                "violations",
                hasItems(
                    codeSizeEqualError,
                    codeOnlyNumbersError,
                    nameSizeBetweenError,
                ),
            )
        }
    }

    @Test
    @Order(9)
    fun insertSuccessRequest() {
        val code = "8".repeat(3)
        val name = "A".repeat(150)

        bank = Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "code" to code,
                    "name" to name,
                ),
            )
        } When {
            post()
        } Then {
            statusCode(201)
            contentType(ContentType.JSON)
            body(
                "id",
                notNullValue(),
                "code",
                equalTo(code),
                "name",
                equalTo(name),
            )
        } Extract {
            body().parse(Bank::class)
        }

        count++
    }

    @Test
    @Order(10)
    fun secondCountTest() {
        When {
            get("/count")
        } Then {
            statusCode(200)
            contentType(ContentType.TEXT)
            body(equalTo("$count"))
        }
    }

    @Test
    @Order(11)
    fun getBankByIdValidTest() {
        When {
            get("/{id}", bank.id)
        } Then {
            statusCode(200)
            body("$", equalTo(bank.toMap()))
        }
    }

    // Verificar se a operação anterior está correta

    // update

    // Verificar se a operação anterior está correta

    // delete

    // Verificar se a operação anterior está correta

//    @Test
//    @DisplayName("Teste para obter um banco por ID Inválido")
//    fun getBankByIdInvalidTest(){
//        val invalidId = 999999L
//
//        When {
//            get("/{id}", invalidId)
//        } Then {
//            statusCode(404)
//        }
//    }
}
