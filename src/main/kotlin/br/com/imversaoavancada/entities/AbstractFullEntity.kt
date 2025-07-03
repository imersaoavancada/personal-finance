package br.com.imversaoavancada.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import org.hibernate.annotations.*
import java.time.OffsetDateTime

/**
 * @author Eduardo Folly
 */
@MappedSuperclass
abstract class AbstractFullEntity : AbstractEntity() {
    @ColumnDefault("NOW()")
    @CreationTimestamp(source = SourceType.DB)
    @Column(name = "created_at", nullable = false)
    var createdAt: OffsetDateTime? = null

    @ColumnDefault("NOW()")
    @UpdateTimestamp(source = SourceType.DB)
    @Column(name = "updated_at", nullable = false)
    var updatedAt: OffsetDateTime? = null

    @JsonIgnore
    @Column(name = "deleted_at")
    @Suppress("unused")
    var deletedAt: OffsetDateTime? = null

    override fun toMap(): Map<String, Any?> =
        super.toMap() +
            mapOf(
                "createdAt" to createdAt.toString(),
                "updatedAt" to updatedAt.toString(),
            )
}
