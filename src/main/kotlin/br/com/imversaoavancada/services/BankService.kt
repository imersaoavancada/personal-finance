package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.Bank
import br.com.imversaoavancada.repositories.BankRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class BankService(
    val repository: BankRepository,
) {
    fun listAll(): List<Bank> = repository.listAll()

    @Transactional
    fun create(bank: Bank): Bank {
        repository.persist(bank)
        return bank
    }
}
