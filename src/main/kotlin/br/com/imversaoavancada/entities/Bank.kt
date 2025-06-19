package br.com.imversaoavancada.entities

import jakarta.persistence.*

/**
 * @author Eduardo Folly
 */
@Entity
@Table(name = "banks")
class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var code: String? = null
    var name: String? = null
}
