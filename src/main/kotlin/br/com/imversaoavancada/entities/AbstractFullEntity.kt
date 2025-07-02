package br.com.imversaoavancada.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.ColumnDefault
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import java.util.Date

/**
 * @author Eduardo Folly
 */
@MappedSuperclass
abstract class AbstractFullEntity : AbstractEntity() {
    @CreationTimestamp
    @Column(
        name = "created_at",
        nullable = false,
        columnDefinition = "timestamp with time zone",
    )
    @ColumnDefault("NOW()")
    var createdAt: Date? = null

    @UpdateTimestamp
    @Column(
        name = "updated_at",
        nullable = false,
        columnDefinition = "timestamp with time zone",
    )
    @ColumnDefault("NOW()")
    var updatedAt: Date? = null

    @JsonIgnore
    @Column(
        name = "deleted_at",
        nullable = false,
        columnDefinition = "timestamp with time zone",
    )
    @ColumnDefault("'1970-01-01 00:00:00+00:00'")
    var deletedAt: Date = Date(0)

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "createdAt" to createdAt?.time,
                "updatedAt" to updatedAt?.time,
            )
}
