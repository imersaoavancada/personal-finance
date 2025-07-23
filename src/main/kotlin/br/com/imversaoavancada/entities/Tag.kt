package br.com.imversaoavancada.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.*
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * @author Douglas O. Luciano
 */
@Entity
@Table(name = "tags")
@SQLDelete(sql = "UPDATE tags SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at = '1970-01-01 00:00:00+00'")
class Tag : AbstractFullEntity() {
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(length = 255, nullable = false, unique = true)
    var name: String? = null

    @NotNull
    @Min(0x00000000)
    @Max(0xFFFFFFFF)
    @Column(name = "color", nullable = false)
    var color: Long? = null

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "name" to name,
                "color" to color,
            )
}
