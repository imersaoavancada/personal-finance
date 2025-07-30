package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.Error
import br.com.imversaoavancada.checkError
import br.com.imversaoavancada.entities.Account
import br.com.imversaoavancada.entities.History
import br.com.imversaoavancada.parse
import br.com.imversaoavancada.projections.HistoryListProjection
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
@TestHTTPEndpoint(HistoryController::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class HistoryControllerTest {
    companion object {
        var count = 4
        var invalidId = -1
        val body = mutableMapOf<String, Any?>()
        lateinit var history: History

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
                Error.Create(History::class).idNotFound(invalidId),
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
                Error.Create("paymentDate").notNull(),
                Error.Create("amount").notNull(),
            )
        }
    }

    @Test
    @Order(6)
    fun insertNullValuesTest() {
        body.apply {
            clear()
            put("name", null)
            put("paymentDate", null)
            put("amount", null)
            put("account", null)
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
                Error.Create("paymentDate").notNull(),
                Error.Create("amount").notNull(),
            )
        }
    }

    @Test
    @Order(7)
    fun insertEmptyValuesTest() {
        body.apply {
            clear()
            put("name", "")
            put("paymentDate", "")
            put("amount", "")
            put("account", null)
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
                Error.Create("paymentDate").notNull(),
                Error.Create("amount").notNull(),
            )
        }
    }

    @Test
    @Order(8)
    fun insertBlankValuesTest() {
        body.apply {
            clear()
            put("name", " ")
            put("paymentDate", " ")
            put("amount", " ")
            put("account", null)
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
                Error.Create("paymentDate").notNull(),
                Error.Create("amount").notNull(),
            )
        }
    }

    @Test
    @Order(9)
    fun insertWrongValuesTest() {
        body.apply {
            clear()
            put("name", "A".repeat(256))
            put("paymentDate", null)
            put("amount", null)
            put("account", null)
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
                Error.Create("paymentDate").notNull(),
                Error.Create("amount").notNull(),
            )
        }
    }

    @Test
    @Order(10)
    fun insertEmptyAccountTest() {
        body.apply {
            clear()
            put("name", "A".repeat(255))
            put("paymentDate", "2025-03-01T00:00:00Z")
            put("amount", 100_00)
            put("account", mapOf<String, Any?>())
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(Account::class).idNotFound(null),
            )
        }
    }

    @Test
    @Order(11)
    fun insertNullAccountIdTest() {
        body.apply {
            clear()
            put("name", "A".repeat(255))
            put("paymentDate", "2025-03-01T00:00:00Z")
            put("amount", 100_00)
            put("account", mapOf<String, Any?>("id" to null))
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(Account::class).idNotFound(null),
            )
        }
    }

    @Test
    @Order(12)
    fun insertInvalidAccountTest() {
        body.apply {
            clear()
            put("name", "A".repeat(255))
            put("paymentDate", "2025-03-01T00:00:00Z")
            put("amount", 100_00)
            put("account", mapOf<String, Any?>("id" to invalidId))
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
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
    @Order(13)
    fun insertSuccessTest() {
        body.apply {
            clear()
            put("name", "A".repeat(255))
            put("paymentDate", "2025-03-01T00:00:00Z")
            put("amount", 100_00)
            put("account", mapOf("id" to 1))
        }

        history = Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            post()
        } Then {
            statusCode(201)
            contentType(ContentType.JSON)
            body(
                "id",
                notNullValue(),
                "createdAt",
                notNullValue(),
                "updatedAt",
                notNullValue(),
            )
            body.remove("account") // FIXME
            body.forEach { (key, value) ->
                body(key, equalTo(value))
            }
        } Extract {
            body().parse(History::class)
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
            get("/{id}", history.id)
        } Then {
            statusCode(200)
            body("$", equalTo(history.toMap()))
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
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(History::class).idNotFound(invalidId),
            )
        }
    }

    @Test
    @Order(17)
    fun updateEmptyBodyTest() {
        Given {
            contentType(ContentType.JSON)
        } When {
            put("/{id}", history.id)
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
            put("/{id}", history.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("paymentDate").notNull(),
                Error.Update("amount").notNull(),
            )
        }
    }

    @Test
    @Order(19)
    fun updateNullValuesTest() {
        body.apply {
            clear()
            put("name", null)
            put("paymentDate", null)
            put("amount", null)
            put("account", null)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", history.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("paymentDate").notNull(),
                Error.Update("amount").notNull(),
            )
        }
    }

    @Test
    @Order(20)
    fun updateWrongValuesTest() {
        body.apply {
            clear()
            put("name", "A".repeat(256))
            put("paymentDate", null)
            put("amount", null)
            put("account", null)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", history.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").sizeBetween(1, 255),
                Error.Update("paymentDate").notNull(),
                Error.Update("amount").notNull(),
            )
        }
    }

    @Test
    @Order(21)
    fun updateEmptyAccountTest() {
        body.apply {
            clear()
            put("name", "B".repeat(255))
            put("paymentDate", "2025-04-02T12:00:00Z")
            put("amount", 200_99)
            put("account", mapOf<String, Any?>())
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", history.id)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Update(Account::class).idNotFound(null),
            )
        }
    }

    @Test
    @Order(22)
    fun updateNullAccountIdTest() {
        body.apply {
            clear()
            put("name", "B".repeat(255))
            put("paymentDate", "2025-04-02T12:00:00Z")
            put("amount", 200_99)
            put("account", mapOf<String, Any?>("id" to null))
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", history.id)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Update(Account::class).idNotFound(null),
            )
        }
    }

    @Test
    @Order(23)
    fun updateInvalidAccountTest() {
        body.apply {
            clear()
            put("name", "B".repeat(255))
            put("paymentDate", "2025-04-02T12:00:00Z")
            put("amount", 200_99)
            put("account", mapOf<String, Any?>("id" to invalidId))
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", history.id)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Update(Account::class).idNotFound(invalidId),
            )
        }
    }

    @Test
    @Order(24)
    fun updateSuccessTest() {
        body.apply {
            clear()
            put("name", "B".repeat(255))
            put("paymentDate", "2025-04-02T12:00:00Z")
            put("amount", 200_99)
            put("account", null)
        }

        history =
            Given {
                contentType(ContentType.JSON)
                body(body)
            } When {
                put("/{id}", history.id)
            } Then {
                statusCode(200)
                contentType(ContentType.JSON)
                body(
                    "id",
                    equalTo(history.id?.toInt()),
                    "createdAt",
                    equalTo(history.createdAt.toString()),
                    "updatedAt",
                    notNullValue(),
                )
                body.remove("account") // FIXME
                body.forEach { (key, value) ->
                    body(key, equalTo(value))
                }
            } Extract {
                body().parse(History::class)
            }
    }

    @Test
    @Order(25)
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
    @Order(26)
    fun checkUpdateTest() {
        When {
            get("/{id}", history.id)
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("$", equalTo(history.toMap()))
        }
    }

    /*
     * Search Term
     */
    @Test
    @Order(27)
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
    @Order(28)
    fun searchTermSuccessTest() {
        Given {
            queryParams("term", history.name)
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
                equalTo(HistoryListProjection.parse(history)),
            )
        }
    }

    /*
     * Delete
     */
    @ParameterizedTest
    @ValueSource(strings = ["-1", "0", "99999"])
    @Order(29)
    fun deleteInvalidTest(invalidId: String) {
        When {
            delete("/{id}", invalidId)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(History::class).idNotFound(invalidId),
            )
        }
    }

    @Test
    @Order(30)
    fun deleteValidTest() {
        When {
            delete("/{id}", history.id)
        } Then {
            statusCode(204)
        }
        count--
    }

    @Test
    @Order(31)
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
    @Order(32)
    fun deleteAlreadyDeletedTest() {
        When {
            delete("/{id}", history.id)
        } Then {
            statusCode(404)
            checkError(
                404,
                Error.Create(History::class).idNotFound(history.id),
            )
        }
    }

    @Test
    @Order(33)
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
