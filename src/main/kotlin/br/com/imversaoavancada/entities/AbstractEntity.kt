package br.com.imversaoavancada.entities

import jakarta.persistence.*

/**
 * @author Eduardo Folly
 */
@MappedSuperclass
abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    fun toMap(): Map<String, Any?> = mapOf("id" to id?.toInt())
}
