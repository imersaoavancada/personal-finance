package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.Account
import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class AccountRepository : PanacheRepositoryBase<Account, Long> {
    private val searchQuery = "LOWER(name) LIKE ?1"

    fun count(term: String?): Long = query(term).count()

    fun list(
        page: Int,
        size: Int,
        term: String?,
    ): List<Account> = query(term).page(page, size).list()

    private fun query(term: String?): PanacheQuery<Account> =
        if (term.isNullOrBlank()) {
            findAll()
        } else {
            find(searchQuery, "%${term.lowercase()}%")
        }
}
