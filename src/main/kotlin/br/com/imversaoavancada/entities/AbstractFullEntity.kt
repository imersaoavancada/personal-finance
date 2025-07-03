package br.com.imversaoavancada.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.*
import java.time.Instant

/**
 * @author Eduardo Folly
 */
@MappedSuperclass
abstract class AbstractFullEntity : AbstractEntity() {
    @ColumnDefault("NOW()")
    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at", nullable = false)
    var createdAt: Instant? = null

    @ColumnDefault("NOW()")
    @UpdateTimestamp(source = SourceType.DB)
    @Column(name = "updated_at", nullable = false)
    var updatedAt: Instant? = null

    @JsonIgnore
    @Column(name = "deleted_at")
    @Suppress("unused")
    var deletedAt: Instant? = null

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "createdAt" to createdAt.toString(),
                "updatedAt" to updatedAt.toString(),
            )
}
