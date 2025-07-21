package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.History
import br.com.imversaoavancada.infra.exceptions.IdNotFoundException
import br.com.imversaoavancada.infra.repositories.AccountRepository
import br.com.imversaoavancada.infra.repositories.HistoryRepository
import br.com.imversaoavancada.projections.HistoryListProjection
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class HistoryService(
    val repository: HistoryRepository,
    val accountRepository: AccountRepository,
) {
    fun count(term: String?): Long = repository.count(term)

    fun listAll(
        page: Int,
        size: Int,
        term: String?,
    ): List<HistoryListProjection> = repository.list(page, size, term)

    fun getById(id: Long): History =
        repository.findById(id)
            ?: throw IdNotFoundException(id, History::class)

    @Transactional
    fun create(history: History): History {
        history.account =
            history.account?.let { account ->
                account.id?.let { accountRepository.findById(it) }
                    ?: throw IdNotFoundException(account.id, account::class)
            }

        repository.persist(history)
        return history
    }

    @Transactional
    fun update(
        id: Long,
        history: History,
    ): History {
        val persisted = getById(id)

        persisted.account =
            history.account?.let { account ->
                account.id?.let { accountRepository.findById(it) }
                    ?: throw IdNotFoundException(account.id, account::class)
            }

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
