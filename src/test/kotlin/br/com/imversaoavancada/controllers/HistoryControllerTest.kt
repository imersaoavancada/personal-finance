package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.entities.History
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
@TestHTTPEndpoint(HistoryController::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class HistoryControllerTest {
    // Create Maps
    val nameNotBlankError =
        mapOf(
            "field" to "create.body.name",
            "message" to "not_blank",
        )

    val paymentDateNotNullError =
        mapOf(
            "field" to "create.body.paymentDate",
            "message" to "not_null",
        )

    val amountNotNullError =
        mapOf(
            "field" to "create.body.amount",
            "message" to "not_null",
        )

    val nameSizeBetweenError =
        mapOf(
            "field" to "create.body.name",
            "message" to "size_between:1:255",
        )

    // Update Maps
    val updateNameNotBlankError =
        mapOf(
            "field" to "update.body.name",
            "message" to "not_blank",
        )

    val updatePaymentDateNotNullError =
        mapOf(
            "field" to "update.body.paymentDate",
            "message" to "not_null",
        )

    val updateAmountNotNullError =
        mapOf(
            "field" to "update.body.amount",
            "message" to "not_null",
        )

    val updateNameSizeBetweenError =
        mapOf(
            "field" to "update.body.name",
            "message" to "size_between:1:255",
        )

    companion object {
        var count = 3
        var invalidId = -1
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
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(3),
                "violations",
                hasItems(
                    nameNotBlankError,
                    paymentDateNotNullError,
                    amountNotNullError,
                ),
            )
        }
    }

    @Test
    @Order(6)
    fun insertNullValuesTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "name" to null,
                    "paymentDate" to null,
                    "amount" to null,
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
                    nameNotBlankError,
                    paymentDateNotNullError,
                    amountNotNullError,
                ),
            )
        }
    }

    @Test
    @Order(7)
    fun insertEmptyValuesTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "name" to "",
                    "paymentDate" to "",
                    "amount" to "",
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
                    nameNotBlankError,
                    nameSizeBetweenError,
                    paymentDateNotNullError,
                    amountNotNullError,
                ),
            )
        }
    }

    @Test
    @Order(8)
    fun insertBlankValuesTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "code" to " ",
                    "paymentDate" to " ",
                    "amount" to " ",
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
                    nameNotBlankError,
                    paymentDateNotNullError,
                    amountNotNullError,
                ),
            )
        }
    }

    @Test
    @Order(9)
    fun insertWrongValuesTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "name" to "A".repeat(256),
                    "paymentDate" to null,
                    "amount" to null,
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
                    nameSizeBetweenError,
                    paymentDateNotNullError,
                    amountNotNullError,
                ),
            )
        }
    }

    @Test
    @Order(10)
    fun insertSuccessTest() {
        val name = "A".repeat(255)
        val paymentDate = "2025-03-01T00:00:00Z"
        val amount = 100_00

        history = Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "name" to name,
                    "paymentDate" to paymentDate,
                    "amount" to amount,
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
                "createdAt",
                notNullValue(),
                "updatedAt",
                notNullValue(),
                "name",
                equalTo(name),
                "paymentDate",
                equalTo(paymentDate),
                "amount",
                equalTo(amount),
            )
        } Extract {
            body().parse(History::class)
        }

        count++
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
    @Order(14)
    fun updateInvalidIdTest() {
        val name = "A".repeat(255)
        val paymentDate = "2025-03-01T00:00:00Z"
        val amount = 100_00

        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "name" to name,
                    "paymentDate" to paymentDate,
                    "amount" to amount,
                ),
            )
        } When {
            put("/{id}", invalidId)
        } Then {
            statusCode(404)
        }
    }

    @Test
    @Order(15)
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
    @Order(16)
    fun updateEmptyObjectTest() {
        Given {
            contentType(ContentType.JSON)
            body(mapOf<String, Any?>())
        } When {
            put("/{id}", history.id)
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
                    updateNameNotBlankError,
                    updatePaymentDateNotNullError,
                    updateAmountNotNullError,
                ),
            )
        }
    }

    @Test
    @Order(17)
    fun updateNullValuesTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "name" to null,
                    "paymentDate" to null,
                    "amount" to null,
                ),
            )
        } When {
            put("/{id}", history.id)
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
                    updateNameNotBlankError,
                    updatePaymentDateNotNullError,
                    updateAmountNotNullError,
                ),
            )
        }
    }

    @Test
    @Order(19)
    fun updateWrongValuesTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "name" to "A".repeat(256),
                    "paymentDate" to null,
                    "amount" to null,
                ),
            )
        } When {
            put("/{id}", history.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "violations.size()",
                equalTo(3),
                "violations",
                hasItems(
                    updateNameSizeBetweenError,
                    updatePaymentDateNotNullError,
                    updateAmountNotNullError,
                ),
            )
        }
    }

    @Test
    @Order(20)
    fun updateSuccessTest() {
        val name = "B".repeat(255)
        val paymentDate = "2025-04-02T12:00:00Z"
        val amount = 200_99

        history =
            Given {
                contentType(ContentType.JSON)
                body(
                    mapOf<String, Any?>(
                        "name" to name,
                        "paymentDate" to paymentDate,
                        "amount" to amount,
                    ),
                )
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
                    "name",
                    equalTo(name),
                    "paymentDate",
                    equalTo(paymentDate),
                    "amount",
                    equalTo(amount),
                )
            } Extract {
                body().parse(History::class)
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
            get("/{id}", history.id)
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("$", equalTo(history.toMap()))
        }
    }

    /*
     * Delete
     */
    @ParameterizedTest
    @ValueSource(strings = ["-1", "0", "99999", "123456789ABCaç@!@#"])
    @Order(23)
    fun deleteInvalidTest(invalidId: String) {
        When {
            delete("/{id}", invalidId)
        } Then {
            statusCode(404)
        }
    }

    @Test
    @Order(24)
    fun deleteValidTest() {
        When {
            delete("/{id}", history.id)
        } Then {
            statusCode(204)
        }
        count--
    }

    @Test
    @Order(25)
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
    @Order(26)
    fun deleteAlreadyDeletedTest() {
        When {
            delete("/{id}", history.id)
        } Then {
            statusCode(404)
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
            body("size()", equalTo(count))
        }
    }
}
