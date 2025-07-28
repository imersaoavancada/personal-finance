package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.Provision
import br.com.imversaoavancada.infra.exceptions.IdNotFoundException
import br.com.imversaoavancada.infra.repositories.ProvisionRepository
import jakarta.enterprise.context.ApplicationScoped
import jakarta.transaction.Transactional

/**
 * @author William Braziellas
 */
@ApplicationScoped
class ProvisionService(
    val repository: ProvisionRepository,
) {
    fun count(term: String?): Long = repository.count(term)

    fun listAll(
        page: Int,
        size: Int,
        term: String?,
    ): List<Provision> = repository.list(page, size, term)

    fun getById(id: Long): Provision =
        repository.findById(id)
            ?: throw IdNotFoundException(id, Provision::class)

    @Transactional
    fun create(provision: Provision): Provision {
        repository.persist(provision)
        return provision
    }

    @Transactional
    fun update(
        id: Long,
        provision: Provision,
    ): Provision {
        val persisted = getById(id)

        persisted.name = provision.name
        persisted.initialDate = provision.initialDate
        persisted.finalDate = provision.finalDate
        persisted.amount = provision.amount

        repository.persist(persisted)

        return persisted
    }

    @Transactional
    fun delete(id: Long) = repository.delete(getById(id))
}
