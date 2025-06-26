package br.com.imversaoavancada.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

/**
 * @author Eduardo Folly
 */
@Entity
@Table(name = "banks")
class Bank : Mapable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @NotBlank(message = "not_blank")
    @Size(min = 3, max = 3, message = "size_equal:{min}")
    @Pattern(regexp = "\\d{3}", message = "only_numbers")
    var code: String? = null

    @NotBlank(message = "not_blank")
    @Size(min = 1, max = 150, message = "size_between:{min}:{max}")
    var name: String? = null

    override fun toMap(): Map<String, Any?> =
        mapOf(
            "id" to id?.toInt(),
            "code" to code,
            "name" to name,
        )
}
