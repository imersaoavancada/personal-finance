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
import org.junit.jupiter.params.provider.NullAndEmptySource
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

    val uniqueCodeError =
        mapOf(
            "field" to "banks_code_key",
            "message" to "constraint_violation_exception",
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

//    val updateCodeOnlyNumbersError =
//        mapOf(
//            "field" to "update.body.code",
//            "message" to "only_numbers",
//        )

//    val updateCodeSizeEqualError =
//        mapOf(
//            "field" to "update.body.code",
//            "message" to "size_equal:3",
//        )

    val updateNameSizeBetweenError =
        mapOf(
            "field" to "update.body.name",
            "message" to "size_between:1:150",
        )

    companion object {
        var count = 2
        var invalidId = -1
        lateinit var bank: Bank

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
                equalTo(2),
                "violations",
                hasItems(nameNotBlankError, codeNotBlankError),
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
    @Order(7)
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
    @Order(8)
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
    @Order(9)
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
    @Order(10)
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

    // https://github.com/imersaoavancada/personal-finance/issues/8
    @Test
    @Order(11)
    fun insertDuplicatedTest() {
        val code = "8".repeat(3)
        val name = "A".repeat(150)

        Given {
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
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(1),
                "violations",
                hasItem(uniqueCodeError),
            )
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
    @Order(17)
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
    // TODO: Pode melhorar!
    @NullAndEmptySource
    @ValueSource(strings = [" ", "ABC", "12A", "@#$", "1234", "12"])
    @Order(18)
    fun updateInvalidCodeTest(invalidCode: String?) {
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
    @Order(19)
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
    @Order(20)
    fun updateSuccessTest() {
        val code = "7".repeat(3)
        val name = "B".repeat(150)

        bank =
            Given {
                contentType(ContentType.JSON)
                body(
                    mapOf<String, Any?>(
                        "code" to code,
                        "name" to name,
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
                    equalTo(code),
                    "name",
                    equalTo(name),
                )
            } Extract {
                body().parse(Bank::class)
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
            get("/{id}", bank.id)
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("$", equalTo(bank.toMap()))
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
            delete("/{id}", bank.id)
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
            delete("/{id}", bank.id)
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
