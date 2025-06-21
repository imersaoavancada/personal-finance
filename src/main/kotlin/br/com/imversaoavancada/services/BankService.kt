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

    fun getById(id: Long) : Bank{
        return repository.findById(id)
            ?: throw NotFoundException("Bank ID #$id not found!")
    }

    @Transactional
    fun create(bank: Bank): Bank {
        repository.persist(bank)
        return bank
    }

    @Transactional
    fun update(id: Long, updatedBank: Bank): Bank {
        val existingBank = repository.findById(id)
            ?: throw NotFoundException("Bank ID #$id not found!")
        existingBank.code = updatedBank.code
        existingBank.name = updatedBank.name

        repository.persist(existingBank)
        return existingBank
    }

    @Transactional
    fun delete(id: Long){
        val bankToBeDeleted = getById(id)
        repository.delete(bankToBeDeleted)
    }
}
