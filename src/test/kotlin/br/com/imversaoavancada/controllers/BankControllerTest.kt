package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.Error
import br.com.imversaoavancada.checkError
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
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.junit.jupiter.params.provider.ValueSource

/**
 * @author Eduardo Folly
 */
@QuarkusTest
@TestHTTPEndpoint(BankController::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class BankControllerTest {
    companion object {
        var count = 2
        var invalidId = -1L
        val body = mutableMapOf<String, Any?>()
        lateinit var bank: Bank

        @BeforeAll
        @JvmStatic
        fun initAll() {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        }

        @JvmStatic
        fun updateInvalidCodes(): List<Arguments> {
            val notBlank = Error.Update("code").notBlank()
            val sizeBetween = Error.Update("code").sizeBetween(3, 3)
            val onlyNumbers = Error.Update("code").onlyNumbers()
            val nameSizeBetween = Error.Update("name").sizeBetween(1, 150)

            return listOf(
                Arguments.of(null, arrayOf(notBlank, nameSizeBetween)),
                Arguments.of(
                    "",
                    arrayOf(
                        notBlank,
                        sizeBetween,
                        onlyNumbers,
                        nameSizeBetween,
                    ),
                ),
                Arguments.of(
                    " ",
                    arrayOf(
                        notBlank,
                        sizeBetween,
                        onlyNumbers,
                        nameSizeBetween,
                    ),
                ),
                Arguments.of("ABC", arrayOf(onlyNumbers, nameSizeBetween)),
                Arguments.of("12A", arrayOf(onlyNumbers, nameSizeBetween)),
                Arguments.of("@#$", arrayOf(onlyNumbers, nameSizeBetween)),
                Arguments.of(
                    "1234",
                    arrayOf(sizeBetween, onlyNumbers, nameSizeBetween),
                ),
                Arguments.of(
                    "12",
                    arrayOf(sizeBetween, onlyNumbers, nameSizeBetween),
                ),
            )
        }
    }

    @Test
    @Order(1)
    fun getByIdInvalidTest() {
        When {
            get("/{id}", invalidId)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(Bank::class).idNotFound(invalidId),
            )
        }
    }

    @Test
    @Order(2)
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
    @Order(3)
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
    @Order(4)
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
    @Order(5)
    fun insertEmptyObjectTest() {
        Given {
            contentType(ContentType.JSON)
            body(mapOf<String, Any?>())
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Create("name").notBlank(),
                Error.Create("code").notBlank(),
            )
        }
    }

    @Test
    @Order(6)
    fun insertNullValuesTest() {
        body.apply {
            clear()
            put("code", null)
            put("name", null)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Create("name").notBlank(),
                Error.Create("code").notBlank(),
            )
        }
    }

    @Test
    @Order(7)
    fun insertEmptyValuesTest() {
        body.apply {
            clear()
            put("code", "")
            put("name", "")
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Create("name").notBlank(),
                Error.Create("name").sizeBetween(1, 150),
                Error.Create("code").notBlank(),
                Error.Create("code").onlyNumbers(),
                Error.Create("code").sizeBetween(3, 3),
            )
        }
    }

    @Test
    @Order(8)
    fun insertBlankValuesTest() {
        body.apply {
            clear()
            put("code", " ")
            put("name", " ")
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Create("name").notBlank(),
                Error.Create("code").notBlank(),
                Error.Create("code").onlyNumbers(),
                Error.Create("code").sizeBetween(3, 3),
            )
        }
    }

    @Test
    @Order(9)
    fun insertInvalidValuesTest() {
        body.apply {
            clear()
            put("code", "A".repeat(4))
            put("name", "A".repeat(151))
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Create("name").sizeBetween(1, 150),
                Error.Create("code").onlyNumbers(),
                Error.Create("code").sizeBetween(3, 3),
            )
        }
    }

    @Test
    @Order(10)
    fun insertSuccessTest() {
        body.apply {
            clear()
            put("code", "8".repeat(3))
            put("name", "A".repeat(150))
        }

        bank = Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(201)
            contentType(ContentType.JSON)

            body("id", notNullValue())
            body.forEach { (key, value) ->
                body(key, equalTo(value))
            }
        } Extract {
            body().parse(Bank::class)
        }

        count++
    }

    // https://github.com/imersaoavancada/personal-finance/issues/8
    @Test
    @Order(11)
    fun insertDuplicatedTest() {
        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(400, Error.Constraint("banks", "code.unq").uniqueField())
        }
    }

    @Test
    @Order(12)
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
    @Order(13)
    fun getByIdValidTest() {
        When {
            get("/{id}", bank.id)
        } Then {
            statusCode(200)
            body("$", equalTo(bank.toMap()))
        }
    }

    /*
     * Update
     */
    @Test
    @Order(14)
    fun updateInvalidIdTest() {
        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", invalidId)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(Bank::class).idNotFound(invalidId),
            )
        }
    }

    @Test
    @Order(15)
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
    @Order(16)
    fun updateEmptyObjectTest() {
        Given {
            contentType(ContentType.JSON)
            body(mapOf<String, Any?>())
        } When {
            put("/{id}", bank.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("code").notBlank(),
            )
        }
    }

    @Test
    @Order(17)
    fun updateNullValuesTest() {
        body.apply {
            clear()
            put("code", null)
            put("name", null)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", bank.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("code").notBlank(),
            )
        }
    }

    @ParameterizedTest
    @MethodSource("updateInvalidCodes")
    @Order(18)
    fun updateInvalidCodeTest(
        invalidCode: String?,
        errors: Array<Map<String, Any?>>,
    ) {
        Given {
            contentType(ContentType.JSON)
            body(mapOf("code" to invalidCode, "name" to "A".repeat(151)))
        } When {
            put("/{id}", bank.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(400, *errors)
        }
    }

    @Test
    @Order(19)
    fun updateSuccessTest() {
        body.apply {
            clear()
            put("code", "7".repeat(3))
            put("name", "B".repeat(150))
        }

        bank =
            Given {
                contentType(ContentType.JSON)
                body(body)
            } When {
                put("/{id}", bank.id)
            } Then {
                statusCode(200)
                contentType(ContentType.JSON)
                body("id", equalTo(bank.id?.toInt()))
                body.forEach { (key, value) ->
                    body(key, equalTo(value))
                }
            } Extract {
                body().parse(Bank::class)
            }
    }

    @Test
    @Order(20)
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
    @Order(21)
    fun checkUpdateTest() {
        When {
            get("/{id}", bank.id)
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("$", equalTo(bank.toMap()))
        }
    }

    /*
     * Search Term
     */
    @Test
    @Order(22)
    fun searchTermBlankTest() {
        Given {
            queryParams("term", " ")
            contentType(ContentType.JSON)
        } When {
            get()
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body(
                "size()",
                equalTo(count),
            )
        }
    }

    @Test
    @Order(23)
    fun searchTermSuccessTest() {
        Given {
            queryParams("term", bank.name)
            contentType(ContentType.JSON)
        } When {
            get()
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body(
                "size()",
                equalTo(1),
                "[0]",
                equalTo(bank.toMap()),
            )
        }
    }

    /*
     * Delete
     */
    @ParameterizedTest
    @ValueSource(strings = ["-1", "0", "999"])
    @Order(24)
    fun deleteInvalidTest(invalidId: String) {
        When {
            delete("/{id}", invalidId)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(Bank::class).idNotFound(invalidId),
            )
        }
    }

    @Test
    @Order(25)
    fun deleteValidTest() {
        When {
            delete("/{id}", bank.id)
        } Then {
            statusCode(204)
        }
        count--
    }

    @Test
    @Order(26)
    fun fourthCountTest() {
        When {
            get("/count")
        } Then {
            statusCode(200)
            contentType(ContentType.TEXT)
            body(equalTo("$count"))
        }
    }

    @Test
    @Order(27)
    fun deleteAlreadyDeletedTest() {
        When {
            delete("/{id}", bank.id)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(Bank::class).idNotFound(bank.id),
            )
        }
    }

    @Test
    @Order(28)
    fun finalListTest() {
        When {
            get()
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("size()", equalTo(count))
        }
    }

    @Test
    @Order(29)
    fun finalInsertSuccessTest() {
        body.apply {
            clear()
            put("code", "7".repeat(3))
            put("name", "B".repeat(150))
        }

        bank = Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(201)
            contentType(ContentType.JSON)

            body("id", notNullValue())
            body.forEach { (key, value) ->
                body(key, equalTo(value))
            }
        } Extract {
            body().parse(Bank::class)
        }

        count++
    }
}
