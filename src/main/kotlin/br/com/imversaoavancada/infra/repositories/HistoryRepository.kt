package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.History
import br.com.imversaoavancada.projections.HistoryListProjection
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class HistoryRepository : PanacheRepositoryBase<History, Long> {
    private val searchQuery = "LOWER(history.name) LIKE ?1"

    fun count(term: String?): Long =
        if (term.isNullOrBlank()) {
            findAll()
        } else {
            find(searchQuery, "%${term.lowercase()}%")
        }.count()

    fun list(
        page: Int,
        size: Int,
        term: String?,
    ): List<HistoryListProjection> =
        """
        FROM History history 
        LEFT JOIN history.account account 
        LEFT JOIN account.bank
        """.trimIndent()
            .let { hql ->
                if (term.isNullOrBlank()) {
                    find(hql)
                } else {
                    find(
                        "$hql WHERE $searchQuery",
                        "%${term.lowercase()}%",
                    )
                }
            }.page(page, size)
            .project(HistoryListProjection::class.java)
            .list()
}
