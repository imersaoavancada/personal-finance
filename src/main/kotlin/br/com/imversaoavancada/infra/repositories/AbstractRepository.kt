package br.com.imversaoavancada.infra.repositories

import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase

/**
 * @author Eduardo Folly
 */
abstract class AbstractRepository<E : Any> :
    PanacheRepositoryBase<E, Long> {
    abstract val searchQuery: String

    fun count(term: String?): Long = query(term).count()

    fun list(
        page: Int,
        size: Int,
        term: String?,
    ): List<E> = query(term).page(page, size).list()

    private fun query(term: String?): PanacheQuery<E> =
        if (term.isNullOrBlank()) {
            findAll()
        } else {
            find(searchQuery, "%${term.lowercase()}%")
        }
}
