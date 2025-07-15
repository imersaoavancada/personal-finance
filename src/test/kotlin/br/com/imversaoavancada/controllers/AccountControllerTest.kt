package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.Error
import br.com.imversaoavancada.checkError
import br.com.imversaoavancada.entities.Account
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
@TestHTTPEndpoint(AccountController::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class AccountControllerTest {
    companion object {
        var count = 2
        var invalidId = -1L
        val body = mutableMapOf<String, Any?>()
        lateinit var account: Account

        @BeforeAll
        @JvmStatic
        fun initAll() {
            RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
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
                Error.Create(Account::class).idNotFound(invalidId),
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
                Error.Create("type").notNull(),
            )
        }
    }

    @Test
    @Order(6)
    fun insertNullValuesTest() {
        body.apply {
            clear()
            put("name", null)
            put("type", null)
            put("bank", null)
            put("branch", null)
            put("number", null)
            put("creditLimit", null)
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
                Error.Create("type").notNull(),
            )
        }
    }

    @Test
    @Order(7)
    fun insertEmptyValuesTest() {
        body.apply {
            clear()
            put("name", "")
            put("type", null)
            put("bank", null)
            put("branch", "")
            put("number", "")
            put("creditLimit", null)
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
                Error.Create("name").sizeBetween(1, 255),
                Error.Create("type").notNull(),
                Error.Create("branch").sizeBetween(1, 255),
                Error.Create("number").sizeBetween(1, 255),
            )
        }
    }

    @Test
    @Order(8)
    fun insertBlankValuesTest() {
        body.apply {
            clear()
            put("name", " ")
            put("type", null)
            put("bank", null)
            put("branch", " ")
            put("number", " ")
            put("creditLimit", null)
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
                Error.Create("type").notNull(),
            )
        }
    }

    @Test
    @Order(9)
    fun insertInvalidValuesTest() {
        body.apply {
            clear()
            put("name", "A".repeat(256))
            put("type", null)
            put("bank", null)
            put("branch", "A".repeat(256))
            put("number", "A".repeat(256))
            put("creditLimit", -1)
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
                Error.Create("name").sizeBetween(1, 255),
                Error.Create("type").notNull(),
                Error.Create("branch").sizeBetween(1, 255),
                Error.Create("number").sizeBetween(1, 255),
                Error.Create("creditLimit").positiveOrZero(),
            )
        }
    }

    @Test
    @Order(10)
    fun insertEmptyBankTest() {
        body.apply {
            clear()
            put("name", "A".repeat(255))
            put("type", "CHECKING")
            put("bank", mapOf<String, Any?>())
            put("branch", "A".repeat(255))
            put("number", "A".repeat(255))
            put("creditLimit", 0)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(404, Error.Create(Bank::class).idNotFound())
        }
    }

    @Test
    @Order(11)
    fun insertNullBankTest() {
        body.apply {
            clear()
            put("name", "A".repeat(255))
            put("type", "CHECKING")
            put("bank", mapOf<String, Any?>("id" to null))
            put("branch", "A".repeat(255))
            put("number", "A".repeat(255))
            put("creditLimit", 0)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(404, Error.Create(Bank::class).idNotFound())
        }
    }

    @Test
    @Order(12)
    fun insertInvalidBankTest() {
        body.apply {
            clear()
            put("name", "A".repeat(255))
            put("type", "CHECKING")
            put("bank", mapOf<String, Any?>("id" to invalidId))
            put("branch", "A".repeat(255))
            put("number", "A".repeat(255))
            put("creditLimit", 0)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(404, Error.Create(Bank::class).idNotFound(invalidId))
        }
    }

    @Test
    @Order(13)
    fun insertSuccessTest() {
        body.apply {
            clear()
            put("name", "A".repeat(255))
            put("type", "CHECKING")
            put("bank", mapOf("id" to 1))
            put("branch", "A".repeat(255))
            put("number", "A".repeat(255))
            put("creditLimit", 0)
        }

        account = Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(201)
            contentType(ContentType.JSON)

            body("id", notNullValue())
            body.remove("bank") // FIXME
            body.forEach { (key, value) ->
                body(key, equalTo(value))
            }
        } Extract {
            body().parse(Account::class)
        }

        count++
    }

    @Test
    @Order(14)
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
    @Order(15)
    fun getByIdValidTest() {
        When {
            get("/{id}", account.id)
        } Then {
            statusCode(200)
            body("$", equalTo(account.toMap()))
        }
    }

    /*
     * Update
     */
    @Test
    @Order(16)
    fun updateInvalidIdTest() {
        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", invalidId)
        } Then {
            statusCode(404)
        }
    }

    @Test
    @Order(17)
    fun updateEmptyBodyTest() {
        Given {
            contentType(ContentType.JSON)
        } When {
            put("/{id}", account.id)
        } Then {
            statusCode(400)
        }
    }

    @Test
    @Order(18)
    fun updateEmptyObjectTest() {
        Given {
            contentType(ContentType.JSON)
            body(mapOf<String, Any?>())
        } When {
            put("/{id}", account.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("type").notNull(),
            )
        }
    }

    @Test
    @Order(19)
    fun updateNullValuesTest() {
        body.apply {
            clear()
            put("name", null)
            put("type", null)
            put("bank", null)
            put("branch", null)
            put("number", null)
            put("creditLimit", null)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", account.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("type").notNull(),
            )
        }
    }

    @Test
    @Order(20)
    fun updateInvalidNameTest() {
        body.apply {
            clear()
            put("name", "A".repeat(256))
            put("type", null)
            put("bank", null)
            put("branch", "A".repeat(256))
            put("number", "A".repeat(256))
            put("creditLimit", -1)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", account.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").sizeBetween(1, 255),
                Error.Update("type").notNull(),
                Error.Update("branch").sizeBetween(1, 255),
                Error.Update("number").sizeBetween(1, 255),
                Error.Update("creditLimit").positiveOrZero(),
            )
        }
    }

    @Test
    @Order(21)
    fun updateSuccessTest() {
        body.apply {
            clear()
            put("name", "B".repeat(255))
            put("type", "SAVINGS")
            put("bank", mapOf("id" to 2))
            put("branch", "B".repeat(255))
            put("number", "B".repeat(255))
            put("creditLimit", 0)
        }

        account =
            Given {
                contentType(ContentType.JSON)
                body(body)
            } When {
                put("/{id}", account.id)
            } Then {
                statusCode(200)
                contentType(ContentType.JSON)

                body("id", equalTo(account.id?.toInt()))
                body.remove("bank") // FIXME
                body.forEach { (key, value) ->
                    body(key, equalTo(value))
                }
            } Extract {
                body().parse(Account::class)
            }
    }

    @Test
    @Order(22)
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
    @Order(23)
    fun checkUpdateTest() {
        When {
            get("/{id}", account.id)
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("$", equalTo(account.toMap()))
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
                Error.Create(Account::class).idNotFound(invalidId),
            )
        }
    }

    @Test
    @Order(25)
    fun deleteValidTest() {
        When {
            delete("/{id}", account.id)
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
            delete("/{id}", account.id)
        } Then {
            statusCode(404)
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
}
