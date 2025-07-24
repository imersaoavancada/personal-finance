package br.com.imversaoavancada.services

import br.com.imversaoavancada.entities.Provision
import br.com.imversaoavancada.infra.exceptions.IdNotFoundException
import br.com.imversaoavancada.infra.repositories.ProvisionRepository

class ProvisionService(
    val repository: ProvisionRepository,
) {
    fun getById(id: Long): Provision =
        repository.findById(id) ?: throw IdNotFoundException(
            id,
            Provision::class,
        )

    fun create(provision: Provision): Provision {
        repository.persist(provision)
        return provision
    }
}
