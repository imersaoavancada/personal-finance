package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.Provision
import jakarta.enterprise.context.ApplicationScoped

/**
 * @author William Braziellas
 */
@ApplicationScoped
class ProvisionRepository : AbstractRepository<Provision>() {
    override val searchQuery = "LOWER(name) LIKE ?1"
}
