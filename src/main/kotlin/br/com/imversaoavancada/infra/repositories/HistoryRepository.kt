package br.com.imversaoavancada.infra.repositories

import br.com.imversaoavancada.entities.History
import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepositoryBase
import jakarta.enterprise.context.ApplicationScoped

/**
 * @author Eduardo Folly
 */
@ApplicationScoped
class HistoryRepository : PanacheRepositoryBase<History, Long>
