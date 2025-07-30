package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.Account
import jakarta.enterprise.context.ApplicationScoped

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class AccountRepository : AbstractRepository<Account>() {
    override val searchQuery = "LOWER(name) LIKE ?1"
}
