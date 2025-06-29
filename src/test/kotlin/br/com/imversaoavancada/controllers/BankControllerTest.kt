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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

/**
 * @author Eduardo Folly
 */
@QuarkusTest
@TestHTTPEndpoint(BankController::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BankControllerTest {
    // Create Maps
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

    // Update Maps
    val updateNameNotBlankError =
        mapOf(
            "field" to "update.body.name",
            "message" to "not_blank",
        )

    val updateCodeNotBlankError =
        mapOf(
            "field" to "update.body.code",
            "message" to "not_blank",
        )

    val updateCodeOnlyNumbersError =
        mapOf(
            "field" to "update.body.code",
            "message" to "only_numbers",
        )

    val updateCodeSizeEqualError =
        mapOf(
            "field" to "update.body.code",
            "message" to "size_equal:3",
        )

    val updateNameSizeBetweenError =
        mapOf(
            "field" to "update.body.name",
            "message" to "size_between:1:150",
        )

    companion object {
        var count = 2
        var invalidId = 999999
        var newCode = "112"
        var newName = "Banco Atualizado"
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

    4) Atualizar o banco criado
    4.1) Checar se atualizou
    4.2) Atualizar com o mesmo objeto
    4.3) Atualizar com o objeto vazio
    4.4) Atualizar com o objeto nulo
    4.5) Atualizar com o código inválido
    4.6) Atualizar com o nome inválido

    5) Deletar o banco
    5.1) Checar se deletou
    5.2) Deletar novamente o banco que já foi deletado
    5.3) Deletar com id inválido

    6) Contar quantos registros tem.
    7) Listar todos os registros.

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
    fun insertEmptyBodyTest() {
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
    fun insertEmptyObjectTest() {
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
    fun insertNullValuesTest() {
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
    fun insertEmptyValuesTest() {
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
    fun insertBlankValuesTest() {
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
    fun insertWrongValuesTest() {
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
    fun insertSuccessTest() {
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
    fun getByIdValidTest() {
        When {
            get("/{id}", bank.id)
        } Then {
            statusCode(200)
            body("$", equalTo(bank.toMap()))
        }
    }

    @Test
    @Order(12)
    fun getByIdInvalidTest() {
        When {
            get("/{id}", invalidId)
        } Then {
            statusCode(404)
        }
    }

    @Test
    @Order(13)
    fun updateValidTest() {
        val newBank =
            Given {
                contentType(ContentType.JSON)
                body(
                    mapOf<String, Any?>(
                        "code" to newCode,
                        "name" to newName,
                    ),
                )
            } When {
                put("/{id}", bank.id)
            } Then {
                statusCode(200)
                contentType(ContentType.JSON)
                body(
                    "id",
                    equalTo(bank.id?.toInt()),
                    "code",
                    equalTo(newCode),
                    "name",
                    equalTo(newName),
                )
            } Extract {
                body().parse(Bank::class)
            }

        bank.code = newBank.code
        bank.name = newBank.name
    }

    @Test
    @Order(14)
    fun checkUpdateTest() {
        When {
            get("/{id}", bank.id)
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("$", equalTo(bank.toMap()))
        }
    }

    @Test
    @Order(15)
    fun updateInvalidIdTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "code" to "123",
                    "name" to "Banco Teste",
                ),
            )
        } When {
            put("/{id}", invalidId)
        } Then {
            statusCode(404)
        }
    }

    @Test
    @Order(16)
    fun updateEmptyBodyTest() {
        Given {
            contentType(ContentType.JSON)
        } When {
            put("/{id}", bank.id)
        } Then {
            statusCode(400)
        }
    }

    @Test
    @Order(17)
    fun updateEmptyObjectTest() {
        Given {
            contentType(ContentType.JSON)
            body(mapOf<String, Any?>())
        } When {
            put("/{id}", bank.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(2),
                "violations",
                hasItems(updateNameNotBlankError, updateCodeNotBlankError),
            )
        }
    }

    @Test
    @Order(18)
    fun updateNullValuesTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "code" to null,
                    "name" to null,
                ),
            )
        } When {
            put("/{id}", bank.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(2),
                "violations",
                hasItems(updateNameNotBlankError, updateCodeNotBlankError),
            )
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["ABC", "12A", "@#$", "1234", "12", "", " "])
    @Order(19)
    fun updateInvalidCodeTest(invalidCode: String) {
        Given {
            contentType(ContentType.JSON)
            body(mapOf("code" to invalidCode, "name" to "Banco Atualizado"))
        } When {
            put("/{id}", bank.id)
        } Then {
            statusCode(400)
            body(
                "violations.field",
                hasItem(containsString("update.body.code")),
            )
        }
    }

    @Test
    @Order(20)
    fun updateInvalidNameTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "code" to "123",
                    "name" to "A".repeat(151),
                ),
            )
        } When {
            put("/{id}", bank.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations",
                hasItems(updateNameSizeBetweenError),
            )
        }
    }

    @Test
    @Order(21)
    fun thirdCountTest() {
        When {
            get("/count")
        } Then {
            statusCode(200)
            contentType(ContentType.TEXT)
            body(equalTo("$count"))
        }
    }

    @Test
    @Order(22)
    fun deleteValidTest() {
        When {
            delete("/{id}", bank.id)
        } Then {
            statusCode(204)
        }
        count--
    }

    @Test
    @Order(23)
    fun fourthCountTest() {
        When {
            get("/count")
        } Then {
            statusCode(200)
            contentType(ContentType.TEXT)
            body(equalTo("$count"))
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["-1", "0", "99999", "123456789ABCaç@!@#"])
    @Order(24)
    fun deleteInvalidTest(invalidId: String) {
        When {
            delete("/{id}", invalidId)
        } Then {
            statusCode(404)
        }
    }

    @Test
    @Order(25)
    fun deleteAlreadyDeletedBankTest() {
        When {
            delete("/{id}", bank.id)
        } Then {
            statusCode(404)
        }
    }

    @Test
    @Order(26)
    fun finalCountTest() {
        When {
            get("/count")
        } Then {
            statusCode(200)
            contentType(ContentType.TEXT)
            body(equalTo("2"))
        }
    }

    @Test
    @Order(27)
    fun finalListTest() {
        When {
            get()
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("size()", equalTo(2))
        }
    }
}
