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
@Table(name = "banks")
@SQLDelete(sql = "UPDATE banks SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class Bank : AbstractFullEntity() {
    @NotBlank(message = "not_blank")
    @Size(min = 3, max = 3, message = "size_equal:{min}")
    @Pattern(regexp = "\\d{3}", message = "only_numbers")
    @Column(length = 3, nullable = false, unique = true)
    var code: String? = null

    @NotBlank(message = "not_blank")
    @Size(min = 1, max = 150, message = "size_between:{min}:{max}")
    @Column(length = 150, nullable = false)
    var name: String? = null

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "code" to code,
                "name" to name,
            )
}
