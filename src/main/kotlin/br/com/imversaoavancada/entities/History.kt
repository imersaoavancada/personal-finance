package br.com.imversaoavancada.entities

import jakarta.persistence.*
import jakarta.validation.constraints.*
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.SQLRestriction
import java.time.Instant

/**
 * @author Eduardo Folly
 */
@Entity
@Table(name = "histories")
@SQLDelete(sql = "UPDATE histories SET deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted_at = '1970-01-01 00:00:00+00'")
class History : AbstractFullEntity() {
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(length = 255, nullable = false)
    var name: String? = null

    // TODO: Provision

    // TODO: Provision Date

    @NotNull
    @Column(name = "payment_date", nullable = false)
    var paymentDate: Instant? = null

    @ManyToMany(fetch = FetchType.EAGER, targetEntity = Tag::class)
    @JoinTable(
        name = "histories_tags",
        joinColumns = [JoinColumn("history_id")],
        inverseJoinColumns = [JoinColumn("tag_id")],
    )
    var tags: MutableSet<Tag>? = mutableSetOf()

    @NotNull
    @ColumnDefault("0")
    @Column(nullable = false)
    var amount: Int? = null

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id", nullable = true)
    var account: Account? = null

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "name" to name,
                "paymentDate" to paymentDate.toString(),
                "amount" to amount,
                "account" to account?.toMap(),
                "tags" to tags?.map { it.toMap() },
            )
}
