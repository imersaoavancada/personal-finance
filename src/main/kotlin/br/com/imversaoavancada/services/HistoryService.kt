package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.History
import br.com.imversaoavancada.infra.repositories.HistoryRepository
import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class HistoryService(
    val repository: HistoryRepository,
) {
    fun count(term: String?): Long = query(term).count()

    fun listAll(
        page: Int,
        size: Int,
        term: String?,
    ): List<History> = query(term).page(page, size).list()

    private fun query(term: String?): PanacheQuery<History> {
        if (term.isNullOrBlank()) {
            return repository.findAll()
        }

        return repository
            .find(
                "LOWER(name) LIKE ?1",
                "%${term.lowercase()}%",
            )
    }

    fun getById(id: Long): History =
        repository.findById(id)
            ?: throw NotFoundException() // TODO: Create a error object.

    @Transactional
    fun create(history: History): History {
        repository.persist(history)
        return history
    }

    @Transactional
    fun update(
        id: Long,
        history: History,
    ): History {
        val persisted = getById(id)

        persisted.name = history.name
        persisted.paymentDate = history.paymentDate
        persisted.amount = history.amount

        repository.persist(persisted)

        return persisted
    }

    @Transactional
    fun delete(id: Long) {
        repository.delete(getById(id))
    }
}
