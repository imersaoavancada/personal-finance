package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.Bank
import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class BankRepository : PanacheRepositoryBase<Bank, Long> {
    private val searchQuery = "LOWER(code) LIKE ?1 OR LOWER(name) LIKE ?1"

    fun findByCode(code: String): Bank? = find("code", code).firstResult()

    fun count(term: String?): Long = query(term).count()

    fun list(
        page: Int,
        size: Int,
        term: String?,
    ): List<Bank> = query(term).page(page, size).list()

    private fun query(term: String?): PanacheQuery<Bank> =
        if (term.isNullOrBlank()) {
            findAll()
        } else {
            find(searchQuery, "%${term.lowercase()}%")
        }
}
