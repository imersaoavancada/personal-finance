package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.Bank
import jakarta.enterprise.context.ApplicationScoped

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class BankRepository : AbstractRepository<Bank>() {
    override val searchQuery = "LOWER(code) LIKE ?1 OR LOWER(name) LIKE ?1"

    fun findByCode(code: String): Bank? = find("code", code).firstResult()
}
