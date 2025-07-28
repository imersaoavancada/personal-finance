package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.Account
import br.com.imversaoavancada.infra.exceptions.IdNotFoundException
import br.com.imversaoavancada.infra.repositories.AccountRepository
import br.com.imversaoavancada.infra.repositories.BankRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class AccountService(
    val repository: AccountRepository,
    val bankRepository: BankRepository,
) {
    fun count(term: String?): Long = repository.count(term)

    fun listAll(
        page: Int,
        size: Int,
        term: String?,
    ): List<Account> = repository.list(page, size, term)

    fun getById(id: Long): Account =
        repository.findById(id)
            ?: throw IdNotFoundException(id, Account::class)

    @Transactional
    fun create(account: Account): Account {
        account.bank =
            account.bank?.let { bank ->
                bank.id?.let { bankRepository.findById(it) }
                    ?: throw IdNotFoundException(bank.id, bank::class)
            }

        repository.persist(account)
        return account
    }

    @Transactional
    fun update(
        id: Long,
        account: Account,
    ): Account {
        val persisted = getById(id)

        persisted.bank =
            account.bank?.let { bank ->
                bank.id?.let { bankRepository.findById(it) }
                    ?: throw IdNotFoundException(bank.id, bank::class)
            }

        persisted.name = account.name
        persisted.type = account.type
        persisted.branch = account.branch
        persisted.number = account.number
        persisted.creditLimit = account.creditLimit

        repository.persist(persisted)

        return persisted
    }

    @Transactional
    fun delete(id: Long) = repository.delete(getById(id))
}
