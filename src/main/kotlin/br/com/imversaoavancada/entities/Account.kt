package br.com.imversaoavancada.entities

import br.com.imversaoavancada.enums.AccountType
import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction

/**
 * @author Eduardo Folly
 */
@Entity
@Table(name = "accounts")
@SQLDelete(sql = "UPDATE accounts SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at IS NULL")
class Account : AbstractFullEntity() {
    @NotBlank(message = "not_blank")
    @Size(min = 1, max = 255, message = "size_between:{min}:{max}")
    @Column(length = 255, nullable = false)
    var name: String? = null

    @NotNull(message = "not_null")
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    var type: AccountType? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id", nullable = true)
    var bank: Bank? = null

    @Size(min = 1, max = 255, message = "size_between:{min}:{max}")
    @Column(length = 255, nullable = true)
    var branch: String? = null

    @Size(min = 1, max = 255, message = "size_between:{min}:{max}")
    @Column(name = "account_number", length = 255, nullable = true)
    var number: String? = null

    @ColumnDefault("0")
    @PositiveOrZero(message = "positive_or_zero")
    @Column(name = "credit_limit")
    var creditLimit: Int = 0

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "name" to name,
                "type" to type?.name,
                "bank" to bank?.toMap(),
                "branch" to branch,
                "number" to number,
                "creditLimit" to creditLimit,
            )
}
