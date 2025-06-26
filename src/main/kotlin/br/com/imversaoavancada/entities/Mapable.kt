package br.com.imversaoavancada.entities

/**
 * @author Eduardo Folly
 */
interface Mapable {
    fun toMap(): Map<String, Any?>
}
