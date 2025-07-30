package br.com.imversaoavancada.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * @author Eduardo Folly
 */
@Entity
@Table(
    name = "banks",
    indexes = [
        Index(
            name = "banks_code_unq",
            unique = true,
            columnList = "code, deleted_at",
        ),
    ],
)
@SQLDelete(sql = "UPDATE banks SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at = '1970-01-01 00:00:00+00'")
class Bank : AbstractFullEntity() {
    @NotBlank
    @Size(min = 3, max = 3)
    @Pattern(regexp = "\\d{3}", message = "only_numbers")
    @Column(length = 3, nullable = false)
    var code: String? = null

    @NotBlank
    @Size(min = 1, max = 150)
    @Column(length = 150, nullable = false)
    var name: String? = null

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "code" to code,
                "name" to name,
            )
}
