package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.Error
import br.com.imversaoavancada.checkError
import br.com.imversaoavancada.entities.Provision
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
@TestHTTPEndpoint(ProvisionController::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class ProvisionControllerTest {
    companion object {
        var count = 2
        var invalidId = -1L
        val body = mutableMapOf<String, Any?>()
        lateinit var provision: Provision

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
                Error.Create(Provision::class).idNotFound(invalidId),
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
                Error.Create("initialDate").notNull(),
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
            put("initialDate", null)
            put("finalDate", null)
            put("amount", null)
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
                Error.Create("initialDate").notNull(),
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
            put("initialDate", "")
            put("finalDate", "")
            put("amount", "")
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
                Error.Create("initialDate").notNull(),
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
            put("initialDate", " ")
            put("finalDate", " ")
            put("amount", " ")
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
                Error.Create("initialDate").notNull(),
                Error.Create("amount").notNull(),
            )
        }
    }

    @Test
    @Order(9)
    fun insertInvalidValuesTest() {
        body.apply {
            clear()
            put("name", "A".repeat(256))
            put("initialDate", "")
            put("finalDate", "")
            put("amount", null)
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
                Error.Create("initialDate").notNull(),
                Error.Create("amount").notNull(),
            )
        }
    }

    @Test
    @Order(10)
    fun insertSuccessTest() {
        body.apply {
            clear()
            put("name", "A".repeat(255))
            put("initialDate", "2025-02-01T12:00:00Z")
            put("finalDate", null)
            put("amount", 99900)
        }

        provision = Given {
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
            body.forEach { (key, value) ->
                body(key, equalTo(value))
            }
        } Extract {
            body().parse(Provision::class)
        }

        count++
    }

    @Test
    @Order(11)
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
    @Order(12)
    fun getByIdValidTest() {
        When {
            get("/{id}", provision.id)
        } Then {
            statusCode(200)
            body(
                "$",
                notNullValue(),
                *provision
                    .toMap()
                    .flatMap { listOf(it.key, equalTo(it.value)) }
                    .toTypedArray(),
            )
        }
    }

    /*
     * Update
     */
    @Test
    @Order(13)
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
                Error.Create(Provision::class).idNotFound(invalidId),
            )
        }
    }

    @Test
    @Order(14)
    fun updateEmptyBodyTest() {
        Given {
            contentType(ContentType.JSON)
        } When {
            put("/{id}", provision.id)
        } Then {
            statusCode(400)
        }
    }

    @Test
    @Order(15)
    fun updateEmptyObjectTest() {
        Given {
            contentType(ContentType.JSON)
            body(mapOf<String, Any?>())
        } When {
            put("/{id}", provision.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("initialDate").notNull(),
                Error.Update("amount").notNull(),
            )
        }
    }

    @Test
    @Order(16)
    fun updateNullValuesTest() {
        body.apply {
            clear()
            put("name", null)
            put("initialDate", null)
            put("finalDate", null)
            put("amount", null)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", provision.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("initialDate").notNull(),
                Error.Update("amount").notNull(),
            )
        }
    }

    @Test
    @Order(17)
    fun updateEmptyTest() {
        body.apply {
            clear()
            put("name", "")
            put("initialDate", "")
            put("finalDate", "")
            put("amount", "")
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", provision.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("name").sizeBetween(1, 255),
                Error.Update("initialDate").notNull(),
                Error.Update("amount").notNull(),
            )
        }
    }

    @Test
    @Order(18)
    fun updateBlankTest() {
        body.apply {
            clear()
            put("name", " ")
            put("initialDate", " ")
            put("finalDate", " ")
            put("amount", " ")
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", provision.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").notBlank(),
                Error.Update("initialDate").notNull(),
                Error.Update("amount").notNull(),
            )
        }
    }

    @Test
    @Order(19)
    fun updateInvalidTest() {
        body.apply {
            clear()
            put("name", "B".repeat(256))
            put("initialDate", "")
            put("finalDate", "")
            put("amount", null)
        }

        Given {
            contentType(ContentType.JSON)
            body(body)
        } When {
            put("/{id}", provision.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            checkError(
                400,
                Error.Update("name").sizeBetween(1, 255),
                Error.Update("initialDate").notNull(),
                Error.Update("amount").notNull(),
            )
        }
    }

    @Test
    @Order(20)
    fun updateSuccessTest() {
        body.apply {
            clear()
            put("name", "B".repeat(255))
            put("initialDate", "2025-04-01T12:00:00Z")
            put("finalDate", "2025-12-01T12:00:00Z")
            put("amount", 88899)
        }

        provision =
            Given {
                contentType(ContentType.JSON)
                body(body)
            } When {
                put("/{id}", provision.id)
            } Then {
                statusCode(200)
                contentType(ContentType.JSON)
                body(
                    "id",
                    equalTo(provision.id?.toInt()),
                    "createdAt",
                    equalTo(provision.createdAt.toString()),
                    "updatedAt",
                    notNullValue(),
                )
                body.forEach { (key, value) ->
                    body(key, equalTo(value))
                }
            } Extract {
                body().parse(Provision::class)
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
    fun checkUpdateTest() {
        When {
            get("/{id}", provision.id)
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("$", equalTo(provision.toMap()))
        }
    }

    /*
     * Search Term
     */
    @Test
    @Order(23)
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
    @Order(24)
    fun searchTermSuccessTest() {
        Given {
            queryParams("term", provision.name)
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
                equalTo(provision.toMap()),
            )
        }
    }

    /*
     * Delete
     */
    @ParameterizedTest
    @ValueSource(strings = ["-1", "0", "999"])
    @Order(25)
    fun deleteInvalidTest(invalidId: String) {
        When {
            delete("/{id}", invalidId)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(Provision::class).idNotFound(invalidId),
            )
        }
    }

    @Test
    @Order(26)
    fun deleteValidTest() {
        When {
            delete("/{id}", provision.id)
        } Then {
            statusCode(204)
        }
        count--
    }

    @Test
    @Order(27)
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
    @Order(28)
    fun deleteAlreadyDeletedTest() {
        When {
            delete("/{id}", provision.id)
        } Then {
            statusCode(404)
            contentType(ContentType.JSON)
            checkError(
                404,
                Error.Create(Provision::class).idNotFound(provision.id),
            )
        }
    }

    @Test
    @Order(29)
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
