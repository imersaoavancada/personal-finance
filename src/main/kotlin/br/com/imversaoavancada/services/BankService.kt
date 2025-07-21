package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.Bank
import br.com.imversaoavancada.infra.exceptions.IdNotFoundException
import br.com.imversaoavancada.infra.repositories.BankRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class BankService(
    val repository: BankRepository,
) {
    fun count(term: String?): Long = repository.count(term)

    fun listAll(
        page: Int,
        size: Int,
        term: String?,
    ): List<Bank> = repository.list(page, size, term)

    fun getById(id: Long): Bank =
        repository.findById(id)
            ?: throw IdNotFoundException(id, Bank::class)

    @Transactional
    fun create(bank: Bank): Bank {
        repository.persist(bank)
        return bank
    }

    @Transactional
    fun update(
        id: Long,
        bank: Bank,
    ): Bank {
        val persisted = getById(id)

        persisted.code = bank.code
        persisted.name = bank.name

        repository.persist(persisted)

        return persisted
    }

    @Transactional
    fun delete(id: Long) = repository.delete(getById(id))
}
