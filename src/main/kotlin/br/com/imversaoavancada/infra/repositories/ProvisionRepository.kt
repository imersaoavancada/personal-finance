package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.Provision
import jakarta.enterprise.context.ApplicationScoped

@ApplicationScoped
class ProvisionRepository : AbstractRepository<Provision>() {
    override val searchQuery = ""
}
