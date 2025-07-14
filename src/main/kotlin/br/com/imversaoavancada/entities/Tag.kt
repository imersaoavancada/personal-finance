package br.com.imversaoavancada.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * @author Douglas O. Luciano
 */

@Entity
@Table(name = "tags")
@SQLDelete(sql = "UPDATE tags SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at is NULL")
class Tag : AbstractFullEntity() {
    @NotBlank(message = "not_blank")
    @Size(
        min = 1,
        max = 255,
        message = "size_between:{min}:{max}",
    )
    @Column(length = 255, nullable = false, unique = true)
    var name: String? = null

    @NotNull(message = "not_blank")
    @PositiveOrZero(message = "positive_or_zero")
    var color: Int? = null

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "name" to name,
                "color" to color,
            )
}
