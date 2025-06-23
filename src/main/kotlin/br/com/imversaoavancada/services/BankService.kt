package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.Bank
import br.com.imversaoavancada.repositories.BankRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional
import jakarta.ws.rs.NotFoundException

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class BankService(
    val repository: BankRepository,
) {
    fun listAll(): List<Bank> = repository.listAll()

    fun getById(id: Long): Bank =
        repository.findById(id)
            ?: throw NotFoundException("Bank ID #$id not found!")

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
    fun delete(id: Long) {
        repository.delete(getById(id))
    }
}
