package br.com.imversaoavancada.controllers

import br.com.imversaoavancada.entities.Tag
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

@Suppress("ktlint:standard:no-consecutive-comments")
/*
 * @author Douglas O. Luciano
 */
@QuarkusTest
@TestHTTPEndpoint(TagController::class)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class TagControllerTest {
    // Create Maps
    val nameNotBlankError =
        mapOf(
            "field" to "create.body.name",
            "message" to "not_blank",
        )

    val colorNotBlankError =
        mapOf(
            "field" to "create.body.color",
            "message" to "not_blank",
        )

    val nameSizeBetweenError =
        mapOf(
            "field" to "create.body.name",
            "message" to "size_between:1:255",
        )

    val uniqueNameError =
        mapOf(
            "field" to "tags_name_key",
            "message" to "constraint_violation_exception",
        )

    // Update Maps
    val updateNameNotBlankError =
        mapOf(
            "field" to "update.body.name",
            "message" to "not_blank",
        )

    val updateColorNotBlankError =
        mapOf(
            "field" to "update.body.color",
            "message" to "not_blank",
        )

    val updateNameSizeBetweenError =
        mapOf(
            "field" to "update.body.name",
            "message" to "size_between:1:255",
        )

    companion object {
        var count = 2
        var invalidId = -1
        lateinit var tag: Tag

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
                hasItems(nameNotBlankError, colorNotBlankError),
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
                hasItems(nameNotBlankError, colorNotBlankError),
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
                    "code" to "",
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
                    colorNotBlankError,
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
                    "name" to " ",
                    "color" to " ",
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
                hasItems(
                    colorNotBlankError,
                    nameNotBlankError,
                ),
            )
        }
    }
    // TODO: How can I get a wrong value on integer field color?
    /*
    @Test
    @Order(9)
    fun insertWrongValuesTest() {
        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "color" to "A".repeat(4),
                    "name" to "A".repeat(300),
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
                hasItems(
                    insertWrongColorTypeTest,
                    nameSizeBetweenError,
                ),
            )
        }
    }
     */

    @Test
    @Order(10)
    fun insertSuccessTest() {
        val color = "8".repeat(5).toInt()
        val name = "A".repeat(150)

        tag = Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "color" to color,
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
                "color",
                equalTo(color),
                "name",
                equalTo(name),
            )
        } Extract {
            body().parse(Tag::class)
        }

        count++
    }

    @Test
    @Order(11)
    fun insertDuplicatedTest() {
        val color = "8".repeat(3).toInt()
        val name = "A".repeat(150)

        Given {
            contentType(ContentType.JSON)
            body(
                mapOf<String, Any?>(
                    "color" to color,
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
                hasItem(uniqueNameError),
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
            get("/{id}", tag.id)
        } Then {
            statusCode(200)
            body("$", equalTo(tag.toMap()))
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
                    "color" to "123",
                    "name" to "Tag Teste",
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
            put("/{id}", tag.id)
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
            put("/{id}", tag.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(2),
                "violations",
                hasItems(updateNameNotBlankError, updateColorNotBlankError),
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
                    "color" to null,
                    "name" to null,
                ),
            )
        } When {
            put("/{id}", tag.id)
        } Then {
            statusCode(400)
            contentType(ContentType.JSON)
            body(
                "status",
                equalTo(400),
                "violations.size()",
                equalTo(2),
                "violations",
                hasItems(updateNameNotBlankError, updateColorNotBlankError),
            )
        }
    }

    @ParameterizedTest
    // TODO: Como capturar ABC, 12A e @#$ no ValueSource?
    @NullAndEmptySource
    @ValueSource(strings = [" ", "-1"])
    @Order(18)
    fun updateInvalidColorTest(invalidColor: String?) {
        Given {
            contentType(ContentType.JSON)
            body(mapOf("color" to invalidColor, "name" to "Tag Atualizada"))
        } When {
            put("/{id}", tag.id)
        } Then {
            statusCode(400)
            body(
                "violations.field",
                hasItem(containsString("update.body.color")),
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
                    "color" to 123,
                    "name" to "A".repeat(256),
                ),
            )
        } When {
            put("/{id}", tag.id)
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
        val color = "7".repeat(3).toInt()
        val name = "B".repeat(150)

        tag =
            Given {
                contentType(ContentType.JSON)
                body(
                    mapOf<String, Any?>(
                        "color" to color,
                        "name" to name,
                    ),
                )
            } When {
                put("/{id}", tag.id)
            } Then {
                statusCode(200)
                contentType(ContentType.JSON)
                body(
                    "id",
                    equalTo(tag.id?.toInt()),
                    "color",
                    equalTo(color),
                    "name",
                    equalTo(name),
                )
            } Extract {
                body().parse(Tag::class)
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
            get("/{id}", tag.id)
        } Then {
            statusCode(200)
            contentType(ContentType.JSON)
            body("$", equalTo(tag.toMap()))
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
            delete("/{id}", tag.id)
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
            delete("/{id}", tag.id)
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
