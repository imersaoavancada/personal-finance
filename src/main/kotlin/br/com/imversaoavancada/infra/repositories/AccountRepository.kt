package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.Account
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class AccountRepository : PanacheRepositoryBase<Account, Long>
