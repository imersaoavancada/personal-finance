package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.Bank
import br.com.imversaoavancada.infra.repositories.BankRepository
import io.quarkus.hibernate.orm.panache.kotlin.PanacheQuery
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
    fun count(term: String?): Long = query(term).count()

    fun listAll(
        page: Int,
        size: Int,
        term: String?,
    ): List<Bank> = query(term).page(page, size).list()

    private fun query(term: String?): PanacheQuery<Bank> {
        if (term.isNullOrBlank()) {
            return repository.findAll()
        }

        return repository
            .find(
                "LOWER(code) LIKE ?1 OR LOWER(name) LIKE ?1",
                "%${term.lowercase()}%",
            )
    }

    fun getById(id: Long): Bank =
        repository.findById(id)
            ?: throw NotFoundException() // TODO: Create a error object.

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
