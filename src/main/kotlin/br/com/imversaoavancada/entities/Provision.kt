package br.com.imversaoavancada.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.Instant

/**
 * @author William Braziellas
 */
@Entity
@Table(name = "provisions")
@SQLDelete(sql = "UPDATE provisions SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at = '1970-01-01 00:00:00+00'")
class Provision : AbstractFullEntity() {
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "name", nullable = false)
    var name: String? = null

    @NotNull
    @Column(name = "initial_date", nullable = false)
    var initialDate: Instant? = null

    @Column(name = "final_date", nullable = true)
    var finalDate: Instant? = null

    @NotNull
    @ColumnDefault("0")
    @Column(nullable = false)
    var amount: Int? = null

    // TODO : include tags to this entity

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "name" to name,
                "initialDate" to initialDate?.toString(),
                "finalDate" to finalDate?.toString(),
                "amount" to amount,
            )
}
