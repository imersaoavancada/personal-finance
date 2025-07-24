package br.com.imversaoavancada.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.annotations.ColumnDefault
import java.time.Instant

@Entity
@Table(name = "Provisions")
class Provision : AbstractFullEntity() {
    @Column(name = "Name", nullable = false)
    @NotBlank(message = "not_blank")
    @Size(min = 1, max = 255, message = "size_between:{min:{max}")
    var name: String? = null

    @NotNull(message = "not_null")
    @ColumnDefault("0")
    @Column(nullable = false)
    var amount: Int? = null

    @NotNull(message = "not_null")
    @Column(name = "initial_date", nullable = false)
    var initialDate: Instant? = null

    @NotNull(message = "not_null")
    @Column(name = "final_date", nullable = true)
    var finalDate: Instant? = null

    // TODO : include tags to this entity

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "name" to name,
                "initialDate" to initialDate.toString(),
                "finalDate" to finalDate.toString(),
                "amount" to amount,
            )
}
